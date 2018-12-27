package com.imooc.spark.utils

import org.apache.commons.lang3.time.FastDateFormat

/**
  * 日期类转换
  */
object DateConvertUtils {
  def DateFormatConvert(date:String): String ={
    //定义输出日期格式
    val INPUT_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
    //定义输入日期格式
    val OUTPUT_FORMAT = FastDateFormat.getInstance("yyyyMMdd")

    //返回一个日期字符串
    OUTPUT_FORMAT.format(INPUT_FORMAT.parse(date).getTime)
  }
}
