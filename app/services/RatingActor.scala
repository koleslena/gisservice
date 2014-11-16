package services

import akka.actor.Actor
import play.Logger
import model.Result
import utils.UrlStore
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import akka.actor.Props
import play.api.libs.json.JsValue
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask

/**
 * Created by elenko on 12.07.14.
 */

case class procId(id: String)

class RatingActor extends Actor  {
    
	def receive = {
	  case procId(id) =>
	    Logger.debug("Get info for firm id {} ", id)
	    implicit val timeout = Timeout(2000.second)

      val lastSender = sender
	    
	    (context.actorOf(Props (new ContentService())) ? UrlSearch(UrlStore.urlForProfile(id)))
	    .mapTo[Try[JsValue]]
	    .map {
        case Success(v) => lastSender ! processFilial(v)
        case Failure(e) =>
          Logger.error(e.getMessage())
          lastSender ! None
	    }
	}
	
	def processFilial(res: JsValue): Option[Result]  = {
	  res.validate[Result] match {
	            case sresult: JsSuccess[Result] => Some(sresult.get)
	            case error: JsError =>
	              Logger.error(JsError.toFlatJson(error).toString())
	              None
	          }
    }
}
