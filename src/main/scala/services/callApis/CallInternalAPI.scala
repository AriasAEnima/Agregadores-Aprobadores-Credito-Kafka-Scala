package eci.edu.co
package services.callApis

import play.api.libs.json.{JsValue, Json}

import scala.util.Random

import commands.CommandCallAPI

object CallInternalAPI extends CommandCallAPI {
  override def callAPI(body: JsValue) = {
    Thread.sleep(2500)
    val score = Random.between(1, 5)
    val activeDebs = Random.between(0, 3)
    val penalty = Random.nextBoolean()
    val attemps = Random.nextBoolean()
    ("Internal", Json.parse(s"""{"score": ${score}, "activeDebs": ${activeDebs}, "penalty" : ${penalty} , "attemps": ${attemps}}"""))
  }

}
