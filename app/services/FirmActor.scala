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
import akka.pattern.pipe

case class city(name: String)

class FirmActor(fir: String) extends Actor {
	def receive = {
	  case city(name) =>
	    Logger.debug("Get firms in city {} ", name) 
	  	processCity(name) pipeTo sender
	}
	
	def processCity(name: String) = {
	  	try {
			val ps: HttpEntity = ContentService.getContent(UrlStore.urlForSearch(name, fir)).get
	
		    val json = EntityUtils.toString(ps);
				
		    val obj = Json.parse(json);
			
		    val listId = (obj \ "result").validate[List[Id]].get
			    
		    val system = ActorSystem("gisservice")
			    
		    implicit val timeout = Timeout(20.second)
			    
		    val listFuture = Future.traverse(listId){ f =>
		      (system.actorOf(Props (new RatingActor())) ? procId(f.id))
		    }.mapTo[List[Option[Result]]]
			    
		    listFuture
	  	} catch {
	    	case e: Exception => {
	    	  Logger.error(e.getMessage())
			  val future = Future {
				  List(None)
				}
			  future
	    	}
		}
	}
}