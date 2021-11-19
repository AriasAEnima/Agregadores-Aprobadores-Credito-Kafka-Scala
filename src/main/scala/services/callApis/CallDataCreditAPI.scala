package eci.edu.co
package services.callApis

import play.api.libs.json.{JsValue, Json}

import scala.util.Random
import commands.CommandCallAPI

object CallDataCreditAPI extends CommandCallAPI {
  override def callAPI(body: JsValue) = {
    Thread.sleep(2500)
    val maxAumount = Random.between(5000000, 10000000)
    val rotative = Random.between(2000000, 5000000)
    val maxPayment = Random.between(500000, 1000000)
    val score = Random.between(1, 5)
    ("DataCredit", Json.parse(s"""{"maxAmount": ${maxAumount}, "rotative": ${rotative}, "maxPayment" : ${maxPayment} , "score": ${score}}"""))
  }

}
