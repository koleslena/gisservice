package services

import play.api._
import play.api.mvc._
import play.Logger
import akka.util.Timeout
import akka.actor.Props
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
		    val obj = ContentService.getJsValue(UrlStore.urlForSearch(name, fir));
			
		    val listId = (obj \ "result").validate[List[Id]].get
			    
		    implicit val timeout = Timeout(20.second)
			    
		    val listFuture = Future.traverse(listId){ f =>
		      (context.actorOf(Props (new RatingActor())) ? procId(f.id))
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