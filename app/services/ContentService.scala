package services

import play.Logger
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.ws._
import play.api.Play.current
import scala.util.Try
import akka.actor.Actor
import akka.pattern.pipe
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure

/**
 * Created by elenko on 12.07.14.
 */
case class UrlSearch(url: String)
case class Response(value: Try[JsValue])

class ContentService extends Actor {
	def receive = {
	  case UrlSearch(url) => {
      Logger.debug("Get utl {} ", url)

      implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

      val lastSender = sender

      val ps = Try(WS.url(url).get()) match {
        case Success(res) => {
          res.onComplete {
            case Success(response) =>
              Logger.debug("Get url status {}, {} ", url, response.statusText)
              lastSender ! Try(response.json)
            case Failure(er) => {
              Logger.error("Get url status {}, {} ", url, er.getMessage())
              lastSender ! Failure
            }
          }
        }
        case Failure(er) => {
          Logger.error("Get url status {}, {} ", url, er.getMessage())
          lastSender ! Failure(er)
        }
      }
    }
	}
}