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

  private def approve(hm : VehiclePackageMessage): (Boolean,String) ={
    VehicleApprover.allConditions.foldLeft(true,""){
      (X,F)=> {
        val result= F(hm)
        result match {
          case (true, "") =>  (X._1 && result._1, X._2)
          case _ =>  (X._1 && result._1, X._2+","+result._2)
        }
      }
    }
  }

}

object VehicleApprover {
  val A = (hm: VehiclePackageMessage) => if (hm.totalAmount > 10000000)  (true,"") else (false, "totalAmount Low")
  val B = (hm: VehiclePackageMessage) => if (hm.totalRotative > 700000)  (true,"") else (false, "totalRotative Low")
  val C = (hm: VehiclePackageMessage) => if (hm.maximumPayment > 1100000)  (true,"") else (false, "maximumPayment Low")
  val D = (hm: VehiclePackageMessage) => if (hm.extScore >= 1)  (true,"") else (false, "extScore Low")
  val E = (hm: VehiclePackageMessage) => if (hm.age < 80)  (true,"") else (false, "Age")

  val allConditions = List(A,B,C,D,E)

}
