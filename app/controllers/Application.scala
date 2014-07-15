package controllers


import org.apache.http.util.EntityUtils
import org.apache.http.HttpEntity

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

import utils.KeyStore

/**
 * Created by elenko on 22.06.14.
 */
object Application extends Controller {
	
  def getFirms(fir: String) = Action {
       
    val ps: HttpEntity = ContentService.getContent("http://catalog.api.2gis.ru/search?key=" + KeyStore.KeyFof2GisApi + "&what=" + fir + "&where=Москва&version=1.3");

    val json = EntityUtils.toString(ps);
	
    val obj = Json.parse(json);
	
    val listId = (obj \ "result").validate[List[Id]].get
    
    val system = ActorSystem("gisservice")
    
    implicit val timeout = Timeout(20.second)
    
    val listFuture = Future.traverse(listId){ f =>
      (system.actorOf(Props (new RatingActor())) ? procId(f.id))
    }
    
    val res = Await.result(listFuture, 10000 second).asInstanceOf[List[Result]]
    
	Ok(toJson(res.sorted.reverse))
  }
}