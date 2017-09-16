package com.derby.nuke.dwarf.script

/**
  * Created by Passyt on 2017/9/16.
  */
trait ScriptExecutor {

  def execute(script: String, parameters: Map[String, AnyRef]): AnyRef

}
