package com.derby.nuke.dwarf.model;

import java.util.List;
import java.util.Map;

/**
 * Created by Passyt on 2017/9/4.
 */
public class IterableJob extends Job {

    private String script;
    private List<Map<String, Object>> candidateIterators;
    private List<CandidateIterator<Object>> candidateIteratorList;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<Map<String, Object>> getCandidateIterators() {
        return candidateIterators;
    }

    public void setCandidateIterators(List<Map<String, Object>> candidateIterators) {
        this.candidateIterators = candidateIterators;
    }

    public List<CandidateIterator<Object>> getCandidateIteratorList() {
        return candidateIteratorList;
    }

    public void setCandidateIteratorList(List<CandidateIterator<Object>> candidateIteratorList) {
        this.candidateIteratorList = candidateIteratorList;
    }
}
