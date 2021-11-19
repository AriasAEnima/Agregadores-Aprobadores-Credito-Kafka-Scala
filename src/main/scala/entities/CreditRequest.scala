package eci.edu.co
package entities

import protos.ClientMessage.ClientMessage
import protos.RequestMessage.RequestMessage
import org.apache.kafka.common.Uuid

import scala.util.Random


case class CreditRequest(uuid: String, creditType: String, amount: Int, clients: Seq[Client]) {

  def toRequestMessage: RequestMessage = {
    RequestMessage(uuid, creditType, amount, getClientsMessages)
  }

  def getClientsMessages: Seq[ClientMessage] = clients.map(c => c.toClientMessage)

  override def toString: String = s"(UUID : ${uuid}, CreditType: ${creditType}, CustomerIDs : ${clients.map(c => c.id)} ,Amount: ${amount})";
}


object CreditRequest {
  def randomClientRequestMessage: CreditRequest = {
    CreditRequest(randomUuid, randomCreditType, randomAmount, randomClients)
  }

  def randomClients: Seq[Client] = {
    Seq(Client.randomCustomer(), Client.randomCustomer())
  }

  def fromClientsMessages(cms: Seq[ClientMessage]): Seq[Client] = cms.map(cm => Client.fromClientMessage(cm))

  def fromRequestMessage(req: RequestMessage): CreditRequest = {
    CreditRequest(req.uuid, req.creditType, req.amount, fromClientsMessages(req.clients))
  }

  val CreditHousing = "Credit_Housing"
  val CreditVehicle = "Credit_Vehicle"
  val CreditType = List(CreditVehicle, CreditHousing )

  def randomCreditType: String = CreditType(Random.nextInt(CreditType.length))

  def randomAmount: Int = Random.between(1000000, 20000000)

  def randomUuid: String = Uuid.randomUuid().toString

}