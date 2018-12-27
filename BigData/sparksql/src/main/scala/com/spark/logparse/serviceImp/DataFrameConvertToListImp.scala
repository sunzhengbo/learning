package com.spark.logparse.serviceImp

import com.spark.logparse.bean.VideoAccess
import com.spark.logparse.daoImp.DatabaseStoreImp
import com.spark.logparse.service.DataFrameConvertToList
import com.spark.logparse.bean.VideoAccess
import com.spark.logparse.daoImp.DatabaseStoreImp
import com.spark.logparse.service.DataFrameConvertToList
import org.apache.spark.sql.DataFrame

import scala.collection.mutable.ListBuffer

object DataFrameConvertToListImp extends DataFrameConvertToList{
  /**
    * 将DataFrame转化成VideoAccess对象，并存储在到数据库中
    */
  def dataFrameConvertToList(videoAccessTraffic:DataFrame): Unit={

    //创建一个VideoAccessObject类型的集合
    val list = new ListBuffer[VideoAccess]

    //将DataFrame转化成VideoAccess对象，并存储在list集合中
    videoAccessTraffic.foreachPartition(partitionRecord => {  //按分区进行遍历
      partitionRecord.foreach(record => {  //对每个分区下的记录进行遍历
        list.append(new VideoAccess( //创建VideoAccessObject对象并添加至list
          //获取每条记录的值与VideoAccessObject对象的属性一一对应
          record.getAs[String]("date"),
          record.getAs[Long]("cmsID"),
          record.getAs[Long]("accessTraffic")
        ))
      })
      DatabaseStoreImp.insertRecordOfVideo(list)
    })
  }

  /**
    * 将DataFrame转化成VideoAccess对象，并存储在到数据库中dataFrameConvertToListTop5ByEveryDayAndCity
    */
  def dataFrameConvertToListTop5(everyDayVideoAccessTraffic:DataFrame): Unit = {

    //创建一个VideoAccess类型的集合
    val list = new ListBuffer[VideoAccess]

    //将DataFrame转化成VideoAccess对象，并存储在list集合中
    everyDayVideoAccessTraffic.foreachPartition(partitionRecord => {  //按分区进行遍历
      partitionRecord.foreach(record => {  //对每个分区下的记录进行遍历
        list.append(new VideoAccess( //创建VideoAccessObject对象并添加至list
          //获取每条记录的值与VideoAccessObject对象的属性一一对应
          record.getAs[String]("date"),
          record.getAs[Long]("cmsID"),
          record.getAs[Long]("accessTraffic"),
          record.getAs[Int]("sequence")
        ))
      })
      DatabaseStoreImp.insertRecordOfVideoTop5(list)
    })
  }


  /**
    * 将DataFrame转化成VideoAccess对象，并存储在到数据库中
    */
  def dataFrameConvertToListTop5ByEveryDayAndCity(everyDayVideoAccessTraffic:DataFrame): Unit = {

    //创建一个VideoAccess类型的集合
    val list = new ListBuffer[VideoAccess]

    //将DataFrame转化成VideoAccess对象，并存储在list集合中
    everyDayVideoAccessTraffic.foreachPartition(partitionRecord => {  //按分区进行遍历
      partitionRecord.foreach(record => {  //对每个分区下的记录进行遍历
        list.append(new VideoAccess( //创建VideoAccess对象并添加至list
          //获取每条记录的值与VideoAccess对象的属性一一对应
          record.getAs[String]("date"),
          record.getAs[String]("city"),
          record.getAs[Long]("cmsID"),
          record.getAs[Long]("accessTraffic"),
          record.getAs[Int]("sequence")
        ))
      })
      DatabaseStoreImp.insertRecordOfVideoTop5ByEveryDayAndCity(list)
    })
  }

  /**
    * 将DataFrame转化成VideoAccess对象，并存储在到数据库中
    */
  def dataFrameConvertToListTop5ByEveryDayAndTraffics(everyDayVideoAccessTraffic:DataFrame): Unit = {

    //创建一个VideoAccess类型的集合
    val list = new ListBuffer[VideoAccess]

    //将DataFrame转化成VideoAccess对象，并存储在list集合中
    everyDayVideoAccessTraffic.foreachPartition(partitionRecord => {  //按分区进行遍历
      partitionRecord.foreach(record => {  //对每个分区下的记录进行遍历
        list.append(new VideoAccess( //创建VideoAccess对象并添加至list
          //获取每条记录的值与VideoAccess对象的属性一一对应
          record.getAs[String]("date"),
          record.getAs[Long]("cmsID"),
          record.getAs[Long]("traffics"),
          record.getAs[Int]("sequence")
        ))
      })
      DatabaseStoreImp.insertRecordOfVideoTop5ByEveryDayAndTraffics(list)
    })
  }
}