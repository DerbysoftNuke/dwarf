package com.derby.nuke.dwarf.model;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Passyt on 2017/9/4.
 */
public class AdminJob extends Job {

    private String script;
    private Map<String, String> taskScripts;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Map<String, String> getTaskScripts() {
        return taskScripts;
    }

    public void setTaskScripts(Map<String, String> taskScripts) {
        this.taskScripts = taskScripts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AdminJob adminJob = (AdminJob) o;
        return Objects.equals(script, adminJob.script) &&
                Objects.equals(taskScripts, adminJob.taskScripts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), script, taskScripts);
    }

}
