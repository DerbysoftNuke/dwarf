package com.derby.nuke.dwarf.model;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * DateCandidateIterator
 *
 * @author Drizzt Yang
 */
public class DateCandidateIterator extends BaseCandidateIterator<LocalDate> {

    private LocalDate start;
    private LocalDate end;

    public DateCandidateIterator() {
    }

    public DateCandidateIterator(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    /**
     * return a Iterator which can iterate each day of the date span
     */
    @Override
    public Iterator<LocalDate> iterator() {
        List<LocalDate> eachDays = new ArrayList<>();
        LocalDate index = start;
        while (!index.isAfter(end)) {
            eachDays.add(index);
            index = index.plusDays(1);
        }
        return eachDays.iterator();
    }

    /**
     * return day length of this
     *
     * @return
     */
    public int length() {
        return Days.daysBetween(start, end).getDays() + 1;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    @Override
    public String getValueString() {
        return new StringBuilder("date:").append(start).append("|").append(end).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DateCandidateIterator that = (DateCandidateIterator) o;
        return Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), start, end);
    }

    @Override
    public String toString() {
        return this.getName() + ": " + start + "|" + end;
    }
}