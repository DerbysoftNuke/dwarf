package com.derby.nuke.dwarf.executor

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.FromConfig
import com.derby.nuke.dwarf.JobParameter
import com.derby.nuke.dwarf.executor.ScriptWorker.ScriptMessage
import com.derby.nuke.dwarf.model.AdminJob

import scala.collection.JavaConverters._

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
      scriptWorkers ! new ScriptMessage(new JobParameter(job.getScript, job.getProperties.asScala.toMap))
  }
}
