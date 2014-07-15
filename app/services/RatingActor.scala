package services

import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import akka.actor.Actor
import play.api.libs.json.Json
import utils.KeyStore
import play.Logger
import model.Result

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
	
	def processFilial(id: String): Result  = {
    	val ps: HttpEntity = ContentService.getContent("http://catalog.api.2gis.ru/profile?&version=1.3&key=" + KeyStore.KeyFof2GisApi + "&id=" + id);

	    val json = EntityUtils.toString(ps);
	    
	    Json.parse(json).validate[Result].get
    }    
	
}
