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
package org.geogebra.common.kernel.discrete.geom.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

import org.geogebra.common.kernel.discrete.geom.LineAndPointUtils;
import org.geogebra.common.kernel.discrete.geom.Point2D;
import org.geogebra.common.kernel.discrete.geom.algorithms.intersections.Event;
import org.geogebra.common.kernel.discrete.geom.algorithms.intersections.Intersection;
import org.geogebra.common.kernel.discrete.geom.algorithms.intersections.LeftEndPoint;
import org.geogebra.common.kernel.discrete.geom.algorithms.intersections.RightEndPoint;
import org.geogebra.common.kernel.discrete.geom.algorithms.intersections.ScanlineIntersection;
import org.geogebra.common.kernel.discrete.geom.algorithms.intersections.Segment2DEx;
import org.geogebra.common.kernel.discrete.geom.algorithms.intersections.SegmentsIntersection;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.LogEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.intersections.NewIntersectionEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.intersections.ScanLinePositionChangeEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.intersections.SegmentsIntersectionCheckEvent;

/**
 *
 * @author cyberpython
 */
public class Intersections {

    public static class EventComparator implements Comparator<Event> {

        public int compare(Event e1, Event e2) {
            Point2D o1 = e1.getIntersection().getPoint();
            Point2D o2 = e2.getIntersection().getPoint();

            double x1 = o1.getX();
            double x2 = o2.getX();
            if (x1 - x2 < -LineAndPointUtils.getErrorTolerance()) {
                return -1;
            } else if (x1 - x2 <= LineAndPointUtils.getErrorTolerance()) {
                double y1 = o1.getY();
                double y2 = o2.getY();

                if (y1 - y2 < -LineAndPointUtils.getErrorTolerance()) {
                    return -1;
                } else if (y1 - y2 <= LineAndPointUtils.getErrorTolerance()) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }
    }

    public static class SegmentComparator implements Comparator<Segment2DEx> {

        private double x;

        public SegmentComparator() {
            x = 0;
        }

        public void setX(double x) {
            this.x = x;
        }

        public int compare(Segment2DEx s1, Segment2DEx s2) {

            if ((s1 == null) && (s2 == null)) {
                return 0;
            }
            if (s1 == null) {
                return -1;
            }
            if (s2 == null) {
                return 1;
            }

            if (s1.equals(s2)) {
                return 0;
            }

            double maxY1 = Math.max(s1.getLeftEndPoint().getY(), s1.getRightEndPoint().getY());
            double maxY2 = Math.max(s2.getLeftEndPoint().getY(), s2.getRightEndPoint().getY());

			Segment2DEx sVert1 = new Segment2DEx(new Point2D(x, 0),
					new Point2D(x, maxY1 + 10));
			Segment2DEx sVert2 = new Segment2DEx(new Point2D(x, 0),
					new Point2D(x, maxY2 + 10));
            Point2D o1 = LineAndPointUtils.computeIntersectionPoint(s1, sVert1);
            Point2D o2 = LineAndPointUtils.computeIntersectionPoint(s2, sVert2);


            if (o1 == null || o2 == null) {
                return 0;
            }

            double y1 = o1.getY();
            double y2 = o2.getY();

            if (y1 < y2) {
                return -1;
            } else if (y1 > y2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private static void getAllInitalEvents(List<Segment2DEx> segments, PriorityQueue<Event> q, TreeSet<Event> eventsSet) {
        for (Iterator<Segment2DEx> it = segments.iterator(); it.hasNext();) {
            Segment2DEx segment = it.next();
            Event e1 = new Event(new ScanlineIntersection(segment, segment.getLeftEndPoint()), Event.Type.SEGMENT_START);
            q.add(e1);
            eventsSet.add(e1);
            Event e2 = new Event(new ScanlineIntersection(segment, segment.getRightEndPoint()), Event.Type.SEGMENT_END);
            q.add(e2);
            eventsSet.add(e2);
        }
    }

    public static List<Intersection> bentleyOttmann(List<Segment2DEx> segments, List<LogEvent> eventLog) {

        eventLog.clear();

        eventLog.add(new ScanLinePositionChangeEvent(0));

        PriorityQueue<Event> q = new PriorityQueue<Event>(11, new EventComparator());
        TreeSet<Event> eventsSet = new TreeSet<Event>(new EventComparator());
        getAllInitalEvents(segments, q, eventsSet);

        SegmentComparator comp = new SegmentComparator();
        List<Segment2DEx> sweepLine = new ArrayList<Segment2DEx>();

        List<Intersection> result = new ArrayList<Intersection>();

        Event e;
        while ((e = q.poll()) != null) {
            eventsSet.add(e);

            Point2D p = e.getIntersection().getPoint();

            comp.setX(p.getX());
            eventLog.add(new ScanLinePositionChangeEvent(p.getX()));

            if (e.getType().equals(Event.Type.SEGMENT_START)) {
                Segment2DEx s0 = ((LeftEndPoint) p).getSegment();

                sweepLine.add(s0);
                Collections.sort(sweepLine, comp);
                int s0Index = sweepLine.indexOf(s0);

                Segment2DEx sNext = s0Index + 1 < sweepLine.size() ? sweepLine.get(s0Index + 1) : null;

                if (sNext != null) {
                    eventLog.add(new SegmentsIntersectionCheckEvent(s0, sNext));
                }


                Intersection s0sNextIntersection = new SegmentsIntersection(LineAndPointUtils.computeIntersectionPoint(s0, sNext), s0, sNext);



                if (s0sNextIntersection.getPoint() != null) {
                    Event e2 = new Event(s0sNextIntersection, Event.Type.SEGMENTS_INTERSECTION);
                    q.add(e2);
                    eventsSet.add(e2);
                    eventLog.add(new NewIntersectionEvent(s0sNextIntersection.getPoint()));
                }


                Segment2DEx sPrev = s0Index > 0 ? sweepLine.get(s0Index - 1) : null;

                if (sPrev != null) {
                    eventLog.add(new SegmentsIntersectionCheckEvent(s0, sPrev));
                }

                Intersection s0sPrevIntersection = new SegmentsIntersection(LineAndPointUtils.computeIntersectionPoint(s0, sPrev), s0, sPrev);

                if (s0sPrevIntersection.getPoint() != null) {
                    Event e2 = new Event(s0sPrevIntersection, Event.Type.SEGMENTS_INTERSECTION);
                    q.add(e2);
                    eventsSet.add(e2);
                    eventLog.add(new NewIntersectionEvent(s0sPrevIntersection.getPoint()));
                }



            } else if (e.getType().equals(Event.Type.SEGMENT_END)) {

                Segment2DEx s0 = ((RightEndPoint) p).getSegment();

                int s0Index = sweepLine.indexOf(s0);

                Segment2DEx sNext = s0Index + 1 < sweepLine.size() ? sweepLine.get(s0Index + 1) : null;
                Segment2DEx sPrev = s0Index > 0 ? sweepLine.get(s0Index - 1) : null;


                sweepLine.remove(s0);
                Collections.sort(sweepLine, comp);

                Intersection sPrevsNextIntersection = new SegmentsIntersection(LineAndPointUtils.computeIntersectionPoint(sPrev, sNext), sPrev, sNext);

                if ((sPrev != null) && (sNext != null)) {
                    eventLog.add(new SegmentsIntersectionCheckEvent(sPrev, sNext));
                }

                if (sPrevsNextIntersection.getPoint() != null) {
                    Event e2 = new Event(sPrevsNextIntersection, Event.Type.SEGMENTS_INTERSECTION);
                    if (!eventsSet.contains(e2)) {
                        q.add(e2);
                        eventsSet.add(e2);
                        eventLog.add(new NewIntersectionEvent(sPrevsNextIntersection.getPoint()));
                    }
                }


            } else {

                if (e.getType().equals(Event.Type.SEGMENTS_INTERSECTION)) {

                    SegmentsIntersection intersection = (SegmentsIntersection) e.getIntersection();

                    result.add(intersection);

                    Segment2DEx segE1 = intersection.getSegment1();
                    Segment2DEx segE2 = intersection.getSegment2();
                    Segment2DEx segTmp = segE1;


                    int segE1Index = sweepLine.indexOf(segE1);

                    Segment2DEx candidate1 = segE1Index + 1 < sweepLine.size() ? sweepLine.get(segE1Index + 1) : null;
                    if (segE2.equals(candidate1)) {
                        segE1 = segE2;
                        segE2 = segTmp;
                    }

                    segE1Index = sweepLine.indexOf(segE1);
                    int segE2Index = sweepLine.indexOf(segE2);

                    Collections.swap(sweepLine, segE1Index, segE2Index);

                    Segment2DEx segA = segE1Index + 1 < sweepLine.size() ? sweepLine.get(segE1Index + 1) : null;
                    Segment2DEx segB = segE2Index > 0 ? sweepLine.get(segE2Index - 1) : null;


                    Intersection sE2sAIntersection = new SegmentsIntersection(LineAndPointUtils.computeIntersectionPoint(segA, segE2), segA, segE2);


                    if ((segA != null) && (segE2 != null)) {
                        eventLog.add(new SegmentsIntersectionCheckEvent(segA, segE2));
                    }

                    if (sE2sAIntersection.getPoint() != null) {
                        Event e2 = new Event(sE2sAIntersection, Event.Type.SEGMENTS_INTERSECTION);
                        if (!eventsSet.contains(e2)) {
                            q.add(e2);
                            eventsSet.add(e2);
                            eventLog.add(new NewIntersectionEvent(sE2sAIntersection.getPoint()));
                        }
                    }


                    Intersection sE1sBIntersection = new SegmentsIntersection(LineAndPointUtils.computeIntersectionPoint(segE1, segB), segE1, segB);

                    if ((segE1 != null) && (segB != null)) {
                        eventLog.add(new SegmentsIntersectionCheckEvent(segE1, segB));
                    }

                    if (sE1sBIntersection.getPoint() != null) {
                        Event e2 = new Event(sE1sBIntersection, Event.Type.SEGMENTS_INTERSECTION);
                        if (!eventsSet.contains(e2)) {
                            q.add(e2);
                            eventsSet.add(e2);
                            eventLog.add(new NewIntersectionEvent(sE1sBIntersection.getPoint()));
                        }
                    }

                }


            }
            //printQueue(q);
            //printSweepLine(sweepLine);
        }
        return result;
    }




    private static void printQueue(PriorityQueue<Event> q) {
        PriorityQueue<Event> tmp = new PriorityQueue<Event>(q);

        System.out.println("QUEUE:");
        System.out.println("----------------------------------");
        while (tmp.isEmpty() == false) {
            Event e = tmp.poll();
            System.out.println(e.toString());
        }
        System.out.println("----------------------------------");
        System.out.println();
    }

    private static void printSweepLine(List<Segment2DEx> sweepLine) {

        System.out.println("SWEEPLINE:");
        System.out.println("----------------------------------");
        for (Iterator it = sweepLine.iterator(); it.hasNext();) {

            Segment2DEx s = (Segment2DEx) it.next();
            System.out.println(s.getLeftEndPoint().toString() + " - " + s.getRightEndPoint().toString());

        }
        System.out.println("----------------------------------");
        System.out.println();
    }
    
}
