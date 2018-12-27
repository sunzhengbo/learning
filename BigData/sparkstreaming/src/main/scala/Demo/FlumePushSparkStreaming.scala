package Demo

import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * SparkStreaming 整合 Flume 之push方式
  */
object FlumePushSparkStreaming {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("FlumePushSparkStreaming")
    val ssc = new StreamingContext(conf,Seconds(5))

    //TODO... 实现 parkStreaming 整合 Flume 之push方式
    //get flume context
    val flumeStream = FlumeUtils.createStream(ssc, "localhost", 7777)
    //convert flumeContext to DStream
    val linesDS = flumeStream.map(flumeContext => new String(flumeContext.event.getBody.array()).trim())
    // computation
    val result = linesDS.flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey(_+_)
    //print
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
