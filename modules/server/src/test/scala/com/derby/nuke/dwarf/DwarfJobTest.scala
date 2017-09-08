package com.derby.nuke.dwarf

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.derby.nuke.dwarf.model.AdminJob
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.IOUtils

import scala.collection.JavaConverters._

/**
  * Created by Passyt on 2017/9/7.
  */
object DwarfJobTest {

  def main(args: Array[String]): Unit = {
    val system: ActorSystem = ActorSystem("dwarf", ConfigFactory.load("cluster"));
    val jobForwarder = system.actorOf(Props[JobForwarder], "jobForwarder")

    Cluster(system) registerOnMemberUp {
      (0 until 100).foreach({ i =>
        val job = new AdminJob
        job.setScript(IOUtils.toString(DwarfJobTest.getClass.getClassLoader.getResourceAsStream("test.groovy")))
        job.setProperties(Map("a" -> s"$i").asJava.asInstanceOf[java.util.Map[String, AnyRef]])
        jobForwarder ! job
      })
    }
  }

}
