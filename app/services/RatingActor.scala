package services

import akka.actor.Actor
import play.Logger
import model.Result
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
	
	def processFilial(id: String): Option[Result]  = {
		try {
		    Some(ContentService.getJsValue(UrlStore.urlForProfile(id)).validate[Result].get)		  
		} catch {
	    	case e: Exception => {
	    	  Logger.error(e.getMessage())
	    	  None
	    	}
	    }
    }    
}
