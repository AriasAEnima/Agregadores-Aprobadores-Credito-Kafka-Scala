package eci.edu.co
package entities

import scala.util.Random

import protos.ClientMessage.ClientMessage

case class Client(id: Long, name : String, customerIdentType : String, age : Int ){

  def toClientMessage: ClientMessage = ClientMessage(id,name,customerIdentType,age)

  override def toString: String = s"(Id ${id}}, Name: ${name} , CustomerIdentType : ${customerIdentType}, Age : ${age}";

}

object Client{
  def randomCustomer(): Client ={
    Client(randomId,randomName,randomCustomerIdentType,randomAge)
  }

  def fromClientMessage( cm : ClientMessage) : Client = Client(cm.id,cm.name,cm.custIdentType, cm.age)

  val customerIdentType = List("CC","CE","NIT");
  def randomId : Long = Random.between(1000000000L, 9999999999L)

  def randomAge : Int = Random.between(18,80)

  def randomCustomerIdentType: String = {
    customerIdentType(Random.nextInt(customerIdentType.length))
  }

  def randomName : String = {
    " FistName"+Random.between(1, 2000) + " LastName "+Random.between(1, 2000)
  }

}