package com.spark.logparse

import com.spark.logparse.serviceImp.CountAccessTrafficImp
import com.spark.logparse.serviceImp.CleanDataImp
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}


/**
  * 项目要求：
  * 1.统计imooc主站所有的课程按访问的次数进行倒序排列
  * 2.按每日统计imooc主站最受欢迎的课程以及访问的次数
  * 3.按每日，地域统计imooc主站最受欢迎的课程以及访问的次数
  * 4.按每日，流量统计imooc主站最受欢迎的课程以及访问的次数
  */
object LogParseApp {
  def main(args: Array[String]): Unit = {

    var spark: SparkSession = null
    try{
      //获取spark对象
      spark = SparkSession.builder().appName("LogParseApp")
        //设置数据类型手动指定，无需自动转换
        .config("spark.sql.sources.partitionColumnTypeInference.enabled",value = false)
        .master("local[2]").getOrCreate()


      /*//数据清洗
      val access = spark.sparkContext.textFile("file:///home/sunzb/Documents/access/access20161111.log")
      val accessRDD = CleanDataImp.dataClean(access)
      //数据写入本地
      dataRDD.saveAsTextFile("file:///home/sunzb/Documents/access/access20161111_firstClean.log")*/


      /*//数据处理，RDD => DataFrame
      val accessRDD = spark.sparkContext.textFile("file:///home/sunzb/Documents/access/access20161111_firstClean.log/part-00*")
      //格式转换
      val accessDF = CleanDataImp.dataFormatConvert(spark,accessRDD)
      //数据写入本地
      accessDF.coalesce(1).write.format("parquet").partitionBy("date").mode(SaveMode.Overwrite)
        .save("/home/sunzb/Documents/access/access20161111_finalClean")
      /**
        * 1.coalesce():设置写入文件的个数，参数是int类型，本次设置为1
        * 2.partitionBy():分区设置，参数是数据结构的字段
        * 3.mode():写入模式的设置，参数有以下几种：
        *   `SaveMode.Overwrite`: overwrite the existing data.
        *   `SaveMode.Append`: append the data.
        *   `SaveMode.Ignore`: ignore the operation (i.e. no-op).
        *   `SaveMode.ErrorIfExists`: default option, throw an exception at runtime.
        */*/

      //功能实现
      val accessDF = spark.read.parquet("/home/sunzb/Documents/access/access20161111_finalClean")
      //统计imooc主站课程按访问的次数进行倒序排列
      //CountAccessTrafficImp.CountVideoAccessTraffic(spark,accessDF)
      //按每日统计imooc主站最受欢迎的课程以及访问的次数
      //CountAccessTrafficImp.CountEveryDayVideoAccessTrafficTop5(spark,accessDF)
      //按每日，地域统计imooc主站最受欢迎的课程以及访问的次数
      //CountAccessTrafficImp.CountEveryDayAndCityVideoAccessTrafficTop5(spark,accessDF)
      //按每日，流量统计imooc主站最受欢迎的课程以及访问的次数
      CountAccessTrafficImp.CountEveryDayAndTrafficsTop5(spark,accessDF)



      //数据可视化
    }catch {
      case e:Exception =>
        println(e)
    }finally {
      if (spark != null) {
        spark.stop()
      }
    }
  }
}
