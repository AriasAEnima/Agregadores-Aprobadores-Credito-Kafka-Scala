package eci.edu.co
package kafka.aggregators

import com.typesafe.scalalogging.Logger
import entities.CreditRequest
import protos.RequestMessage.RequestMessage

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._
import protos.ReadyDataSourcesMessage.ReadyDataSourcesMessage

import java.util.Properties
import services.callApis.CallAPIsAndSaveService


object AggregatorStreamer extends App {
  val LOGGER = Logger("Aggregator")
  LOGGER.info("Starting Aggregator")

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
    props.put("application.id", "approval-stream-1")
    props.put("auto.offset.reset", "latest")
    props
  }

  val streams = new KafkaStreams(streamTopology, kafkaStreamProps)
  Runtime.getRuntime.addShutdownHook(new Thread(() => streams.close()))
  streams.start()

  val producer = new KafkaProducer[String, Array[Byte]](kafkaProducerProps)

  val callApisService = CallAPIsAndSaveService()

  def streamTopology = {
    val streamsBuilder = new StreamsBuilder()
    streamsBuilder
      .stream[String, Array[Byte]]("request-topic")
      .foreach(
        (key, value) => {
          LOGGER.info(s"Read message with key: $key")
          val clientRequestMessage = RequestMessage.parseFrom(value)
          val clientRequest = CreditRequest.fromRequestMessage(clientRequestMessage)
          LOGGER.info(s"Deserialized message: $clientRequest")
          val duration = callApisService.callApis(clientRequest)
          val readyMessage = ReadyDataSourcesMessage(clientRequest.uuid, clientRequestMessage.clients,duration)
          val topic = s"packer${clientRequest.creditType}-topic"
          LOGGER.info(s"Message to be send: $readyMessage with key ${readyMessage.uuid} to Topic : ${topic}")
          val messageSent = producer.send(new ProducerRecord[String, Array[Byte]](topic,readyMessage.uuid,readyMessage.toByteArray)).get()
          LOGGER.info(s"Message Sent - $messageSent")
        }
      )
    streamsBuilder.build()
  }
}
