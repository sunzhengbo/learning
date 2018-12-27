package com.imooc.spark

import com.imooc.spark.service.{DataClean, DataCount}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 实时统计慕课网的实战课程的访问总量
  */
object App {
  def main(args: Array[String]): Unit = {

    if (args.length != 2){
      System.err.print("The param is two or four")
      System.exit(1)
    }

    val conf = new SparkConf()//.setMaster("local[2]").setAppName("App")
    val ssc = new StreamingContext(conf,Seconds(10))

    val brokers = args(0) //"localhost:9092"
    val topics = args(1)//"sparkstreaming"  //多个topic用逗号隔开


    //清洗
    val data = DataClean.cleanData(ssc,brokers,topics)

    //统计...入库
    DataCount.realtimeTraffic(data)

    ssc.start()
    ssc.awaitTermination()
  }
}
