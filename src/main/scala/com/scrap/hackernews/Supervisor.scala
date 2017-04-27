package com.scrap.hackernews

import java.net.URL

import akka.actor.{Actor, Props, _}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import play.api.libs.json._

/**
  * Created by sravi on 4/23/17.
  */


class Supervisor(system: ActorSystem) extends Actor {
  val frequencyCounter = context actorOf Props(new FrequencyCounter(self))
  val htmlParser = context actorOf Props(new HTMLRequester(frequencyCounter, self))
  val process = "Process next url"
  val maxArticles = 1000
  val maxRetries = 2
  implicit val timeout = Timeout(20 seconds)
  val tick =
    context.system.scheduler.schedule(0 millis, 1000 millis, self, process)
  var currentId:Long = -1
  val maxURL = "https://hacker-news.firebaseio.com/v0/maxitem.json"
  var numVisited = 0
  var httpRequestMap = Map.empty[String, ActorRef]
  val url = "https://google.com"
  var scrapCounts = Map.empty[URL, Int]

  def receive: Receive = {
    case Start() =>
      println(s"starting")
      (htmlParser ? Request(new URL(maxURL), "id")).mapTo[Response].recoverWith {case e => Future{RequestFailure(maxURL, e)}}.pipeTo(self)
    case Response(text, responseType) =>
      responseType match {
        case "id" =>
          currentId = text.toLong
          //val url = "https://hacker-news.firebaseio.com/v0/item/" + text + ".json"
          ((context actorOf Props(new HTMLRequester(frequencyCounter, self))) ? Request(new URL(url), "json")).mapTo[Response]
            .recoverWith { case e => Future {
              RequestFailure(url, e)
            }
            }
            .pipeTo(self)
        case "json" =>
          println(text + " sdfsdfsdfsfsfsfsfsf")
          val jsonObj: JsValue = Json.parse(text)
          println(jsonObj \ "type")
          val articleType: String = (jsonObj \ "type").toString()
          //val url: String = (jsonObj \ "url").toString()
          if (articleType == "story")
            ((context actorOf Props(new HTMLRequester(frequencyCounter, self))) ? Request(new URL(url), "json")).mapTo[Response]
              .recoverWith { case e => Future {
                RequestFailure(url, e)
              }
              }
              .pipeTo(self)
        case "html" =>
          frequencyCounter ! Count(text)
          numVisited += 1
          currentId -= 1
          //val url = "https://hacker-news.firebaseio.com/v0/item/" + currentId.toString() + ".json"
          if (numVisited == 10) {
            self ! PoisonPill
            system.terminate()
          }

          ((context actorOf Props(new HTMLRequester(frequencyCounter, self))) ? Request(new URL(url), "json")).mapTo[Response].recoverWith { case e => Future {
            RequestFailure(url, e)
          }
          }.pipeTo(self)
      }
    case FinalResult(dist) =>
      println(dist)
    case RequestFailure(url, e) =>
      println("Failed to fetch "+url+" "+e)
      ((context actorOf Props(new HTMLRequester(frequencyCounter, self))) ? Request(new URL(url), "json")).mapTo[Response].recoverWith { case e => Future {
        RequestFailure(url, e)
      }
      }.pipeTo(self)


  }
  def countVisits(url: URL): Unit = scrapCounts += (url -> (scrapCounts.getOrElse(url, 0) + 1))

}
