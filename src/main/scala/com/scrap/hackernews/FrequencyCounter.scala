package com.scrap.hackernews

import java.net.URL

import akka.actor.{Actor, ActorRef}

/**
  * Created by sravi on 4/23/17.
  */
class FrequencyCounter(supervisor: ActorRef) extends Actor{
  var distribution = Map.empty[String, Int]
  def stopWords: List[String] = List("is", "as", "i", "at", "from", "to")

  def receive: Receive = {
    case Count(content) =>
    process(content)
    case Result() =>
      supervisor ! FinalResult(distribution)
  }

  def process(text:String) = {
    text.split(("\\W+")).filter(e => !stopWords.contains(e))
      .foldLeft(distribution){
        (count, word) => count + (word -> (count.getOrElse(word, 0) + 1))}
  }

}
