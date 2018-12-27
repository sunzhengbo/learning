package Demo

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * spark streaming整合spark sql统计词频
  */
object SQLNetworkWordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SQLNetworkWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(5))

    val linesDS = ssc.socketTextStream("localhost",7777)
    val wordsDS = linesDS.flatMap(line => line.split(" "))

    // Convert RDDs of the words DStream to DataFrame and run SQL query
    wordsDS.foreachRDD(rdd => {
      // Get the singleton instance of SparkSession
      val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)
      import spark.implicits._

      // Convert RDD[String] to RDD[case class] to DataFrame
      val wordsDF = rdd.map(word => Record(word)).toDF()
      wordsDF.createOrReplaceTempView("words")

      spark.sql("select word, count(*) as total from words group by word").show()
    })

    ssc.start()
    ssc.awaitTermination()
  }
}

/** Case class for converting RDD to DataFrame */
case class Record(word: String)

/** Lazily instantiated singleton instance of SparkSession */
object SparkSessionSingleton {

  @transient  private var instance: SparkSession = _

  def getInstance(sparkConf: SparkConf): SparkSession = {
    if (instance == null) {
      instance = SparkSession
        .builder
        .config(sparkConf)
        .getOrCreate()
    }
    instance
  }
}