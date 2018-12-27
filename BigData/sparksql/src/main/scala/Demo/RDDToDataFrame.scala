package Demo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}



/**
  * RDD convert to DataFrame
  *
  */
object RDDToDataFrame {
  def main(args: Array[String]): Unit = {
    //获取一个spark对象
    val spark = SparkSession.builder().appName("SparkSessionApp").master("local[2]").getOrCreate()

    //获取本地的RDD文件
    val personRDD = spark.sparkContext.textFile("file:///home/sunzb/Documents/person.txt")

    //调用方法
    //RDDToDataFrame01(spark,personRDD)
    RDDToDataFrame02(spark,personRDD)

    spark.stop()
  }

  /**
    * 定义RDD的Schema
    */
  case class convert(name:String,age:Int)

  /**
    * 方式一
    * 条件：预先定义Schema的类型
    * 优点：代码简洁
    */
  def RDDToDataFrame01 (spark:SparkSession,personRDD:RDD[String]): Unit ={

    //导入隐式转换
    import spark.implicits._
    //转成DataFrame
    val personDF = personRDD.map(_.split(",")).map(attributes => convert(attributes(0),attributes(1).toInt)).toDF

    //DataFrame操作
    personDF.show()
    //过滤
    personDF.filter(personDF.col("age")>18).show()

    //sql语句操作
    personDF.createOrReplaceTempView("person")
    //过滤
    spark.sql("select * from person where age>20").show()
  }

  /**
    * 方式二
    * 优点：在创建DataFrame之前指定Schema的类型
    * 缺点：代码冗余
    */
  def RDDToDataFrame02 (spark:SparkSession,personRDD:RDD[String]): Unit ={

    // Convert records of the RDD (people) to Rows
    val rowRDD = personRDD.map(_.split(",")).map(attributes => Row(attributes(0), attributes(1).toInt))

    // Generate the schema based on the string of schema
    val schema = StructType(Array(
      StructField("name": String, StringType: DataType, true),
      StructField("age": String, IntegerType: DataType, true)))

    // Apply the schema to the RDD
    val peopleDF = spark.createDataFrame(rowRDD, schema)

    // Creates a temporary view using the DataFrame
    peopleDF.createOrReplaceTempView("people")

    spark.sql("SELECT * FROM people").show()

  }
}
