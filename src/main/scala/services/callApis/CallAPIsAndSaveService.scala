package eci.edu.co
package services.callApis

import com.typesafe.scalalogging.Logger
import play.api.libs.json.Json

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import commands.CommandCallAPI
import services.DynamoService
import entities.CreditRequest.{CreditVehicle, CreditHousing}
import entities.CreditRequest

import scala.concurrent.ExecutionContext.Implicits.global


case class CallAPIsAndSaveService() {
  val LOGGER = Logger("CallAPIsAndSaveService")
  val dynamoService = DynamoService()
  val tableDataSources = "DataSourcesResponses"
  val DataSourcesRequired: Map[String, List[CommandCallAPI]] =
    Map(CreditHousing -> List(CallInternalAPI, CallDataCreditAPI),
      CreditVehicle -> List(CallDataCreditAPI)
    )

  def callApis(clientReq: CreditRequest): Double = {
    val start = System.nanoTime
    val apisToCall: Option[List[CommandCallAPI]] = DataSourcesRequired.get(clientReq.creditType)
    clientReq.clients.foreach(client => {
      val body = Json.parse(s"""{"name": "${client.name}", "amount": "${client.id}" }""")
      apisToCall match {
        case Some(x) => {
          val fut = x.map(apiCall => Future(apiCall.callAPI(body)))
          val allfut = Future.sequence(fut)
          val results = Await.result(allfut, Duration.Inf)
          results.foreach(result => dynamoService.putItem(tableDataSources,clientReq.uuid, result._1, client.id.toString, result._2))
        }
        case None =>
      }
    })
    LOGGER.info(s"Request: ${clientReq.uuid} has DataSources on Dynamo")
    (System.nanoTime - start) / 1e9d
  }


}
