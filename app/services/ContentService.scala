package services

import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import play.Logger
import play.api.libs.json.Json
import play.api.libs.json.JsValue


/**
 * Created by elenko on 12.07.14.
 */
object ContentService {
	def getContent(url: String): Option[HttpEntity] = {
	    try {
		    val req = new HttpGet(url)
		    val client = new DefaultHttpClient()
		
		    val response = client.execute(req)
		
		    Logger.debug("Get url status {}, {} ", url, response.getStatusLine().getStatusCode().toString)
		
		    if(response.getStatusLine().getStatusCode().equals(200))
		    	Some(response.getEntity())
		    else
		    	None
	    } catch {
	    	case e: Exception => {
	    	  Logger.error(e.getMessage())
	    	  None
	    	}
	    }
	}
	
	def getJsValue(url:String):Option[JsValue] = {
		val ps = getContent(url)

		val json = ps match {
		  case Some(p) => {
		    Some(Json.parse(EntityUtils.toString(p)))
		  }
		  case None => None
		}
		json
	}
}