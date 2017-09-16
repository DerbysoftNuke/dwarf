package com.derby.nuke.dwarf.executor

import akka.actor.{Actor, ActorLogging, Props}
import com.derby.nuke.dwarf.JobParameter
import com.derby.nuke.dwarf.executor.ScriptWorker.ScriptMessage
import com.derby.nuke.dwarf.script.ScriptProvider

/**
  * Created by Passyt on 2017/9/5.
  */
class ScriptWorker extends Actor with ActorLogging {

  def receive = {
    case ScriptMessage(parameter) =>
      val result = ScriptProvider.provider(ScriptProvider.Language.groovy).execute(parameter.script, parameter.parameters)
      log.debug(s"Result = $result");
      sender() ! result
  }
}

object ScriptWorker {

  def props: Props = Props[ScriptWorker]

  final case class ScriptMessage(parameter: JobParameter) extends Serializable

}
