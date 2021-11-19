import
com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB
import eci.edu.co.entities.{Client, CreditRequest}
import eci.edu.co.services.callApis.CallAPIsAndSaveService
import eci.edu.co.services.DynamoService
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.Random
val f: Future[Int] = Future(0)

def getFutureString(waitingTime: Long): String = {
  Thread.sleep(waitingTime)
  waitingTime.toString
}

val future1 = Future(getFutureString(5000))
val future2 = Future(getFutureString(5000))


/*val start = System.nanoTime
CallAPIsService.callApis(CreditHousing, Json.parse("""{"hello": "client", "age": 42}""" ))
val end = System.nanoTime()
println(s"Time taken: ${(end - start) / 1000 / 1000 /1000} s")*/

/*
DynamoService.putItem(rad,CreditHousing, "1234",Json.parse("""{"hello": "dataCredit", "age": 42}"""))
DynamoService.putItem(rad,CreditFreeInvestment , "1234",Json.parse("""{"hello": "dataCredit", "age": 42}"""))

DynamoService.getItems(rad)*/

val l = List("A","B")

Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
Random.nextInt(l.length)
