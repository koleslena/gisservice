package services

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success
import akka.actor.Actor
import akka.actor.Props
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import model.Id
import model.Result
import play.Logger
import play.api._
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import utils.UrlStore
import scala.util.Try
import akka.actor.ActorRef

case class city(name: String)

class FirmActor(fir: String) extends Actor {
  
	def receive = {
	  case city(name) => {
      Logger.debug("Get firms in city {} ", name)
      implicit val timeout = Timeout(2000.second)

      val lastSender = sender

      (context.actorOf(Props (new ContentService())) ? UrlSearch(UrlStore.urlForSearch(name, fir)))
        .mapTo[Try[JsValue]]
        .map { res =>
        Logger.debug("Get firms in city on complete {} ")
        res match {
          case Success(v) => processCity(v) pipeTo lastSender
          case Failure(e) => {
            Logger.error(e.getMessage())
            defaultRes pipeTo lastSender
          }
        }
      }
    }
	}

	val defaultRes = Future {
					  List(None)
					}
	def processCity(res: JsValue): Future[List[Option[Result]]] = {
    (res \ "result").validate[List[Id]] match {
      case sId: JsSuccess[List[Id]] => {
        implicit val timeout = Timeout(2000.second)

        val listFuture = Future.traverse(sId.get){ f =>
          (context.actorOf(Props (new RatingActor())) ? procId(f.id))
        }.mapTo[List[Option[Result]]]

        listFuture
      }
      case error: JsError => {
        Logger.error(JsError.toFlatJson(error).toString())
        defaultRes
      }
    }
	}
}