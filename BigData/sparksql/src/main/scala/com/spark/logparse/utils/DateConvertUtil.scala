package com.spark.logparse.utils

import org.apache.commons.lang3.time.FastDateFormat

/**
  *日期格式转换
  */
object DateConvertUtil {
  def dateFormatConvert(date:String): String ={
    //定义输出日期格式
    val OUTPUT_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
    //定义输入日期格式
    val INPUT_FORMAT = FastDateFormat.getInstance("dd/MMM/yyyy:HH:mm:ss Z")

    //返回一个日期字符串
    OUTPUT_FORMAT.format(INPUT_FORMAT.parse(date).getTime)
  }
}
