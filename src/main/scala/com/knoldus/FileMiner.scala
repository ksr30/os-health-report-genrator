package com.knoldus

import akka.actor.Actor
import akka.pattern.pipe

import scala.concurrent.Future
import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global

class FileMiner extends Actor{
  var noOfErrors:Int =0
  var noOfWarnings:Int = 0
  var noOfInfo:Int = 0
  def receive:Receive={
  case file=> val filePointer=Source.fromFile(s"$file")
      val listOfLines=filePointer.getLines.toList
      val totalLogFind:actorDataStructure=tagFinder(listOfLines)
      futureWrapper(totalLogFind).pipeTo(context.sender())
  }
  def tagFinder(listOfLines: List[String]):actorDataStructure={
    listOfLines match {
      case Nil=> actorDataStructure(noOfErrors,noOfWarnings,noOfInfo)
      case head :: rest if head.contains("[ERROR]") => noOfErrors+=1; tagFinder(rest)
      case head :: rest if head.contains("[WARN]") => noOfWarnings+=1; tagFinder(rest)
      case head :: rest if head.contains("[INFO]") => noOfInfo+=1; tagFinder(rest)
      case _ :: rest => tagFinder(rest)
    }
  }

  def futureWrapper(totalLogFind:actorDataStructure):Future[actorDataStructure]={
    Future{totalLogFind}
  }

}
