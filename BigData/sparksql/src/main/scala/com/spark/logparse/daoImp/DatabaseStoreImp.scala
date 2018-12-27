package com.spark.logparse.daoImp

import com.spark.logparse.bean.VideoAccess
import com.spark.logparse.dao.DatabaseStore
import com.spark.logparse.utils.DatabaseUtil

import scala.collection.mutable.ListBuffer

object DatabaseStoreImp extends DatabaseStore{

  /**
    * 将获取的课程排序写入数据库
    */
  def insertRecordOfVideo(list:ListBuffer[VideoAccess]): Unit ={

    val sql = "insert into VideoAccessTraffic (date,cms_id,traffic) values (?,?,?)"
    val conn = DatabaseUtil.getConnection()  //获取数据库连接
    val pres = conn.prepareStatement(sql)  //获取预处理对象

    //设置手动提交
    conn.setAutoCommit(false)

    //赋值
    for (ele <- list){
      pres.setString(1,ele.date)
      pres.setLong(2, ele.cms_id)
      pres.setLong(3, ele.traffic)
      pres.addBatch()  //设置批处理
    }

    pres.executeBatch() //此处就应该调用批处理，而不是execute()

    conn.commit()  //提交事务

    DatabaseUtil.releaseResource(conn,pres)  //释放资源
  }

  def insertRecordOfVideoTop5(list:ListBuffer[VideoAccess]): Unit ={

    val sql = "insert into EveryDayVideoAccessTraffic (date,cms_id,traffic,sequence) values (?,?,?,?)"
    val conn = DatabaseUtil.getConnection()  //获取数据库连接
    val pres = conn.prepareStatement(sql)  //获取预处理对象

    //设置手动提交
    conn.setAutoCommit(false)

    //赋值
    for (ele <- list){
      pres.setString(1,ele.date)
      pres.setLong(2, ele.cms_id)
      pres.setLong(3, ele.traffic)
      pres.setInt(4,ele.sequence)
      pres.addBatch()  //设置批处理
    }
    pres.executeBatch() //此处就应该调用批处理，而不是execute()

    conn.commit()  //提交事务

    DatabaseUtil.releaseResource(conn,pres)  //释放资源
  }

  def insertRecordOfVideoTop5ByEveryDayAndCity(list:ListBuffer[VideoAccess]): Unit ={

    val sql = "insert into EveryDayAndCityVideoAccessTraffic (date,city,cms_id,traffic,sequence) values (?,?,?,?,?)"
    val conn = DatabaseUtil.getConnection()  //获取数据库连接
    val pres = conn.prepareStatement(sql)  //获取预处理对象

    //设置手动提交
    conn.setAutoCommit(false)

    //赋值
    for (ele <- list){
      pres.setString(1,ele.date)
      pres.setString(2,ele.city)
      pres.setLong(3, ele.cms_id)
      pres.setLong(4, ele.traffic)
      pres.setInt(5,ele.sequence)
      pres.addBatch()  //设置批处理
    }
    pres.executeBatch() //此处就应该调用批处理，而不是execute()

    conn.commit()  //提交事务

    DatabaseUtil.releaseResource(conn,pres)  //释放资源
  }

  def insertRecordOfVideoTop5ByEveryDayAndTraffics(list:ListBuffer[VideoAccess]): Unit ={

    val sql = "insert into EveryDayAndTraffics (date,cms_id,traffics,sequence) values (?,?,?,?)"
    val conn = DatabaseUtil.getConnection()  //获取数据库连接
    val pres = conn.prepareStatement(sql)  //获取预处理对象

    //设置手动提交
    conn.setAutoCommit(false)

    //赋值
    for (ele <- list){
      pres.setString(1,ele.date)
      pres.setLong(2, ele.cms_id)
      pres.setLong(3, ele.traffic)
      pres.setInt(4,ele.sequence)
      pres.addBatch()  //设置批处理
    }
    pres.executeBatch() //此处就应该调用批处理，而不是execute()

    conn.commit()  //提交事务

    DatabaseUtil.releaseResource(conn,pres)  //释放资源
  }
}