package com.derby.nuke.dwarf

import java.util
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.pattern.ask
import akka.util.Timeout
import com.derby.nuke.dwarf.model.AdminJob
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.IOUtils

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

/**
  * Created by Passyt on 2017/9/7.
  */
object DwarfJobTest {

  def main(args: Array[String]): Unit = {
    val system: ActorSystem = ActorSystem("dwarf", ConfigFactory.load("cluster"));
    val jobForwarder = system.actorOf(Props[JobForwarder], "jobForwarder")

    Cluster(system) registerOnMemberUp {
//      TimeUnit.SECONDS.sleep(10)
      implicit val timeout = Timeout(3600 seconds)
      val futures: util.List[Future[AnyRef]] = new util.ArrayList[Future[AnyRef]]();
      (0 until 5).foreach({ i =>
        val job = new AdminJob
        job.setScript(IOUtils.toString(DwarfJobTest.getClass.getClassLoader.getResourceAsStream("admin.groovy")))
        job.setTaskScripts(Map("task" -> IOUtils.toString(DwarfJobTest.getClass.getClassLoader.getResourceAsStream("task.groovy"))).asJava)
        job.setProperties(Map("a" -> s"$i", "batch" -> "true").asJava.asInstanceOf[java.util.Map[String, AnyRef]])


        def future = ask(jobForwarder, job).mapTo[AnyRef]

        future onComplete {
          case Success(taskResult) =>
            taskResult
          case Failure(e) =>
        }

        futures.add(future)
      })

      def resutls = Await.result(Future.sequence(futures.asScala), timeout.duration).asJava

      println("***************************************")
      resutls.forEach { result =>
        println(result)
      }
    }
  }

}
