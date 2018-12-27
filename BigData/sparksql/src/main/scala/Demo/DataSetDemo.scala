package Demo

import DataFrameDemo.convert
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *将csv的格式文件转化成DataSet
  */
object DataSetDemo {
  def main(args: Array[String]): Unit = {
    //获取一个spark对象
    val spark = SparkSession.builder().appName("SparkSessionApp").master("local[2]").getOrCreate()

    //获取本地的csv文件
    val salesDF = spark.read
      .option("header","true")  //是否需要读取头部信息
      .option("inferSchema","true")  //是否需要结构信息
      .csv("file:///home/sunzb/Documents/hadoop001/sales.csv")

    //调用方法
    DataSet01(spark,salesDF)

    spark.stop()
  }

  /**
    * 定义DataSet的Schema
    */
  case class convert(transactionId:Int,customerId:Int,itemId:Int,amountPaid:Double)

  def DataSet01 (spark:SparkSession,salesDF:DataFrame): Unit ={

    //导入隐式转换
    import spark.implicits._

    //把DataFrame转换成DataSet
    val salesDS = salesDF.as[convert]

    //输出
    salesDS.show()
    salesDF.show()

  }
}
