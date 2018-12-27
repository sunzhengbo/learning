package com.spark.logparse.bean

/**
  * 视频访问实体类
  */
class VideoAccess {

  var date:String = ""  // 日期
  var cms_id:Long = 0l  // 课程编号
  var traffic:Long = 0l  // 访问量
  var sequence:Int = 0  // 序号
  var city:String = ""  // 城市

  def this(date:String, cms_id:Long, traffic:Long){
    this()
    this.date = date
    this.cms_id = cms_id
    this.traffic = traffic
  }

  def this(date:String, cms_id:Long, traffic:Long, sequence:Int){
    this()
    this.date = date
    this.cms_id = cms_id
    this.traffic = traffic
    this.sequence = sequence
  }

  def this(date:String,city:String,cms_id:Long, traffic:Long, sequence:Int){
    this()
    this.date = date
    this.city = city
    this.cms_id = cms_id
    this.traffic = traffic
    this.sequence = sequence
  }
}
