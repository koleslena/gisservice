package services

import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import play.Logger

/**
 * Created by elenko on 12.07.14.
 */
object ContentService {
	def getContent(url: String): HttpEntity = {
	    try {
	    	
		    val req = new HttpGet(url);
		    val client = new DefaultHttpClient();
		
		    val response = client.execute(req);
		
		    Logger.debug("Get url status {}, {} ", url, response.getStatusLine().getStatusCode().toString);
		
		    if(response.getStatusLine().getStatusCode().equals(200))
		    	response.getEntity();
		    else
		    	null
	    } catch {
	    	case e: Exception => {
	    	  Logger.error(e.getMessage())
	    	  null
	    	}
	    }
	}
}