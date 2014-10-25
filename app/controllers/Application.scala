package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import akka.util.Timeout
import akka.actor.Props
import akka.actor.ActorSystem
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import services.procId
import services.RatingActor
import services.ContentService
import model.Result
import model.Id
import services.city
import services.FirmActor
import scala.collection.mutable.MutableList

/**
 * Created by elenko on 22.06.14.
 */
object Application extends Controller {
	
  def getFirms(fir: String) = Action.async {
       
    val system = ActorSystem("gisservice")
    
    implicit val timeout = Timeout(1000.second)
    
    val listCity: List[String] = List("Новосибирск", "Омск", "Томск", "Кемерово", "Новокузнецк")
    
    val listFuture = Future.traverse(listCity){ name =>
      (system.actorOf(Props (new FirmActor(fir))) ? city(name))
    }.mapTo[List[List[Result]]]
    
    listFuture.map {
      res => Ok(toJson(res.flatten.sorted))
    }
  }
}
