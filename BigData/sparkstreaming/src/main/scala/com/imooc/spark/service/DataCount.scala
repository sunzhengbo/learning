package com.imooc.spark.service

import com.imooc.spark.dao.DatabaseOperation
import com.imooc.spark.domain.{CourseAccessTotal, CourseNumber}
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable.ListBuffer

/**
  * 数据统计
  */
object DataCount {

  /**
    * 实时统计慕课网的实战课程的访问总量
    */
  def realtimeTraffic(date:DStream[CourseNumber]): Unit = {
    date.map(x => {
      //date_id ==> row key
      (x.date+"_"+x.id,1)
      //computing
    }).reduceByKey(_+_).map(x =>{
      //DStream[CourseNumber]==>DStream[CourseAccessTotal]
      CourseAccessTotal(x._1,x._2)
      //DStream => RDD
    }).foreachRDD(rdd =>{
      rdd.foreachPartition(partition =>{
        val list = new ListBuffer[CourseAccessTotal]
        //遍历放在ListBuffer[CourseAccessTotal]中
        partition.foreach(record =>{
          list.append(CourseAccessTotal(record.date_id,record.traffic))
        })
        //数据入库
        DatabaseOperation.incrementRecord(list)
      })
    })
  }
}
