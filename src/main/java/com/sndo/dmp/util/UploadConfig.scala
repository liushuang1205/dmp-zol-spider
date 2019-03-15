package com.sndo.dmp.util

/**
  * Created by ldxPC on 2019/3/1.
  */
object UploadConfig {

  val conf = ConfigFactory.load("application.conf")
  //mongo src
  var srcHost: String = conf.getString("mongo.src.host")

  var srcPc6Db: String = conf.getString("mongo.src.pc6.db")
  var allPc6ParseCol: String = conf.getString("mongo.src.pc6.all.parse.col")
  var needLoadPc6Col: String = conf.getString("mongo.src.pc6.need.load.col")

  var srcAppstoreDb: String = conf.getString("mongo.src.appstore.db")
  var allAppstoreParseCol: String = conf.getString("mongo.src.appstore.all.parse.col")
  var needLoadAppstoreCol: String = conf.getString("mongo.src.appstore.need.load.col")

  var srcAuthDataBase: String = conf.getString("mongo.src.auth.database")
  var srcAuthUserName: String = conf.getString("mongo.src.auth.username")
  var srcAuthPassword: String = conf.getString("mongo.src.auth.password")
  //mongo online
  var onlineHost: String = conf.getString("mongo.online.host")
  var onlineDatabase: String = conf.getString("mongo.online.db")
  var onlinePostCol: String = conf.getString("mongo.online.post_db")
  var pullPatch: Int = conf.getInt("mongo.online.pull_batch")
  var onlineAuthDatabase: String = conf.getString("mongo.online.auth.database")
  var onlineAuthUserName: String = conf.getString("mongo.online.auth.username")
  var onlineAuthPassword: String = conf.getString("mongo.online.auth.password")
  //file
  var logoPathPrefix: String = conf.getString("file.logo.prefix")
  var screenPathPrefix: String = conf.getString("file.screen.prefix")
  /*var needLoadLogoPathPrefix: String = conf.getString("file.need.load.logo.prefix")
  var needLoadScreenPathPrefix: String = conf.getString("file.need.load.screenshot.prefix")
  var zipLogoFileName: String = conf.getString("file.zip.logo.file.name")
  var zipScreenshotFileName: String = conf.getString("file.zip.screenshot.file.name")*/
  //wordsegment
  var wordSegmentHost: String = conf.getString("wordsegment.host")
}
