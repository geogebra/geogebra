/*
 * Copyright (c) 2010 Georgios Migdos <cyberpython@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.geogebra.common.kernel.discrete.geom.algorithms.intersections;

import org.geogebra.common.kernel.discrete.geom.LineAndPointUtils;
import org.geogebra.common.kernel.discrete.geom.Point2D;
import org.geogebra.common.kernel.discrete.geom.Segment2D;

/**
 *
 * @author cyberpython
 */
public class Event {

    public enum Type {

        SEGMENT_START,
        SEGMENT_END,
        SEGMENTS_INTERSECTION;
    }
    private Intersection intersection;
    private Type type;

    public Event(Intersection intersection, Event.Type eventType) {
        this.intersection = intersection;
        this.type = eventType;
    }

    public Intersection getIntersection() {
        return this.intersection;
    }

    public Event.Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return type.name() + "\n\t" + intersection.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Event) {
            Event e = (Event) o;

            if (this.type.equals(e.type)) {
                Intersection i = e.getIntersection();
                if (intersection instanceof ScanlineIntersection) {
                    if (i instanceof ScanlineIntersection) {
                        return equalsForScanLineIntersectionEvent(e);

                    }
                } else if (intersection instanceof SegmentsIntersection) {
                    if (i instanceof SegmentsIntersection) {
                        return equalsForSegmentsIntersectionEvent(e);
                    }
                }
            }
        }
        return false;

    }

    private boolean equalsForScanLineIntersectionEvent(Event e) {
        Intersection i = e.getIntersection();
        if (i instanceof ScanlineIntersection) {
            ScanlineIntersection sIntersection = (ScanlineIntersection) intersection;
            ScanlineIntersection si = (ScanlineIntersection) i;

            Segment2D s1 = sIntersection.getSegment();
            Point2D p1 = sIntersection.getPoint();
            Segment2D s2 = si.getSegment();
            Point2D p2 = si.getPoint();

            if (s1.equals(s2)) {
                if (LineAndPointUtils.pointsAreEqual(p1, p2)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    private boolean equalsForSegmentsIntersectionEvent(Event e) {
        Intersection i = e.getIntersection();
        if (i instanceof SegmentsIntersection) {

            SegmentsIntersection sIntersection = (SegmentsIntersection) intersection;
            SegmentsIntersection si = (SegmentsIntersection) i;

            Segment2D sa1 = sIntersection.getSegment1();
            Segment2D sa2 = sIntersection.getSegment2();
            Segment2D sb1 = si.getSegment1();
            Segment2D sb2 = si.getSegment2();
            Point2D p1 = sIntersection.getPoint();
            Point2D p2 = si.getPoint();

            if ((sa1.equals(sb1) && sa2.equals(sb2)) || (sa1.equals(sb2) && sa2.equals(sb1))) {
                if (LineAndPointUtils.pointsAreEqual(p1, p2)) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.intersection != null ? this.intersection.hashCode() : 0);
        hash = 17 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
