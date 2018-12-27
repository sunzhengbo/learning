package Demo

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 通过监测目录的方式获取数据
  */
object FileWordCount {
    def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("FileWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(5))

    val file = ssc.textFileStream("file:///home/sunzb/Documents/test")
    val wordCount = file.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    wordCount.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
