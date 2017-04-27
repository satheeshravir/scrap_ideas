package com.scrap.hackernews

import java.net.URL

/**
  * Created by sravi on 4/24/17.
  */


case class Start()
case class Count(text: String)
case class Parse(url: URL)
case class Result()
case class Request(url: URL, requestType: String)
case class Response(response: String, responseType: String)
case class IndexFinished(url: URL, urls: List[URL])
case class RequestFailure(url: String, reason: Throwable)
case class FinalResult(failure: Map[String, Int])
