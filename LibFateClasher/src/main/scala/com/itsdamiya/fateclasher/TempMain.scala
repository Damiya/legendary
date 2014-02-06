package com.itsdamiya.fateclasher

import akka.actor._
import com.gvaneyck.rtmp.ServerInfo

object TempMain {
  def main(args: Array[String]) {
    val system = ActorSystem.create()
    val actor = system.actorOf(RTMPSClient.props(ServerInfo.NA), "rtmpsClient")
    actor ! "Hi"
  }
}