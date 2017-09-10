package com.derby.nuke.dwarf.executor

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import com.derby.nuke.dwarf.JobParameter
import com.derby.nuke.dwarf.executor.ScriptWorker.ScriptMessage
import com.derby.nuke.dwarf.model.AdminJob

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by Passyt on 2017/9/5.
  */
class AdminJobExecutor extends Actor with ActorLogging {

  val scriptWorkers = context.actorOf(FromConfig.props(Props[ScriptWorker]), "scriptWorkerRouter")
  //  val scriptWorkers = context.actorOf(FromConfig.props(), "scriptWorkerRouter")
  //  val scriptWorkers = context.actorOf(
  //    ClusterRouterPool(AdaptiveLoadBalancingPool(
  //      MixMetricsSelector), ClusterRouterPoolSettings(
  //      totalInstances = 100, maxInstancesPerNode = 3,
  //      allowLocalRoutees = false)).props(Props[ScriptWorker]),
  //    name = "scriptWorkerRouter")

  override def receive = {
    case job: AdminJob =>
      implicit val timeout = Timeout(5 seconds)

      def adminFuture = ask(scriptWorkers, new ScriptMessage(new JobParameter(job.getScript, job.getProperties.asScala.toMap))).mapTo[AnyRef]

      adminFuture onComplete {
        case Success(adminResult) => log.info("Admin result = {}", adminResult)
        case Failure(e) =>
          log.error(e, "Unexpected exception")
      }
  }
}
