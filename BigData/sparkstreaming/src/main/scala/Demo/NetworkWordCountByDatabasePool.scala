package Demo

import java.sql.Connection

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import utils.{JdbcConnections, JdbcConnectionsPool}

/**
  * 通过socket的方式获取数据,并写入数据库,存在就更新值
  *
  * 注:rdd的算子里的代码都是在 executor 执行，需要序列化，dStream的算子里的非rdd算子部分是在driver端执行的
  */
object NetworkWordCountByDatabasePool {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("NetworkWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(5))

    val linesDS = ssc.socketTextStream("localhost",7777)
    val words = linesDS.flatMap(line => line.split(" "))
    val pairs = words.map(word => (word,1))
    val wordCount = pairs.reduceByKey(_+_)
    val jdbcConnectionsPool = new JdbcConnectionsPool()


    wordCount.foreachRDD { rdd =>
      rdd.foreachPartition { partitionOfRecords =>
        val connection = jdbcConnectionsPool.getConnection() //使用数据库连接池的方式
        partitionOfRecords.foreach(record => {
          DatabaseOperation.send(connection,record)
          //DatabaseOperation.send(record) //直接获取数据库连接的方式
        })
        //JdbcConnectionsPool.returnConnection(connection)
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }
}

object DatabaseOperation extends Serializable {
  /**
    * 使用数据库连接池的方式插入数据
    */
  def send(connection: Connection, record: (String, Int)): Unit = {

    val sql_1 = "select * from networkwordcount where word=?"
    val pre_1 = connection.prepareStatement(sql_1)
    pre_1.setString(1, record._1)
    val resultSet = pre_1.executeQuery()

    val flag = resultSet.next() //判断获取的结果集是否存在,存在返回true

    if (!flag) {
      val sql_2 = "insert into networkwordcount (word,number) values(?,?)"
      val pre = connection.prepareStatement(sql_2)
      pre.setString(1, record._1)
      pre.setInt(2, record._2)
      pre.executeUpdate()
    } else {
      val sql_3 = "update networkwordcount set number=? where word=?"
      val pre = connection.prepareStatement(sql_3)
      pre.setString(2, record._1)
      pre.setInt(1, record._2 + resultSet.getInt(2))
      pre.executeUpdate()
    }
  }

  /**
    * 直接创建数据库连接的方式插入数据
    */
  def send(record: (String, Int)): Unit = {

    val connection = JdbcConnections.getConnection

    val sql_1 = "select * from networkwordcount where word=?"
    val pre_1 = connection.prepareStatement(sql_1)
    pre_1.setString(1, record._1)
    val resultSet = pre_1.executeQuery()

    val flag = resultSet.next() //判断获取的结果集是否存在,存在返回true

    if (!flag) {
      val sql_2 = "insert into networkwordcount (word,number) values(?,?)"
      val pre = connection.prepareStatement(sql_2)
      pre.setString(1, record._1)
      pre.setInt(2, record._2)
      pre.executeUpdate()
    } else {
      val sql_3 = "update networkwordcount set number=? where word=?"
      val pre = connection.prepareStatement(sql_3)
      pre.setString(2, record._1)
      pre.setInt(1, record._2 + resultSet.getInt(2))
      pre.executeUpdate()
    }
    if (connection != null) {
      connection.close()
    }
  }
}
