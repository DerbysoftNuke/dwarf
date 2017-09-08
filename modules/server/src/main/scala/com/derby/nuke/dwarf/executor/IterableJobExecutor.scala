package com.derby.nuke.dwarf.executor

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.FromConfig
import com.derby.nuke.dwarf.JobParameter
import com.derby.nuke.dwarf.executor.ScriptWorker.ScriptMessage
import com.derby.nuke.dwarf.model.IterableJob

import scala.collection.JavaConverters._

/**
  * Created by Passyt on 2017/9/5.
  */
class IterableJobExecutor extends Actor with ActorLogging {

  val scriptWorkers = context.actorOf(FromConfig.props(Props[ScriptWorker]), "scriptWorkerRouter")

  override def receive = {
    case job: IterableJob =>
      scriptWorkers ! new ScriptMessage(new JobParameter(job.getScript, job.getProperties.asScala.toMap))
  }
}
