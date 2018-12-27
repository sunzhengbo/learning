package com.imooc.spark.service

import com.imooc.spark.domain.CourseNumber
import com.imooc.spark.utils.DateConvertUtils
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils

/**
  *数据清洗
  */
object DataClean {
  /**
   * 实时统计实战课程的访问量
   */
  def cleanData(ssc:StreamingContext,brokers:String,topics:String): DStream[CourseNumber] ={

    val kafkaParams = Map[String,String]("metadata.broker.list"->brokers)
    val topicSet = topics.split(",").toSet

    val linesDS = KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](ssc,kafkaParams,topicSet)

    linesDS.map(_._2).map(line =>{
      val words = line.split("\t")

      val url = words(2)
      if (!"_".equals(words(3)) && url.contains("class/")){
        val id = url.substring(url.indexOf("/")+1,url.lastIndexOf("."))
        CourseNumber(DateConvertUtils.DateFormatConvert(words(0)),id)
      }else{
        CourseNumber("","")
      }
    }).filter(CourseNumber => !("".equals(CourseNumber.date) || "".equals(CourseNumber.id)))
  }
}
