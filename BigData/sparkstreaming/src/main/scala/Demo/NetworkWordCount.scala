package Demo

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 通过socket的方式获取数据
  */
object NetworkWordCount {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("NetworkWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(5))

    val linesDS = ssc.socketTextStream("localhost",7777)
    val words = linesDS.flatMap(line => line.split(" "))
    val pairs = words.map(word => (word,1))
    val wordCount = pairs.reduceByKey(_+_)
    wordCount.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
