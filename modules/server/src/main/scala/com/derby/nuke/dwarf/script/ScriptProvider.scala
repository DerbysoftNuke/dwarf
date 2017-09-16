package com.derby.nuke.dwarf.script

import com.derby.nuke.dwarf.script.groovy.GroovyScriptExecutor

/**
  * Created by Passyt on 2017/9/16.
  */
object ScriptProvider {

  object Language {
    val groovy = "groovy"
  }

  private val providers: Map[String, ScriptExecutor] = Map(Language.groovy -> GroovyScriptExecutor.instance)

  def provider(language: String): ScriptExecutor = providers.get(language).get

}