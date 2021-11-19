package eci.edu.co
package kafka.packers

import com.typesafe.scalalogging.Logger
import protos.RequestMessage.RequestMessage

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._
import entities.CreditRequest.CreditVehicle
import services.packers.VehiclePacker
import protos.ReadyDataSourcesMessage.ReadyDataSourcesMessage

import java.util.Properties

object VehiclePackerStreamer extends App {
  val LOGGER = Logger("VehiclePackerStreamer")
  val topic = s"packer${CreditVehicle}-topic"
  LOGGER.info(s"Starting VehiclePackerStreamer with Topic : ${topic} ")

  //
  val kafkaProducerProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9093")
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[ByteArraySerializer].getName)
    props
  }

  val kafkaStreamProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9093")
    props.put("application.id", "packer-Vehicle-stream-1")
    props.put("auto.offset.reset", "latest")
    props
  }

  val streams = new KafkaStreams(streamTopology, kafkaStreamProps)
  Runtime.getRuntime.addShutdownHook(new Thread(() => streams.close()))
  streams.start()

  val producer = new KafkaProducer[String, Array[Byte]](kafkaProducerProps)

  val vehiclePacker = VehiclePacker()

  def streamTopology = {
    val streamsBuilder = new StreamsBuilder()
    streamsBuilder
      .stream[String, Array[Byte]](topic)
      .foreach(
        (key, value) => {
          LOGGER.info(s"Read message with key: $key ")
          val readyMessage = ReadyDataSourcesMessage.parseFrom(value)
          LOGGER.info(s"Deserialized message: $readyMessage")
          val vehiclePackageMessage = vehiclePacker.toVehicleMessage(readyMessage.uuid,readyMessage.clients)
          val toTopic = s"Approval${CreditVehicle}-topic"
          LOGGER.info(s"Message to be send: $vehiclePackageMessage with key ${vehiclePackageMessage.uuid} to Topic : ${toTopic}")
          val messageSent = producer.send(new ProducerRecord[String, Array[Byte]](toTopic, vehiclePackageMessage.uuid,vehiclePackageMessage.toByteArray)).get()
          LOGGER.info(s"Message Sent - $messageSent")
        }
      )
    streamsBuilder.build()
  }
}
