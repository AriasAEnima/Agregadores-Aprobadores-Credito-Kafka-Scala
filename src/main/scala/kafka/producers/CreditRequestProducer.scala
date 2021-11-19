package eci.edu.co
package kafka.producers

import entities.CreditRequest

import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import protos.RequestMessage.RequestMessage

import java.util.Properties

object CreditRequestProducer extends App {
  val LOGGER = Logger("TransactionsProducer")

  LOGGER.info("Starting TransactionsProducer")

  val kafkaProducerProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9093")
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[ByteArraySerializer].getName)
    props
  }

  val producer = new KafkaProducer[String, Array[Byte]](kafkaProducerProps)

  while (true) {
    val clientRequestMessage: RequestMessage = CreditRequest.randomClientRequestMessage.toRequestMessage
    LOGGER.info(s"Message to be send: $clientRequestMessage with key ${clientRequestMessage.uuid}")
    Thread.sleep(2500)
    val messageSent = producer.send(new ProducerRecord[String, Array[Byte]]("request-topic", clientRequestMessage.uuid,clientRequestMessage.toByteArray)).get()
    LOGGER.info(s"Message Sent - $messageSent")
  }
  producer.close()

}
