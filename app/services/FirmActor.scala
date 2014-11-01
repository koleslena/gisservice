package services


import org.apache.http.util.EntityUtils
import org.apache.http.HttpEntity
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import play.Logger
import akka.util.Timeout
import akka.actor.{Props, Actor}
import akka.pattern.ask
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
	
	def processCity(name: String): ListId = {
		try {
			val ps: HttpEntity = ContentService.getContent(UrlStore.urlForSearch(name, fir));
	
			if(ps != null) {
			    val json = EntityUtils.toString(ps);
				
			    val obj = Json.parse(json);
				
			    val listId = (obj \ "result").validate[List[Id]].get
			    
			    ListId(listId)
			} else
				ListId(List())
		} catch {
	    	case e: Exception => {
	    	  Logger.error(e.getMessage())
	    	  ListId(List())
	    	}
	    }
	}
}