package eci.edu.co
package kafka.approval

import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._
import entities.CreditRequest.CreditVehicle
import protos.VehiclePackageMessage.VehiclePackageMessage
import services.approve.VehicleApprover

import com.typesafe.scalalogging.Logger

import java.util.Properties

object VehicleApprovalConsumer extends App {
  val LOGGER = Logger("VehicleApprovalConsumer")
  val topic = s"Approval${CreditVehicle}-topic"
  LOGGER.info(s"Starting VehicleApprovalConsumer with Topic : ${topic}")


  val kafkaStreamProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9093")
    props.put("application.id", "Vehicle-approval-stream")
    props
  }

  val streams = new KafkaStreams(streamTopology, kafkaStreamProps)
  Runtime.getRuntime.addShutdownHook(new Thread(() => streams.close()))
  streams.start()

  val vehicleApprover = VehicleApprover()

  def streamTopology = {
    val streamsBuilder = new StreamsBuilder()
    streamsBuilder
      .stream[String, Array[Byte]](topic)
      .foreach(
        (key, value) => {
          LOGGER.info(s"Read message with key: $key")
          val vehiclePackage = VehiclePackageMessage.parseFrom(value)
          LOGGER.info(s"Deserialized message: $vehiclePackage")
          vehicleApprover.runApprovalEngine(vehiclePackage)
          LOGGER.info(s"Result saved ${vehiclePackage.uuid}")
        }
      )
    streamsBuilder.build()
  }

}
