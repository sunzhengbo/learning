package Demo

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Streaming基于Received的方式整合Kafka
  * 1.启动Zookeeper
  * 2.启动kafka
  * 3.创建Topic
  * 4.开启生产消息入口
  * 5.代码开发
  * 6.发送消息
  * 6.发送消息
  * 7.控制台消费消息
  */
object StreamingIncorporateKafkaByReceived {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StreamingIncorporateKafka").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(5))

    val zkQuorum = "localhost:2181"
    val groupId = "0"
    val topics = "sparkstreaming"  //多个topic用逗号隔开
    val topicMap = topics.split(",").map((_, 2)).toMap

    //TODO... 实现整合
    val linesDS = KafkaUtils.createStream(ssc, zkQuorum, groupId, topicMap
      , StorageLevel.MEMORY_AND_DISK_SER_2)

    //linesDS 的结果是 (null,value value ) 形式,所以使用 map(_._2) 获取符合条件的值
    val result = linesDS.map(_._2).flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)

    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}

