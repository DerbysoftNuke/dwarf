package com.derby.nuke.dwarf.executor

import javax.script._

import akka.actor.{Actor, ActorLogging, Props}
import com.derby.nuke.dwarf.JobParameter
import com.derby.nuke.dwarf.executor.ScriptWorker.ScriptMessage

/**
  * Created by Passyt on 2017/9/5.
  */
class ScriptWorker extends Actor with ActorLogging {

  def receive = {
    case ScriptMessage(parameter) =>
      val result = compile(parameter.script).eval(parameter.scriptContext);
      log.debug(s"Result = $result");
      sender() ! result
  }

  def compile(script: String): CompiledScript = {
    log.debug("compile script by [{}]", script)
    val manager = new ScriptEngineManager
    val engine = manager.getEngineByName("groovy");
    if (engine.isInstanceOf[Compilable]) {
      val compilable = engine.asInstanceOf[Compilable]
      try
        return compilable.compile(script)
      catch {
        case e: ScriptException =>
          log.warning("Compiled failed", e)
      }
    }

    throw new IllegalArgumentException(s"Unknown script $script");
  }
}

object ScriptWorker {

  def props: Props = Props[ScriptWorker]

  final case class ScriptMessage(parameter: JobParameter) extends Serializable

}
