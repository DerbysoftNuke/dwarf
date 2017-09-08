package com.derby.nuke.dwarf

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by Passyt on 2017/9/4.
  */
object DwarfApplication extends App {

  val port = if (args.isEmpty) "0" else args(0)
  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
    withFallback(ConfigFactory.load("cluster"))

  ActorSystem("dwarf", config);

}
