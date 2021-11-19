package eci.edu.co
package services.packers

import protos.HousingPackageMessage.HousingPackageMessage
import services.DynamoService
import protos.ClientMessage.ClientMessage

case class HousingPacker() {
  val dynamoService = DynamoService()
  val tableSourcesName = "DataSourcesResponses"
  def toHousingMessage(uuid: String, clients : Seq[ClientMessage]): HousingPackageMessage = {
    val start = System.nanoTime
    val ds = dynamoService.getItems(tableSourcesName,uuid)
    val dataCredit = ds.filter(d => d._1 == "DataCredit")
    val internal = ds.filter(d => d._1 == "Internal")
    val totalAmount = dataCredit.foldLeft(0) {
      (a, b) => a + (b._2 \ "maxAmount").as[Int]
    }
    val totalRotative = dataCredit.foldLeft(0) {
      (a, b) => a + (b._2 \ "rotative").as[Int]
    }
    val maximumPayment = dataCredit.foldLeft(0) {
      (a, b) => a + (b._2 \ "maxPayment").as[Int]
    }
    val extScore = dataCredit.foldLeft(0) {
      (a, b) => a.max ((b._2 \ "score").as[Int])
    }
    val intScore = internal.foldLeft(0) {
      (a, b) => a.max((b._2 \ "score").as[Int])
    }
    val activeDebs = internal.foldLeft(0) {
      (a, b) => (a + (b._2 \ "activeDebs").as[Int])/2
    }
    val penalty = !internal.foldLeft(false) {
      (a, b) => a || !(b._2 \ "penalty").as[Boolean]
    }
    val age = clients.foldLeft(0) {
      (a, b) => a.max(b.age)
    }
    val previousAttemps = internal.foldLeft(false) {
      (a, b) => a || (b._2 \ "attemps").as[Boolean]
    }
    HousingPackageMessage(uuid, (System.nanoTime - start) / 1e9d, totalAmount, totalRotative, maximumPayment, extScore, intScore, activeDebs, penalty, age, previousAttemps)
  }
}
