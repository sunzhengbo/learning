package Demo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


/**
  * DataFrame其他API操作
  *
  */
object DataFrameDemo {
  def main(args: Array[String]): Unit = {
    //获取一个spark对象
    val spark = SparkSession.builder().appName("SparkSessionApp").master("local[2]").getOrCreate()

    //获取本地的RDD文件
    val studentRDD = spark.sparkContext.textFile("file:///home/sunzb/Documents/student.data")

    //调用方法
    DataFrame01(spark,studentRDD)

    spark.stop()
  }

  /**
    * 定义RDD的Schema
    */
  case class convert(ID:Int,name:String,phone:String,email:String)

  def DataFrame01 (spark:SparkSession,studentRDD:RDD[String]): Unit ={

    //导入隐式转换
    import spark.implicits._
    //转成DataFrame
    val studentDF = studentRDD.map(_.split("\\|"))
      .map(attributes => convert(attributes(0).toInt,attributes(1),attributes(2),attributes(3)))
      .toDF

    //排序,show默认是显示钱20行，并且每个字段的长度也是有限制的，输入false参数可以关闭限制
    //studentDF.sort(studentDF.col("name")).show(30,false)

    //按名字正序，邮箱倒序
    //studentDF.sort(studentDF.col("name").asc,studentDF.col("email").desc).show(30,false)

    //取第一行的数据  studentDF.first() = studentDF.head()
    //println(studentDF.first())

    //获取前10行
    //println(studentDF.take(10))

    //再次获取一个表
    val studentDF2 = studentRDD.map(_.split("\\|"))
      .map(attributes => convert(attributes(0).toInt,attributes(1),attributes(2),attributes(3)))
      .toDF

    //jion 等号是===三个,默认的jion方式是内连接
    studentDF.join(studentDF2,studentDF.col("id")===studentDF2.col("id"),"left").show()

  }
}
