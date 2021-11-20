package eci.edu.co
package services
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClient}
import com.amazonaws.services.dynamodbv2.document.spec.{PutItemSpec, QuerySpec}
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Expected, Item, Table}
import com.typesafe.scalalogging.Logger
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable

case class DynamoService() {
  val LOGGER = Logger("DynamoService")

  lazy val client: DynamoDB = {
    val result = new AmazonDynamoDBClient(new BasicAWSCredentials("test", "test"))
    result.withEndpoint("http://localhost:8000")
    new DynamoDB(result)
  }

  def getItems( tableName: String, rquid: String): List[(String,JsValue)] ={
    val table = client.getTable(tableName)
    val spec = new QuerySpec()
      .withKeyConditionExpression("rqUID = :rqUID")
      .withValueMap(new ValueMap()
        .withString(":rqUID", rquid))
    val items = table.query(spec).iterator()
    var ans : mutable.ArraySeq[(String,JsValue)]  = mutable.ArraySeq()
    while(items.hasNext) {
      val item = items.next()
      ans = ans :+ ((item.getString("dataSource") , Json.parse(item.getJSON("dataSourceResponse"))))
    }
    LOGGER.info(s"Contents found : ${ans.length}")
    ans.toList
  }


  def putItem( tableName : String , rquid: String,  dataSource: String, customerId : String, obj : JsValue): Unit ={
    val table = client.getTable(tableName)
    val item = new Item().withPrimaryKey("rqUID",rquid)
      .withString( "dataSource-customerId", dataSource+"-"+customerId)
      .withString( "dataSource", dataSource)
      .withJSON("dataSourceResponse",Json.stringify(obj))
    table.putItem(item)
  }

  def putItem( tableName : String , rquid: String, creditType: String, approved: Boolean, reasons : List[String]): Unit ={
    val table = client.getTable(tableName)
    val item = new Item().withPrimaryKey("rqUID",rquid)
      .withString("creditType", creditType)
      .withString( "Approved", approved.toString)
      .withString( "reasons", reasons.mkString("[", ",", "]"))
    LOGGER.info(s"Item ${item.toJSONPretty}")
    table.putItem(item)
  }

}

