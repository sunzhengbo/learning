package com.imooc.spark.dao

import com.imooc.spark.domain.CourseAccessTotal
import com.imooc.spark.utils.HBaseUtils

import scala.collection.mutable.ListBuffer

/**
  * 数据库操作
  */
object DatabaseOperation {

  /**
    * 实时统计的数据入库
    */
  def incrementRecord(list:ListBuffer[CourseAccessTotal]): Unit ={
    val hBaseUtils: HBaseUtils = HBaseUtils.create()
    //遍历讲数据插入数据库
    list.foreach(cat => {
      hBaseUtils.incrementColumnValue("streaming",cat.date_id,"course","visitorVolume",cat.traffic)
    })
  }
}
