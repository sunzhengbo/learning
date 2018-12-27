package com.spark.logparse.service

import org.apache.spark.sql.DataFrame

trait DataFrameConvertToList {

  def dataFrameConvertToList(VideoAccessTraffic:DataFrame): Unit

  def dataFrameConvertToListTop5(everyDayVideoAccessTraffic:DataFrame): Unit

  def dataFrameConvertToListTop5ByEveryDayAndCity(everyDayVideoAccessTraffic:DataFrame): Unit

  def dataFrameConvertToListTop5ByEveryDayAndTraffics(everyDayVideoAccessTraffic:DataFrame): Unit
}
