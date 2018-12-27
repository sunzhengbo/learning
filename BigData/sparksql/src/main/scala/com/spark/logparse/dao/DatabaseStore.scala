package com.spark.logparse.dao

import com.spark.logparse.bean.VideoAccess

import scala.collection.mutable.ListBuffer

trait DatabaseStore {

  /**
    * 将获取的课程排序写入数据库
    */
  def insertRecordOfVideo(list:ListBuffer[VideoAccess]): Unit

  /**
    * 按每日统计imooc主站最受欢迎的课程以及访问的次数
    */
  def insertRecordOfVideoTop5(list:ListBuffer[VideoAccess]): Unit

  def insertRecordOfVideoTop5ByEveryDayAndCity(list:ListBuffer[VideoAccess]): Unit

  def insertRecordOfVideoTop5ByEveryDayAndTraffics(list:ListBuffer[VideoAccess]): Unit
}
