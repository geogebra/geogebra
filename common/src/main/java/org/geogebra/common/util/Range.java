package org.geogebra.common.util;

import java.util.Objects;

/**
 * Represents an immutable range of integers with a start and an end value.
 */
public final class Range {

    private final int start;
    private final int end;

    /**
     * @param start The start value of the range (inclusive)
     * @param end The end value of the range (exclusive)
     */
    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * @return The start value of the range
     */
    public int getStart() {
        return start;
    }

    /**
     * @return The end value of the range
     */
    public int getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Range range = (Range) object;
        return start == range.start && end == range.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}