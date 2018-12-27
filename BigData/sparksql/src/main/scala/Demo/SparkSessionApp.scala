package Demo

import org.apache.spark.sql.SparkSession


/**
  * SparkSession的使用
  */
object SparkSessionApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("SparkSessionApp").master("local[2]").getOrCreate()

    val person = spark.read.json(args(0))
    person.printSchema()
    person.show()

    spark.stop()
  }
}
