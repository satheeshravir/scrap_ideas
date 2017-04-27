package com.scrap.hackernews

import akka.actor.{Actor, ActorRef}
import org.apache.commons.validator.routines.UrlValidator
import org.jsoup.Jsoup
import java.net.URL

import scala.collection.JavaConverters._
/**
  * Created by sravi on 4/24/17.
  */
class HTMLRequester(frequencyCounter: ActorRef, supervisor: ActorRef) extends Actor  {
    val urlValidator = new UrlValidator()

    def receive: Receive = {
      case Request(url, responseType) =>
        var text = getResponse(url)
        supervisor ! Response(text:String, responseType)
    }

    def getResponse(uRL: URL):String = {
      println("Fetching the URL "+uRL.toString)
      Jsoup.connect(uRL.toString).ignoreContentType(true)
      .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").execute().parse().text()

  }

}
