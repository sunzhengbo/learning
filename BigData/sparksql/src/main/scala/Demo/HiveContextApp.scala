package Demo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext

/**
  * HiveContext的使用,使用的时候需要加上MySQL的连接jar包，并讲hivesite.xm文件配置在conf目录下
  */
object HiveContextApp {
  def main(args: Array[String]): Unit = {
    val scf = new SparkConf()
    val sct = new SparkContext(scf)
    val hiveContext = new HiveContext(sct)

    val table = hiveContext.table(args(0))
    table.show()
  }
}
