package com.derby.nuke.dwarf.model;

import java.util.Iterator;
import java.util.Objects;

/**
 * BaseCandidateIterator
 *
 * @author Drizzt Yang
 */
public abstract class BaseCandidateIterator<T> implements CandidateIterator<T> {
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public abstract Iterator<T> iterator();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCandidateIterator<?> that = (BaseCandidateIterator<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
