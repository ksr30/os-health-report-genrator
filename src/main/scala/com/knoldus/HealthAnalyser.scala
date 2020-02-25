package com.knoldus

import java.io.File

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import akka.pattern._
import akka.routing.RoundRobinPool

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class HealthAnalyser(path: String) {
  connector()
  def connector() = {
    val actorSystem = ActorSystem("firstActorSystem")
    val fileList: List[String] = fileFinder(path)
    val master = actorSystem.actorOf((RoundRobinPool(5)).props(Props[FileMiner]).withDispatcher("fixed-thread-pool"), "master")
    val listOfFuture: List[Future[actorDataStructure]] = futureListFinder(master, fileList, List())
    val futureResult: Future[actorDataStructure] = Future.sequence(listOfFuture).map(_.foldLeft(actorDataStructure(0, 0, 0)) { (acc, ele) => futureResultFinder(acc, ele) })
    futureResult
    val result = Await.result(futureResult, 1 second)
    println(result)


  }

  def fileFinder(path: String): List[String] = {
    val filePointer = new File(path)
    filePointer.listFiles.toList.map(_.toString)
  }

  def futureListFinder(master: ActorRef, fileList: List[String], futureList: List[Future[actorDataStructure]]): List[Future[actorDataStructure]] = {
    implicit val timeout = Timeout(5 second)
    fileList match {
      case Nil => futureList
      case first :: rest => {
        val futureLogfound = (master ? first).mapTo[actorDataStructure]
        futureListFinder(master, rest, futureLogfound :: futureList)
      }
    }
  }

  def futureResultFinder(accumelator: actorDataStructure, element: actorDataStructure): actorDataStructure = {
    actorDataStructure(accumelator.countError + element.countError, accumelator.countWarnings + element.countWarnings, accumelator.countInfo + element.countInfo)
  }


}
