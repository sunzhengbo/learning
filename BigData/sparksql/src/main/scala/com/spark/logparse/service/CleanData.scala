package com.spark.logparse.service

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

trait CleanData {
  /**
    * 1.获取我们需要的字段记录
    * 2.过滤掉url不符的记录
    */
  def dataClean(data:RDD[String]): RDD[String]

  /**
    * 1.将RDD数据转化成DataFrame
    * 2.把输入的数据解析成我们需要的字段
    */
  def dataFormatConvert(spark:SparkSession,data:RDD[String]): DataFrame

  /**
    * 解析每条记录，并将其转换成输出的RDD
    * @param record：每条记录
    */
  def handleOutputData(record:String): Row
}
