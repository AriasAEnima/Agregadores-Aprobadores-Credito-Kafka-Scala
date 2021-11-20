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


  private def approve(hm : HousingPackageMessage): (Boolean, List[String]) = {
    val allresults = HousingCampaignApprover.allConditions.flatMap(f => {
      val result = f(hm)
      result match {
        case "" => None
        case x => Some(x)
      }
    })
    if (allresults.length == 0) (true, List()) else (false, allresults)
  }
}

object HousingCampaignApprover {
  val A = (hm: HousingPackageMessage) => if (hm.totalAmount > 6000000)  ("") else ( "totalAmount Low")
  val B = (hm: HousingPackageMessage) => if (hm.totalRotative > 600000)  ("") else ( "totalRotative Low")
  val C = (hm: HousingPackageMessage) => if (hm.maximumPayment > 500000)  ("") else ( "maximumPayment Low")
  val D = (hm: HousingPackageMessage) => if (hm.extScore >= 2)  ("") else ( "extScore Low")
  val E = (hm: HousingPackageMessage) => if (hm.intScore >=1 )  ("") else ( "intScore Low")
  val F = (hm: HousingPackageMessage) => if (hm.activeDebs <=2 )  ("") else ( "activeDebs High")
  val G = (hm: HousingPackageMessage) => if (!hm.penalty )  ("") else ( "has penalty")
  val H = (hm: HousingPackageMessage) => if (hm.age < 80)  ("") else ( "Age")

  val allConditions = List(A,B,C,D,E,F,G,H)

}
