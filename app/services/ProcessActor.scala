package services

import akka.actor.Actor
import scala.collection.immutable.List
import akka.actor.Props
import akka.actor.ActorRef
import model.Result
import scala.collection.mutable.MutableList
import model.Id
import play.Logger

case class ListCity(fir:String, list: List[String])
case class ListId(list: List[Id])

case class ListIdAnswer(res: Result)

class ProcessActor extends Actor {
	
	var countReqs = 0
	var expectedReqs = 0
	var selfSender:ActorRef = null
	val resList: MutableList[Result] = new MutableList() 
	
	def receive = {
	  case ListCity(fir, list) =>
	    selfSender = sender
	    list.foreach { name: String  =>
	      	context.actorOf(Props (new FirmActor(fir))) ! city(name)
	    }
	    
	  case ListId(list) =>
	    expectedReqs += list.size
	    list.foreach { modelId: Id  =>
	      	context.actorOf(Props (new RatingActor())) ! procId(modelId.id)
	    }
	    
	  case ListIdAnswer(res) =>
	    countReqs += 1
	    if(res != null)
	      resList.+=(res) 
	    if(countReqs.equals(expectedReqs))
	      selfSender  ! resList 	      
	}

}