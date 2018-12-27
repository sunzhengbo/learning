package com.spark.logparse.serviceImp

import com.spark.logparse.service.CountAccessTraffic
import com.spark.logparse.bean.VideoAccess
import com.spark.logparse.service.CountAccessTraffic
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ListBuffer

/**
  * 功能实现
  */
object CountAccessTrafficImp extends CountAccessTraffic {

  /**
    * 统计imooc主站课程按访问的次数进行倒序排列
    */
  def CountVideoAccessTraffic(spark:SparkSession,data:DataFrame): Unit ={
    //使用sql的方式统计
    //    data.createTempView("access")
    //
    //    val videoAccessTraffic = spark.sql(
    //      "select date,cmsID,count(*) as accessTraffic from access " +
    //        "where cmsType = 'video' " +
    //        "group by date,cmsID " +
    //        "order by accessTraffic desc"
    //    )

    //使用DataFrame的方式
    import spark.implicits._  //$"cmsType" == data.col("cmsType"),并需要导入隐式转换
    val videoAccessTrafficDF = data
      .filter($"cmsType"==="video")  //将视频类型的记录过滤出来
      .groupBy($"date",$"cmsID")  //安装日期和cmsID分组
      .agg(count($"cmsID").as("accessTraffic"))  //计算cmsID的点击量
      .orderBy($"accessTraffic".desc) //按照点击量进行排序

    //videoAccessTrafficDF.show()
    //将统计的结果转换成list集合
    DataFrameConvertToListImp.dataFrameConvertToList(videoAccessTrafficDF)
  }

  /**
    * 按每日统计imooc主站最受欢迎的课程以及访问的次数，统计每天前5名
    */
  def CountEveryDayVideoAccessTrafficTop5(spark:SparkSession,data:DataFrame): Unit ={

    import spark.implicits._  //$"cmsType" == data.col("cmsType"),并需要导入隐式转换
    val videoAccessTrafficDF = data
      .filter($"cmsType"==="video")  //将视频类型的记录过滤出来
      .groupBy($"date",$"cmsID")  //安装日期和cmsID分组
      .agg(count($"cmsID").as("accessTraffic"))  //计算cmsID的点击量
    //videoAccessTrafficDF.show()

    val everyDayVideoAccessTraffic = videoAccessTrafficDF.select($"date",$"cmsID",$"accessTraffic",
      row_number().over(//排序
        Window.partitionBy($"date")  //按日期分区
          .orderBy($"accessTraffic".desc)  //按访问量排序
      ).as("sequence") //重命名
    ).filter("sequence <= 5")
    //everyDayVideoAccessTraffic.show()

    //将统计的结果转换成list集合
    DataFrameConvertToListImp.dataFrameConvertToListTop5(everyDayVideoAccessTraffic)
  }

  /**
    * 按每日，地域统计imooc主站最受欢迎的课程以及访问的次数，统计每天前5名
    */
  def CountEveryDayAndCityVideoAccessTrafficTop5(spark:SparkSession,data:DataFrame): Unit ={

    import spark.implicits._  //$"cmsType" == data.col("cmsType"),并需要导入隐式转换
    val everyDayAndCityVideoAccessTrafficDF = data
      .filter($"cmsType"==="video")  //将视频类型的记录过滤出来
      .groupBy($"date",$"city",$"cmsID")  //安装日期和cmsID分组
      .agg(count($"cmsID").as("accessTraffic"))  //计算cmsID的点击量
    //everyDayAndCityVideoAccessTrafficDF.show()

    val everyDayAndCityVideoAccessTraffic = everyDayAndCityVideoAccessTrafficDF
      .select($"date",$"city",$"cmsID",$"accessTraffic",
        row_number().over(//排序
          Window.partitionBy($"date",$"city")  //按日期分区
            .orderBy($"date",$"city",$"accessTraffic".desc)  //按访问量排序
        ).as("sequence") //重命名
      ).filter("sequence <= 5")
    //everyDayAndCityVideoAccessTraffic.show(1000)

    //将统计的结果转换成list集合
    DataFrameConvertToListImp.dataFrameConvertToListTop5ByEveryDayAndCity(everyDayAndCityVideoAccessTraffic)
  }


  /**
    * 按每日，流量统计imooc主站最受欢迎的课程以及访问的次数，统计每天前5名
    */
  def CountEveryDayAndTrafficsTop5(spark:SparkSession,data:DataFrame): Unit ={

    import spark.implicits._  //$"cmsType" == data.col("cmsType"),并需要导入隐式转换
    val everyDayAndTrafficsDF = data
      .filter($"cmsType"==="video")  //将视频类型的记录过滤出来
      .groupBy($"date",$"cmsID")  //安装日期和cmsID分组
      .agg(sum($"traffic").as("traffics"))  //计算cmsID的点击量
    //everyDayAndTrafficsDF.show()

    val everyDayAndTraffics = everyDayAndTrafficsDF
      .select($"date",$"cmsID",$"traffics",
        row_number().over(//排序
          Window.partitionBy($"date")  //按日期分区
            .orderBy($"date",$"traffics".desc)  //按访问量排序
        ).as("sequence") //重命名
      ).filter("sequence <= 5")
    //everyDayAndTraffics.show()

    //将统计的结果转换成list集合CountEveryDayAndTrafficsTop5
    DataFrameConvertToListImp.dataFrameConvertToListTop5ByEveryDayAndTraffics(everyDayAndTraffics)
  }
}
