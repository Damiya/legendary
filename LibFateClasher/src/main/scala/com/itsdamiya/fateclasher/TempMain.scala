package com.itsdamiya.fateclasher

import com.gvaneyck.rtmp.ServerInfo
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object TempMain {
  def main(args: Array[String]) {
    implicit val timeout = Timeout(5 seconds)

    val system = ActorSystem()
    val server = ServerInfo.NA

    val actor = system.actorOf(LoginQueueClient(server),"gogoboots")
    val future = actor ? LoginCmd("","")
    readLine()
  }
}