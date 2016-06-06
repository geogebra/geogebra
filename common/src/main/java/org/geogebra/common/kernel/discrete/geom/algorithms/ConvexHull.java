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
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.kernel.discrete.geom.LineAndPointUtils;
import org.geogebra.common.kernel.discrete.geom.Point2D;
import org.geogebra.common.kernel.discrete.geom.Segment2D;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.LogEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.grahams_scan.GrahamsScanCompleteEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.grahams_scan.GrahamsScanSegmentAddEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.grahams_scan.GrahamsScanSegmentCheckEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.grahams_scan.GrahamsScanSegmentRemoveEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.jarvis_march.JarvisAddSegmentEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.jarvis_march.JarvisChainsDetectedEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.jarvis_march.JarvisPointSelectedEvent;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.jarvis_march.JarvisPointsCheckEvent;

/**
 *
 * @author cyberpython
 */
public class ConvexHull {

    static class PointComparator implements Comparator<Point2D> {

        /*
         * Sorts the points so that the lowest - leftmost one is the first.
         * Used by both Graham's and Jarvis's algorithms.
         */
        public int compare(Point2D o1, Point2D o2) {
            double y1 = o1.getY();
            double y2 = o2.getY();
            double yDiff = y1 - y2;
            double errorTolerance = LineAndPointUtils.getErrorTolerance();
            if (yDiff < -errorTolerance) {
                return -1;
            } else if (yDiff>errorTolerance) {
                return 1;
            } else{
                double x1 = o1.getX();
                double x2 = o2.getX();
                double xDiff = x1 - x2;

                if (xDiff < -errorTolerance) {
                    return -1;
                } else if (xDiff > errorTolerance) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    /*static class PointComparator implements Comparator<Point2D> {

        public int compare(Point2D o1, Point2D o2) {
            double y1 = o1.getY();
            double y2 = o2.getY();
            if (y1 < y2) {
                return -1;
            } else if (y1 == y2) {
                double x1 = o1.getX();
                double x2 = o2.getX();

                if (x1 < x2) {
                    return -1;
                } else if (x1 == x2) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }
    }*/

    static class PolarAngleComparator implements Comparator<Point2D> {

        private Point2D p0;

        public PolarAngleComparator(Point2D p0) {
            this.p0 = p0;
        }

        /*
         * Sorts the points so that the lowest - leftmost one is the first.
         * Used by both Graham's and Jarvis's algorithms.
         */
        public int compare(Point2D p1, Point2D p2) {

            Double theta1 = polarAngle(p0, p1);
            Double theta2 = polarAngle(p0, p2);

            if (theta1 == null) {
                theta1 = new Double(0);
            }

            if (theta2 == null) {
                theta2 = new Double(0);
            }

            if(p1.equals(p0)){
                theta1 = -500.0;
            }

            if(p2.equals(p0)){
                theta2 = -500.0;
            }

            if (theta1 < theta2) {
                return -1;
            } else if (theta1 == theta2) {
                double errorTolerance = LineAndPointUtils.getErrorTolerance();
                double x1 = p1.getX();
                double x2 = p2.getX();
                double xDiff = x1 - x2;

                if (xDiff < -errorTolerance) {
                    return -1;
                } else if (xDiff > errorTolerance) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return 1;
            }
        }
    }


    private static Double polarAngle(Point2D p0, Point2D p1) {
        if (p0.equals(p1)) {
            return null;
        }
        double dy = p1.getY() - p0.getY();
        double dx = p1.getX() - p0.getX();
        double result = Math.toDegrees(Math.atan(dy / dx));
        if (dx < 0) {
            result = 180 + result;
        } else {
            if (dy < 0) {
                result = 270 + result;
            }
        }
        return result;
    }

    private static Double polarAngleNegAxis(Point2D p0, Point2D p1) {
        if (p0.equals(p1)) {
            return null;
        }
        double dy = p1.getY() - p0.getY();
        double dx = p1.getX() - p0.getX();
        double result = Math.toDegrees(Math.atan(dy / dx));
        if (dx < 0) {
            result = 180 + result;
        }
        result = (result + 180) % 360;
        return result;
    }

    private static int jarvisFindSmallestPolarAngle(List<Point2D> points, Point2D p0, boolean rightChain, List<LogEvent> events) {
        /*
         * Returns the index of the point with the smallest polar angle relative to p0
         * The rightChain parameter defines whether we are looking for the smallest polar angle
         * in the left or the right chain of the hull.
         */

        int i = 0;
        int index = 0;
        double minAngle = 500;
        Double polarAngle;


        for (Iterator<Point2D> it = points.iterator(); it.hasNext();) {
            Point2D point = it.next();
            if (rightChain) {
                polarAngle = polarAngle(p0, point);
            } else {
                polarAngle = polarAngleNegAxis(p0, point);
            }

            if (polarAngle != null) {
                events.add(new JarvisPointsCheckEvent(p0, point, polarAngle, rightChain));
            }

            if ((polarAngle != null) && (polarAngle < minAngle)) {
                index = i;
                minAngle = polarAngle;
            }
            i++;
        }

        events.add(new JarvisPointSelectedEvent(p0, points.get(index), minAngle, rightChain));

        return index;
    }

    public static List<Point2D> jarvisMarch(List<Point2D> points, List<LogEvent> events) {

        events.clear();

        List<Point2D> result = new ArrayList<Point2D>();

        if (points.size() > 2) {

            Collections.sort(points, new PointComparator());

            Point2D lowestPoint = points.get(0);
            Point2D highestPoint = points.get(points.size() - 1);

            events.add(new JarvisChainsDetectedEvent(lowestPoint, highestPoint));

            result.add(lowestPoint);
            events.add(new JarvisAddSegmentEvent(lowestPoint, lowestPoint));

            int i = 1;

            Point2D p = lowestPoint;
            Point2D p1;
            int index = 1;
            while (p.equals(highestPoint) == false) {
                index = jarvisFindSmallestPolarAngle(points, p, true, events);
                result.add(points.get(index));
                p1 = points.get(index);
                events.add(new JarvisAddSegmentEvent(p, p1));
                p = p1;
            }

            while (p.equals(lowestPoint) == false) {
                index = jarvisFindSmallestPolarAngle(points, p, false, events);
                result.add(points.get(index));
                p1 = points.get(index);
                events.add(new JarvisAddSegmentEvent(p, p1));
                p = p1;
            }
        }

        return result;


    }

    private static double ccw(Point2D p1, Point2D p2, Point2D p3) {
        /* Three points are a counter-clockwise turn if ccw > 0, clockwise if
         * ccw < 0, and collinear if ccw = 0 because ccw is a determinant that
         * gives the signed area of the triangle formed by p1, p2, and p3.
         */
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }
    

	// public static List<Point2D> grahamsScan(List<Point2D> points,
	// List<LogEvent> events) {
	//
	// events.clear();
	//
	// List<Point2D> result = new ArrayList<Point2D>();
	//
	// if (points.size() > 2) {
	//
	// //----------------- Start of Graham's Scan
	//
	// ArrayDeque<Point2D> s = new ArrayDeque<Point2D>();
	//
	// Collections.sort(points, new PointComparator());
	// Point2D startingPoint = points.get(0);
	//
	// for(int i=0; i<30; i++){
	// System.out.println();
	// }
	// int j=0;
	// boolean stop=false;
	// while(j<points.size() && !stop){
	// Point2D p = points.get(j);
	// if(p.getY()>startingPoint.getY()){
	// break;
	// }
	// if(p.equals(startingPoint)){
	// System.out.print("S--> ");
	// }
	// System.out.println(p.toString());
	// j++;
	// }
	//
	// Collections.sort(points, new PolarAngleComparator(startingPoint));
	//
	// s.push(startingPoint);
	// s.push(points.get(1));
	// s.push(points.get(2));
	//
	// Point2D top = points.get(2);
	// Point2D nTop = points.get(1);
	//
	// events.add(new GrahamsScanSegmentAddEvent(new Segment2D(points.get(0),
	// nTop)));
	// events.add(new GrahamsScanSegmentAddEvent(new Segment2D(nTop, top)));
	//
	// int m = points.size();
	// for (int i = 3; i < m; i++) {
	//
	// events.add(new GrahamsScanSegmentCheckEvent(new Segment2D(nTop, top), new
	// Segment2D(top, points.get(i))));
	// while ((s.size()>1) && ccw(nTop, top, points.get(i)) <= 0) {
	//
	// events.add(new GrahamsScanSegmentRemoveEvent(new Segment2D(nTop, top)));
	// s.pop();
	//
	// nTop = peekNTop(s);
	// top = s.peekFirst();
	//
	// if(s.size()>1){
	// events.add(new GrahamsScanSegmentCheckEvent(new Segment2D(nTop, top), new
	// Segment2D(top, points.get(i))));
	// }
	// }
	//
	// s.push(points.get(i));
	//
	// nTop = peekNTop(s);
	// top = s.peekFirst();
	//
	// events.add(new GrahamsScanSegmentAddEvent(new Segment2D(nTop, top)));
	//
	// }
	//
	// s.push(startingPoint);
	//
	// events.add(new GrahamsScanSegmentAddEvent(new Segment2D(top,
	// points.get(0))));
	// events.add(new GrahamsScanCompleteEvent());
	//
	// //----------------- End of Graham's Scan
	//
	// for (Iterator<Point2D> it = s.descendingIterator(); it.hasNext();) {
	// Point2D point2D = it.next();
	// result.add(point2D);
	// }
	//
	//
	// }
	// return result;
	//
	// }


    private static Point2D peekNTop(Deque<Point2D> s){
        Point2D top = s.pop();
        Point2D result = s.peekFirst();
        s.push(top);
        return result;
    }


    
     public static List<Point2D> grahamsScan2(List<Point2D> points, List<LogEvent> events) {

        events.clear();

        List<Point2D> result = new ArrayList<Point2D>();

        if (points.size() > 2) {

            //----------------- Start of Graham's Scan

            Collections.sort(points, new PointComparator());
            Point2D startingPoint = points.get(0);
            Point2D highestPoint = points.get(points.size() - 1);

            for (int i = 0; i < 30; i++) {
                System.out.println();
            }

            int j=0;
            boolean stop=false;
            while(j<points.size() && !stop){
                Point2D p = points.get(j);
                if(p.getY()>startingPoint.getY()){
                    break;
                }
                System.out.println(p.toString());
                j++;
            }

            Collections.sort(points, new PolarAngleComparator(startingPoint));


            Point2D endPoint = points.get(points.size() - 1);


            //System.out.println("PUSH: "+points.get(0));
            //System.out.println("PUSH: "+points.get(1));
            //System.out.println("ADD    :"+points.get(0)+" -> "+points.get(1));
            events.add(new GrahamsScanSegmentAddEvent(new Segment2D(points.get(0), points.get(1))));
            int n = points.size();
            int m = 1;
            for (int i = 2; i < n; i++) {
                //System.out.println("CHECK  :"+points.get(m - 1)+", "+points.get(m)+" , "+points.get(i));
                events.add(new GrahamsScanSegmentCheckEvent(new Segment2D(points.get(m-1), points.get(m)), new Segment2D(points.get(m), points.get(i))));
                while ((m>0)&&ccw(points.get(m - 1), points.get(m), points.get(i)) <= 0) {

                    //System.out.println("POP: "+points.get(m));
                    //System.out.println("REMOVE :"+points.get(m-1)+" -> "+points.get(m));
                    events.add(new GrahamsScanSegmentRemoveEvent(new Segment2D(points.get(m-1), points.get(m))));
                    m--;
                    //System.out.println("CHECK  :"+points.get(m - 1)+", "+points.get(m)+" , "+points.get(i));
                    if(m>0){
                        events.add(new GrahamsScanSegmentCheckEvent(new Segment2D(points.get(m-1), points.get(m)), new Segment2D(points.get(m), points.get(i))));
                    }
                }

                m++;

                //swap(points, m, i);
                Collections.swap(points, m, i);


                //System.out.println("PUSH: "+points.get(m));
                //System.out.println("ADD    :"+points.get(m-1)+" -> "+points.get(m));
                events.add(new GrahamsScanSegmentAddEvent(new Segment2D(points.get(m-1), points.get(m))));

            }

            //System.out.println("PUSH: "+points.get(0));
            //System.out.println("ADD    :"+points.get(m)+" -> "+points.get(0));
            events.add(new GrahamsScanSegmentAddEvent(new Segment2D(points.get(m), points.get(0))));
            events.add(new GrahamsScanCompleteEvent());

            //----------------- End of Graham's Scan


            int i = 0;
            Point2D p = null;
            int max = points.size();
            while ((i < max) && (p != endPoint)) {
                p = points.get(i);
                result.add(p);
                i++;
            }

            result.add(startingPoint);


        }
        return result;

    }

    /*public static void main(String[] args) {
        List<Point2D> points = new ArrayList();
        points.add(new Point(7, 6));
        points.add(new Point(3, 7));
        points.add(new Point(2, 3));
        //points.add(new Point(8, 8));
        //points.add(new Point(6, 5));
        points.add(new Point(11, 4));
        points.add(new Point(5, 1));
        //points.add(new Point(1, 4));
        points.add(new Point(8, 3));
        points.add(new Point(5, 2));

        List<Point2D> jarvisResult = ConvexHull.jarvisMarch(points, new ArrayList<LogEvent>());
        List<Point2D> grahamsResult = ConvexHull.grahamsScan(points, new ArrayList<LogEvent>());

        System.out.print("Jarvis' March:\t");
        LineAndPointUtils.printPointsList(jarvisResult, System.out);
        System.out.println();
        System.out.print("Graham's Scan:\t");
        LineAndPointUtils.printPointsList(grahamsResult, System.out);

    }*/
}
