package Demo

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 保留状态统计方式,就是累计历史的数据
  */
object StatefulWordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StatefulWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(5))

    //设置数据记录的目录,生产上最好放在hdfs上
    ssc.checkpoint(".")

    val lines = ssc.socketTextStream("localhost",7777)
    val pairs = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    val runningCounts = pairs.updateStateByKey[Int](updateFunction _)
    runningCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }

  /**
    *记录历史数据
    */
  def updateFunction(newValues: Seq[Int], runningCount: Option[Int]): Option[Int] = {
    /**
      * newValues:将获取到的新值进行求和
      * runningCount:获取历史的值,有就获取,没有就是返回默认的状态
      */
    val newCount = newValues.sum + runningCount.getOrElse(0)
    Some(newCount)
  }
}
