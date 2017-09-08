package com.derby.nuke.dwarf.model;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ListCandidateIterator extends BaseCandidateIterator<Object> implements Iterable<Object> {

    private List<Object> list;

    public ListCandidateIterator() {

    }

    public ListCandidateIterator(List<Object> list) {
        this.list = list;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    @Override
    public Iterator<Object> iterator() {
        return this.list.iterator();
    }

    @Override
    public String toString() {
        return this.list.toString();
    }

    @Override
    public String getValueString() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ListCandidateIterator objects = (ListCandidateIterator) o;
        return Objects.equals(list, objects.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), list);
    }
}