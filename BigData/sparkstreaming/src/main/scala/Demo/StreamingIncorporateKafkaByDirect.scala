package Demo

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Streaming基于Direct的方式整合Kafka(推荐使用)
  */
object StreamingIncorporateKafkaByDirect {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("StreamingIncorporateKafkaByDirect")
    val ssc = new StreamingContext(conf,Seconds(5))

    val brokers = "localhost:9092"
    val kafkaParams = Map[String,String]("metadata.broker.list"->brokers)
    val topics = "sparkstreaming"  //多个topic用逗号隔开
    val topicSet = topics.split(",").toSet

    //TODO... 实现整合
    val linesDS = KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](ssc,kafkaParams,topicSet)
    //linesDS 的结果是 (null,value value ) 形式,所以使用 map(_._2) 获取符合条件的值
    val result = linesDS.map(_._2).flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
