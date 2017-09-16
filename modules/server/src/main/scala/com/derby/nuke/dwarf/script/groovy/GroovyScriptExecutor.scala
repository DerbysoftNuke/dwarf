package com.derby.nuke.dwarf.script.groovy

import javax.script._

import com.derby.nuke.dwarf.script.ScriptExecutor
import com.google.common.cache.{CacheBuilder, CacheLoader}

/**
  * Created by Passyt on 2017/9/16.
  */
object GroovyScriptExecutor {
  val instance = new GroovyScriptExecutor
}

class GroovyScriptExecutor private extends ScriptExecutor {

  val caches = CacheBuilder.from("softValues").build(new CacheLoader[String, CompiledScript] {
    override def load(key: String) = compile(key)
  })

  override def execute(script: String, parameters: Map[String, AnyRef]): AnyRef = {
    //TODO change to md5
    caches.get(script).eval(scriptContext(parameters))
  }

  protected def compile(script: String): CompiledScript = {
    val manager = new ScriptEngineManager
    val engine = manager.getEngineByName("groovy");
    if (engine.isInstanceOf[Compilable]) {
      val compilable = engine.asInstanceOf[Compilable]
      try
        return compilable.compile(script)
      catch {
        case e: ScriptException =>
      }
    }

    throw new IllegalArgumentException(s"Unknown script $script");
  }

  protected def scriptContext(parameters: Map[String, AnyRef]): ScriptContext = {
    val context: ScriptContext = new SimpleScriptContext
    parameters.foreach(e => context.setAttribute(e._1, e._2, ScriptContext.ENGINE_SCOPE))
    context
  }

}
