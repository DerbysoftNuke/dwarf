package com.derby.nuke.dwarf

import javax.script.{ScriptContext, SimpleScriptContext}

/**
  * Created by Passyt on 2017/9/4.
  */
class JobParameter(val script: String, val parameters: Map[String, AnyRef]) extends Serializable {

  def scriptContext: ScriptContext = {
    val context: ScriptContext = new SimpleScriptContext
    parameters.foreach(e => context.setAttribute(e._1, e._2, ScriptContext.ENGINE_SCOPE))
    context
  }

}
