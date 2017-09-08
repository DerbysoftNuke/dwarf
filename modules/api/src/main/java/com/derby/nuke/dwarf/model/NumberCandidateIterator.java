package com.derby.nuke.dwarf.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * NumberCandidateIterator
 *
 * @author Drizzt Yang
 */
public class NumberCandidateIterator extends BaseCandidateIterator<Integer> {
    private Integer start;
    private Integer end;
    private Integer interval = 1;

    public NumberCandidateIterator() {
    }

    public NumberCandidateIterator(Integer start, Integer end, Integer interval) {
        this.start = start;
        this.end = end;
        this.interval = interval;
    }

    @Override
    public Iterator<Integer> iterator() {
        List<Integer> eachDays = new ArrayList<>();
        for (int index = getStart(); index <= getEnd(); index = index + getInterval()) {
            eachDays.add(index);
        }
        return eachDays.iterator();
    }


    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    @Override
    public String getValueString() {
        return new StringBuilder("number:").append(start).append("...").append(end).append(".").append(interval).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NumberCandidateIterator integers = (NumberCandidateIterator) o;
        return Objects.equals(start, integers.start) &&
                Objects.equals(end, integers.end) &&
                Objects.equals(interval, integers.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), start, end, interval);
    }

    @Override
    public String toString() {
        return this.getName() + ": " + "[" + start + "..." + end + "]" + interval;
    }

}
