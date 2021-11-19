package eci.edu.co
package services.approve
import services.DynamoService
import protos.HousingPackageMessage.HousingPackageMessage
import entities.CreditRequest.CreditHousing

case class HousingCampaignApprover(){
  val dynamoService = DynamoService()

  def runApprovalEngine(hm : HousingPackageMessage) = {
    val result = approve(hm)
    dynamoService.putItem("ResultTableCampaign",hm.uuid,CreditHousing,result._1,result._2)
  }


  private def approve(hm : HousingPackageMessage): (Boolean,String) ={
    HousingCampaignApprover.allConditions.foldLeft(true,""){
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

object HousingCampaignApprover {
  val A = (hm: HousingPackageMessage) => if (hm.totalAmount > 6000000)  (true,"") else (false, "totalAmount Low")
  val B = (hm: HousingPackageMessage) => if (hm.totalRotative > 600000)  (true,"") else (false, "totalRotative Low")
  val C = (hm: HousingPackageMessage) => if (hm.maximumPayment > 500000)  (true,"") else (false, "maximumPayment Low")
  val D = (hm: HousingPackageMessage) => if (hm.extScore >= 2)  (true,"") else (false, "extScore Low")
  val E = (hm: HousingPackageMessage) => if (hm.intScore >=1 )  (true,"") else (false, "intScore Low")
  val F = (hm: HousingPackageMessage) => if (hm.activeDebs <=2 )  (true,"") else (false, "activeDebs High")
  val G = (hm: HousingPackageMessage) => if (!hm.penalty )  (true,"") else (false, "has penalty")
  val H = (hm: HousingPackageMessage) => if (hm.age < 80)  (true,"") else (false, "Age")

  val allConditions = List(A,B,C,D,E,F,G,H)

}
