package com.derby.nuke.dwarf.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Passyt on 2017/9/4.
 */
public abstract class Job implements Serializable {

    private String key;
    private Map<String, Object> properties;
    private String callback;
    private Integer priority = 5;

    private long submitMillis = System.currentTimeMillis();
    private long startMillis;
    private boolean success;
    private String state;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public long getSubmitMillis() {
        return submitMillis;
    }

    public void setSubmitMillis(long submitMillis) {
        this.submitMillis = submitMillis;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return submitMillis == job.submitMillis &&
                startMillis == job.startMillis &&
                success == job.success &&
                Objects.equals(key, job.key) &&
                Objects.equals(properties, job.properties) &&
                Objects.equals(callback, job.callback) &&
                Objects.equals(priority, job.priority) &&
                Objects.equals(state, job.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, properties, callback, priority, submitMillis, startMillis, success, state);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Job{");
        sb.append("key='").append(key).append('\'');
        sb.append(", properties=").append(properties);
        sb.append(", callback='").append(callback).append('\'');
        sb.append(", priority=").append(priority);
        sb.append(", submitMillis=").append(submitMillis);
        sb.append(", startMillis=").append(startMillis);
        sb.append(", success=").append(success);
        sb.append(", state='").append(state).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
