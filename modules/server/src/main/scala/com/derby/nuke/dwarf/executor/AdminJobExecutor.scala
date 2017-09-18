package com.derby.nuke.dwarf.executor

import java.util
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import com.derby.nuke.dwarf.JobParameter
import com.derby.nuke.dwarf.executor.ScriptWorker.ScriptMessage
import com.derby.nuke.dwarf.model.AdminJob
import com.google.common.collect.{HashMultimap, Multimap}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.control.Breaks._
import scala.util.{Failure, Success}

/**
  * Created by Passyt on 2017/9/5.
  */
class AdminJobExecutor extends Actor with ActorLogging {

  implicit val timeout = Timeout(3600 seconds)
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

      def aSender = sender()
      def adminFuture = ask(scriptWorkers, new ScriptMessage(new JobParameter(job.getScript, job.getProperties.asScala.toMap))).mapTo[AnyRef]

      adminFuture onComplete {
        case Success(adminResult) =>
          log.info("Admin results = {}", adminResult)
          if (job.getTaskScripts == null || job.getTaskScripts.isEmpty || !adminResult.isInstanceOf[util.Collection[util.Map[String, AnyRef]]]) {
            aSender ! adminResult
          } else {
            val results = executeTasks(job, adminResult.asInstanceOf[util.Collection[util.Map[String, AnyRef]]])
            log.debug("Send {} back by sender {}", results, aSender)
            aSender ! results
          }
        case Failure(e) =>
          log.error(e, "Unexpected exception")
      }
  }

  def executeTasks(job: AdminJob, adminResults: util.Collection[util.Map[String, AnyRef]]): util.Collection[AnyRef] = {
    val tasks = toScriptTasks(job, adminResults) match {
      case tasks: SequenceScriptTasks => executeTasksBySequence(job, tasks)
      case tasks: NoBatchScriptTasks => executeTasksByConcurrent(job, tasks)
      case tasks: BatchScriptTasks => executeTasksByConcurrent(job, tasks)
    }
    tasks
  }

  def toScriptTasks(job: AdminJob, adminResults: util.Collection[util.Map[String, AnyRef]]): ScriptTasks = {
    val isBatch = "true".equalsIgnoreCase(job.getProperties.get("batch").asInstanceOf[String])
    val isSequence = "true".equalsIgnoreCase(job.getProperties.get("sequence").asInstanceOf[String])
    val scriptTasks = ScriptTasks(isSequence, isBatch)

    breakable {
      adminResults.forEach {
        adminResult => {

          val scriptKey = String.valueOf(adminResult.get("key"))
          val script = job.getTaskScripts.get(scriptKey)
          if (script == null) {
            log.warning("No task script found by {} in admin result {}", scriptKey, adminResult)
            break
          }

          scriptTasks.addScriptTask(scriptKey, new JobParameter(script, Map("properties" -> job.getProperties, "parameter" -> adminResult)))
        }
      }
    }
    return scriptTasks
  }

  def executeTasksBySequence(job: AdminJob, scriptTasks: SequenceScriptTasks): util.Collection[AnyRef] = {
    log.info("Executing {} tasks by sequence", scriptTasks.tasks.size())

    def results = new util.ArrayList[AnyRef]()

    scriptTasks.tasks.forEach {
      task => {
        //        implicit val timeout = Timeout(5 seconds)

        def taskFuture = ask(scriptWorkers, new ScriptMessage(task)).mapTo[AnyRef]

        val taskResult = Await.result(taskFuture, timeout.duration)
        log.info("Task result = {}", taskResult)
        results.add(taskResult)
      }
    }

    return results;
  }

  def executeTasksByConcurrent(job: AdminJob, scriptTasks: AbstractBatchScriptTasks): util.Collection[AnyRef] = {
    log.info("Executing {} tasks by concurrent", scriptTasks.tasks.size())
    val futures: util.List[Future[AnyRef]] = new util.ArrayList[Future[AnyRef]]();
    scriptTasks.tasks.asMap().forEach { (key, values) =>
      values.forEach { task =>
        //        implicit val timeout = Timeout(5 seconds)

        def taskFuture = ask(scriptWorkers, new ScriptMessage(task)).mapTo[AnyRef]

        taskFuture onComplete {
          case Success(taskResult) =>
            log.info("Task result = {}", taskResult)
            taskResult
          case Failure(e) =>
            log.error(e, "Unexpected exception")
        }

        futures.add(taskFuture)
      }
    }

    def results = Await.result(Future.sequence(futures.asScala), timeout.duration).asJava

    return results
  }
}

trait ScriptTasks {
  def addScriptTask(key: String, task: JobParameter): Unit
}

object ScriptTasks {
  def apply(isSequence: Boolean, isBatch: Boolean): ScriptTasks = if (isSequence) new SequenceScriptTasks else if (isBatch) new BatchScriptTasks else new NoBatchScriptTasks
}

case class SequenceScriptTasks(val tasks: util.List[JobParameter] = new util.ArrayList[JobParameter]()) extends ScriptTasks {
  override def addScriptTask(key: String, task: JobParameter): Unit = tasks.add(task)
}

abstract class AbstractBatchScriptTasks(val tasks: Multimap[String, JobParameter] = HashMultimap.create()) extends ScriptTasks {
}

case class NoBatchScriptTasks() extends AbstractBatchScriptTasks {
  override def addScriptTask(key: String, task: JobParameter): Unit = tasks.put(key + UUID.randomUUID().toString, task)
}

case class BatchScriptTasks() extends AbstractBatchScriptTasks {
  override def addScriptTask(key: String, task: JobParameter): Unit = tasks.put(key, task)
}