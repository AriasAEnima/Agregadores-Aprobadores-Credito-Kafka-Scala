package eci.edu.co
package kafka.approval

import com.typesafe.scalalogging.Logger

import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._
import entities.CreditRequest.CreditHousing
import protos.HousingPackageMessage.HousingPackageMessage
import services.approve.HousingCampaignApprover

import java.util.Properties


object HousingCampaignApprovalConsumer extends App {
  val LOGGER = Logger("HousingCampaignApprovalConsumer")
  val topic = s"Approval${CreditHousing}-topic"
  LOGGER.info(s"Starting HousingCampaignApprovalConsumer with Topic : ${topic}")


  val kafkaStreamProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9093")
    props.put("application.id", "Housing-campaign-approval-stream")
    props
  }

  val streams = new KafkaStreams(streamTopology, kafkaStreamProps)
  Runtime.getRuntime.addShutdownHook(new Thread(() => streams.close()))
  streams.start()
  val housingApprover = HousingCampaignApprover()

  def streamTopology = {
    val streamsBuilder = new StreamsBuilder()
    streamsBuilder
      .stream[String, Array[Byte]](topic)
      .foreach(
        (key, value) => {
          LOGGER.info(s"Read message with key: $key")
          val housingPackage = HousingPackageMessage.parseFrom(value)
          LOGGER.info(s"Deserialized message: $housingPackage")
          housingApprover.runApprovalEngine(housingPackage)
          LOGGER.info(s"Result saved ${housingPackage.uuid}")
        }
      )
    streamsBuilder.build()
  }
}