package com.spark.logparse.serviceImp

import com.ggstar.util.ip.IpHelper
import com.spark.logparse.service.CleanData
import com.spark.logparse.utils.DateConvertUtil
import org.apache.commons.lang3.StringUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}

import scala.util.matching.Regex

object CleanDataImp extends CleanData{

  /**
    * 1.获取我们需要的字段记录
    * 2.过滤掉url不符的记录
    */
  def dataClean(data:RDD[String]): RDD[String] ={
    val dataClean = data.map(line => {
      //把每条记录按照空格进行拆分，获取一个columns
      val columns = line.split(" ")

      //获取浏览内容的网址
      val url = columns(11).replace("\"","")//去掉两边的双引号

      //定义需要的数据模型
      val reg1 = new Regex("http://www.imooc.com/video/[0-9]{1,}")
      val reg2 = new Regex("http://www.imooc.com/article/[0-9]{1,}")

      //将url不符的记录标记为NULL
      if (url.equals("-")
        || reg1.findAllIn(url).isEmpty
        && reg2.findAllIn(url).isEmpty
      ){
        "NULL"
      }else{
        //获取IP
        val ip = columns(0)
        //获取时间
        val dateString = columns(3) + " " + columns(4)
        val date = DateConvertUtil.dateFormatConvert(dateString.substring(1,dateString.length-1))//去掉中括号

        //获取浏览
        val traffic = columns(9)

        date +"\t"+ url +"\t"+traffic+"\t"+ ip
      }
    })
    //过滤掉记录是NULL的日志
    dataClean.filter(!_.contains("NULL"))
  }


  /**
    * 1.将RDD数据转化成DataFrame
    * 2.把输入的数据解析成我们需要的字段
    */
  def dataFormatConvert(spark:SparkSession,data:RDD[String]): DataFrame = {

    //定义输出结构数据
    val schema = StructType(Array(
      StructField("url": String, StringType: DataType, nullable = true),
      StructField("cmsType": String, StringType: DataType, nullable = true),
      StructField("cmsID": String, LongType: DataType, nullable = true),
      StructField("traffic": String, LongType: DataType, nullable = true),
      StructField("ip": String, StringType: DataType, nullable = true),
      StructField("city": String, StringType: DataType, nullable = true),
      StructField("date": String, StringType: DataType, nullable = true),
      StructField("time": String, StringType: DataType, nullable = true)
    ))

    //准备输出的RDD数据
    val rowRDD = data.map(record => handleOutputData(record))

    //最后生成DataFrame类型的数据
    spark.createDataFrame(rowRDD,schema)

    //df.show(100,false)
  }

  /**
    * 解析每条记录，并将其转换成输出的RDD
    * @param record：每条记录
    */
  def handleOutputData(record:String): Row ={
    val columns = record.split("\t")

    val url = columns(1)
    val traffic = columns(2)
    var traffic_Long = 0l
    //判断traffic是否为数字型的字符串，是就转换成Long类型
    if (StringUtils.isNumeric(traffic)){
      traffic_Long = traffic.toLong
    }
    val ip = columns(3)

    //获取cms
    val cms = url.split("/")
    val cmsType = cms(3)
    var cmsID = cms(4)
    //4779?from=itblog避免遇到这样的情况出现
    if(cmsID.contains("?")){
      cmsID = cmsID.substring(0,cmsID.indexOf("?"))
    }

    var cmsID_Long = 0l
    //判断cmsID是否为数字型的字符串，是就转换成Long类型
    if (StringUtils.isNumeric(cmsID)){
      cmsID_Long = cmsID.toLong
    }

    //获取时间
    val date_time = columns(0).split(" ")
    val date = date_time(0).replaceAll("-","")//去掉"-"
    val time = date_time(1)

    //ip地址解析通过IpHelper类中的indRegionByIp()
    val city = IpHelper.findRegionByIp(ip)

    //返回每条Row记录
    Row(url,cmsType,cmsID_Long,traffic_Long,ip,city,date,time)
  }
}
