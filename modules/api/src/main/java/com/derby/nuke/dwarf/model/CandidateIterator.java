package com.derby.nuke.dwarf.model;

/**
 * CandidateIterator
 *
 * @author Drizzt Yang
 */
public interface CandidateIterator<T> extends Iterable<T>{
    String getName();
    
    String getValueString();
}
