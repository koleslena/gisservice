package services


import org.apache.http.util.EntityUtils
import org.apache.http.HttpEntity
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import play.Logger
import akka.util.Timeout
import akka.actor.Props
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.actor.Actor
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import model.Result
import model.Id
import utils.UrlStore

case class city(name: String)

class FirmActor(fir: String) extends Actor {
	def receive = {
	  case city(name) =>
	    Logger.debug("Get firms in city {} ", name) 
	  	sender ! processCity(name)
	}
	
	def processCity(name: String): List[Result] = {
		val ps: HttpEntity = ContentService.getContent(UrlStore.urlForSearch(name, fir));

		if(ps != null) {
		    val json = EntityUtils.toString(ps);
			
		    val obj = Json.parse(json);
			
		    val listId = (obj \ "result").validate[List[Id]].get
		    
		    val system = ActorSystem("gisservice")
		    
		    implicit val timeout = Timeout(20.second)
		    
		    val listFuture = Future.traverse(listId){ f =>
		      (system.actorOf(Props (new RatingActor())) ? procId(f.id))
		    }
		    
		    val res = Await.result(listFuture, 10000 second).asInstanceOf[List[Result]]
		    
		    res
		} else
			null
	}
}