package com.scrap.hackernews

import akka.actor.{ActorSystem, PoisonPill, Props}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps


/**
  * Created by sravi on 4/23/17.
  */
object StartCrawl extends App {
  val system = ActorSystem()
  val supervisor = system.actorOf(Props(new Supervisor(system)))

  supervisor ! Start()

  Await.result(system.whenTerminated, 10 minutes)

  supervisor ! PoisonPill
  system.terminate
}
