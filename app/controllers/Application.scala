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
import model.Result
import services.ListCity
import scala.collection.mutable.MutableList
import services.ProcessActor

/**
 * Created by elenko on 22.06.14.
 */
object Application extends Controller {
	
  def getFirms(fir: String) = Action {
       
    val system = ActorSystem("gisservice")
    
    implicit val timeout = Timeout(1000.second)
    
    val listCity: List[String] = List("Новосибирск", "Омск", "Томск", "Кемерово", "Новокузнецк")
    
    val listFuture = system.actorOf(Props[ProcessActor]) ? ListCity(fir, listCity) 
    
    val res = Await.result(listFuture, 1000000 second).asInstanceOf[MutableList[Result]]
    
	Ok(toJson(res.sorted))
  }
}
