package eci.edu.co
package services.approve
import services.DynamoService
import protos.VehiclePackageMessage.VehiclePackageMessage
import entities.CreditRequest.CreditVehicle

case class VehicleApprover(){
  val dynamoService = DynamoService()

  def runApprovalEngine(hm : VehiclePackageMessage) = {
    val result = approve(hm)
    dynamoService.putItem("ResultTable",hm.uuid,CreditVehicle,result._1,result._2)
  }

  private def approve(hm : VehiclePackageMessage): (Boolean,List[String]) ={
    val allresults=VehicleApprover.allConditions.flatMap(f => {
      val result = f(hm)
      result match {
        case "" => None
        case x => Some(x)
      }
    })
    if (allresults.length == 0) (true, List()) else (false, allresults)
  }

}

object VehicleApprover {
  val A = (hm: VehiclePackageMessage) => if (hm.totalAmount > 10000000)  ("") else ( "totalAmount Low")
  val B = (hm: VehiclePackageMessage) => if (hm.totalRotative > 700000)  ("") else ( "totalRotative Low")
  val C = (hm: VehiclePackageMessage) => if (hm.maximumPayment > 1100000)  ("") else ( "maximumPayment Low")
  val D = (hm: VehiclePackageMessage) => if (hm.extScore >= 1)  ("") else ( "extScore Low")
  val E = (hm: VehiclePackageMessage) => if (hm.age < 80)  ("") else ( "Age")

  val allConditions = List(A,B,C,D,E)

}
