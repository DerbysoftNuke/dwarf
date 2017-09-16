package com.derby.nuke.dwarf.executor

import java.util

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
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

/**
  * Created by Passyt on 2017/9/5.
  */
class AdminJobExecutor extends Actor with ActorLogging {

  implicit val timeout = Timeout(300 seconds)
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
      //      implicit val timeout = Timeout(5 seconds)

      def adminFuture = ask(scriptWorkers, new ScriptMessage(new JobParameter(job.getScript, job.getProperties.asScala.toMap))).mapTo[AnyRef]

      adminFuture onComplete {
        case Success(adminResults) =>
          log.info("Admin results = {}", adminResults)
          executeTasks(job, adminResults)
        case Failure(e) =>
          log.error(e, "Unexpected exception")
      }
  }

  def executeTasks(job: AdminJob, adminResults: AnyRef): Unit = {
    if (job.getTaskScripts == null || job.getTaskScripts.isEmpty) {
      return
    }

    if (adminResults.isInstanceOf[util.Collection[AnyRef]]) {
      log.error("Admin result should be collection, but is {}({})", adminResults.getClass, adminResults)
      return
    }

    val isBatch = "true".equalsIgnoreCase(job.getProperties.get("batch").asInstanceOf[String])
    val isSequence = "true".equalsIgnoreCase(job.getProperties.get("sequence").asInstanceOf[String])
    if (isSequence) {
      executeTasksBySequence(job, adminResults.asInstanceOf[util.Collection[util.Map[String, AnyRef]]])
    } else {
      executeTasksByConcurrent(job, adminResults.asInstanceOf[util.Collection[util.Map[String, AnyRef]]])
    }
  }

  def executeTasksBySequence(job: AdminJob, adminResults: util.Collection[util.Map[String, AnyRef]]): Unit = {
    adminResults.forEach(
      adminResult => {
        val resultKey = String.valueOf(adminResult.get("key"))
        val script = job.getTaskScripts.get(resultKey)
        if (script == null) {
          log.warning("No task script found by {} in admin result {}", resultKey, adminResult)
          return
        }

        //        implicit val timeout = Timeout(5 seconds)

        def taskFuture = ask(scriptWorkers, new ScriptMessage(new JobParameter(script, Map("properties" -> job.getProperties, "parameter" -> adminResult)))).mapTo[AnyRef]

        val taskResult = Await.result(taskFuture, timeout.duration)
        log.info("Task result = {}", taskResult)
      }
    )
  }

  def executeTasksByConcurrent(job: AdminJob, adminResults: util.Collection[util.Map[String, AnyRef]]): Unit = {
    val futures: Seq[Future[AnyRef]] = Seq[Future[AnyRef]]();
    adminResults.forEach(
      adminResult => {
        val resultKey = String.valueOf(adminResult.get("key"))
        val script = job.getTaskScripts.get(resultKey)
        if (script == null) {
          log.warning("No task script found by {} in admin result {}", resultKey, adminResult)
          return
        }

        //        implicit val timeout = Timeout(5 seconds)

        def taskFuture = ask(scriptWorkers, new ScriptMessage(new JobParameter(script, Map("properties" -> job.getProperties, "parameter" -> adminResult)))).mapTo[AnyRef]

        taskFuture onComplete {
          case Success(taskResult) =>
            log.info("Task result = {}", taskResult)
          case Failure(e) =>
            log.error(e, "Unexpected exception")
        }

        futures.+:(taskFuture)
      }
    )

    val results: Seq[AnyRef] = Await.result(Future.sequence(futures), timeout.duration)
  }

}
