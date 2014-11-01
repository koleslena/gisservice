package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import akka.util.Timeout
import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import model.Result
import services.{ListCity, ProcessActor}

/**
 * Created by elenko on 22.06.14.
 */
object Application extends Controller {
	
  def getFirms(fir: String) = Action.async {
       
    val system = ActorSystem("gisservice")
    
    implicit val timeout = Timeout(1000.second)
    
    val listCity: List[String] = List("Новосибирск", "Омск", "Томск", "Кемерово", "Новокузнецк")
    
    val listFuture = (system.actorOf(Props[ProcessActor]) ? ListCity(fir, listCity)).mapTo[List[model.Result]]
    
    listFuture.map { 
       o: List[model.Result] => Ok(toJson(o.sorted))
	}.recover { 
	  case _ => Ok(toJson("")) 
	} 

  }
}
