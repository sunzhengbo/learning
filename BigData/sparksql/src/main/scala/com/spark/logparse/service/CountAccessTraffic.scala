package com.spark.logparse.service

import org.apache.spark.sql.{DataFrame, SparkSession}

trait CountAccessTraffic {

  /**
    * 统计imooc主站课程按访问的次数进行倒序排列
    */
  def CountVideoAccessTraffic(spark:SparkSession,data:DataFrame): Unit

  /**
    * 按每日统计imooc主站最受欢迎的课程以及访问的次数，统计每天前5名
    */
  def CountEveryDayVideoAccessTrafficTop5(spark:SparkSession,data:DataFrame): Unit

  /**
    * 按每日，地域统计imooc主站最受欢迎的课程以及访问的次数，统计每天前5名
    */
  def CountEveryDayAndCityVideoAccessTrafficTop5(spark:SparkSession,data:DataFrame): Unit

  /**
    * 按每日，流量统计imooc主站最受欢迎的课程以及访问的次数，统计每天前5名
    */
  def CountEveryDayAndTrafficsTop5(spark:SparkSession,data:DataFrame): Unit
}
