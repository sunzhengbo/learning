package Demo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
  * SQLContext的使用
  */
object SQLContextApp {
  def main(args: Array[String]): Unit = {

    //创建sqlContext对象
    val scf = new SparkConf()
//    scf.setAppName("SQLContext") //设置名称
//    scf.setMaster("local[2]") //设置模式
//    scf.set("spark.testing.memory", "500000000") //IllegalArgumentException: System memory 259522560 must be at least 471859200
    val sct = new SparkContext(scf)
    val sqlContext = new SQLContext(sct)

    //业务处理
    val person = sqlContext.read.format("json").load(args(0))
    person.printSchema()
    person.show()

    //关闭资源
    sct.stop()
  }
}
