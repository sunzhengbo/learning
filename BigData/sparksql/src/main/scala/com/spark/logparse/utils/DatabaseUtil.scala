package com.spark.logparse.utils

import java.sql.{Connection, DriverManager, PreparedStatement}

object DatabaseUtil {

  /**
    * 获取MySQL连接
    */
  def getConnection(): Connection ={

    val url = "jdbc:mysql://118.25.7.22:3306/ImoocLogStore"

    DriverManager.getConnection(url,"root","root")

  }

  /**
    * 释放资源
    */
  def releaseResource(connection:Connection,pres: PreparedStatement): Unit ={

    if (connection != null) connection.close()

    if (pres != null) pres.close()
  }
}
