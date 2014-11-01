package services

import org.apache.http.HttpEntity
import org.apache.http.util.EntityUtils
import akka.actor.Actor
import play.api.libs.json.Json
import play.Logger
import model.Result
import akka.actor.actorRef2Scala
import utils.UrlStore

/**
 * Created by elenko on 12.07.14.
 */

case class procId(id: String)

class RatingActor extends Actor  {
	def receive = {
	  case procId(id) =>
	    Logger.debug("Get info for firm id {} ", id) 
	  	sender ! processFilial(id)
	}
	
	def processFilial(id: String): ListIdAnswer  = {
		try {
			val ps: HttpEntity = ContentService.getContent(UrlStore.urlForProfile(id)).get
			
		    val json = EntityUtils.toString(ps);
		    
		    val res = Json.parse(json).validate[Result].get
		    
		    ListIdAnswer(res)
		} catch {
	    	case e: Exception => {
	    	  Logger.error(e.getMessage())
	    	  ListIdAnswer(Result("","",""))
	    	}
	    }
    }    
}
