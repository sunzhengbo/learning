package Demo

import java.sql.ResultSet
import java.util

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import utils.JdbcConnectionsPool

import scala.collection.mutable.ArrayBuffer

/**
  * 过滤黑名单
  */
object FilterBlacks {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("FilterBlocks")
    val ssc = new StreamingContext(conf,Seconds(5))
    val list  = new ArrayBuffer[String]()

    //获取数据
    val linesDS = ssc.socketTextStream("localhost",7777)

    val result = selectQuery()
    while(result.next()){
      list.append(result.getString(1))
    }

    // record => (record.name,true)
    val listRDD = ssc.sparkContext.parallelize(list).map(record => (record,true))

    // line => (line.name,line)
    val lineRDD = linesDS.map(line => (line.split(",")(1),line))

    val finalRDD = lineRDD.transform(rdd => {
      //join两个RDD => (name,(line,true/false))
      rdd.leftOuterJoin(listRDD).filter(f => {//过滤掉不符合条件的记录
        f._2._2.getOrElse(false) != true
      }).map(m => m._2._1) //还原到原型
    })

    finalRDD.print()

    ssc.start()
    ssc.awaitTermination()
  }


  /**
    * 使用数据库连接池的方式插入数据
    */
  def selectQuery(): ResultSet = {
    val connection = new JdbcConnectionsPool().getConnection()

    val sql = "select * from blockname"
    val pre_1 = connection.prepareStatement(sql)
    val resultSet = pre_1.executeQuery()
    return resultSet
  }
}
