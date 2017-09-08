package com.derby.nuke.dwarf

import akka.actor.{Actor, ActorLogging, Props}
import com.derby.nuke.dwarf.executor.AdminJobExecutor
import com.derby.nuke.dwarf.model.{AdminJob, IterableJob}

/**
  * Created by Passyt on 2017/9/7.
  */
class JobForwarder extends Actor with ActorLogging {

  //  val scriptWorkers = context.actorOf(FromConfig.props(Props[ScriptWorker]), "scriptWorkerRouter")
  //  val scriptWorkers = context.actorOf(FromConfig.props(), "scriptWorkerRouter")
  //  val scriptWorkers = context.actorOf(
  //    ClusterRouterPool(AdaptiveLoadBalancingPool(
  //      MixMetricsSelector), ClusterRouterPoolSettings(
  //      totalInstances = 100, maxInstancesPerNode = 3,
  //      allowLocalRoutees = false)).props(Props[ScriptWorker]),
  //    name = "scriptWorkerRouter")

  val adminJobExecutor = context.actorOf(Props[AdminJobExecutor], "adminJobExecutor")

  override def receive = {
    case job: AdminJob =>
      adminJobExecutor.forward(job)
    case job: IterableJob =>
    case a: AnyRef =>
      log.error("Unsupported message {}", a)
  }

}
