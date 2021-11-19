package eci.edu.co
package services.packers
import services.DynamoService
import protos.ClientMessage.ClientMessage
import protos.VehiclePackageMessage.VehiclePackageMessage

case class VehiclePacker() {
  val dynamoService = DynamoService()
  val tableSourcesName = "DataSourcesResponses"
  def toVehicleMessage(uuid: String, clients : Seq[ClientMessage]): VehiclePackageMessage = {
    val start = System.nanoTime
    val ds = dynamoService.getItems(tableSourcesName,uuid)
    val dataCredit = ds.filter(d => d._1 == "DataCredit")
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
    val age = clients.foldLeft(0) {
      (a, b) => a.max(b.age)
    }
    VehiclePackageMessage(uuid, (System.nanoTime - start) / 1e9d, totalAmount, totalRotative, maximumPayment, extScore, age)
  }
}
