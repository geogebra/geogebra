/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Denis M. Kishenko
 */
// This file was later modified by GeoGebra Inc.

package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;

import java.util.NoSuchElementException;

public abstract class Line2D implements Shape, Cloneable {

    public static class Float extends Line2D {

        public float x1;
        public float y1;
        public float x2;
        public float y2;

        public Float() {
        }

        public Float(float x1, float y1, float x2, float y2) {
            setLine(x1, y1, x2, y2);
        }

        public Float(Point2D p1, Point2D p2) {
            setLine(p1, p2);
        }

        @Override
        public double getX1() {
            return x1;
        }

        @Override
        public double getY1() {
            return y1;
        }

        @Override
        public double getX2() {
            return x2;
        }

        @Override
        public double getY2() {
            return y2;
        }

        @Override
        public Point2D getP1() {
            return new Point2D.Float(x1, y1);
        }

        @Override
        public Point2D getP2() {
            return new Point2D.Float(x2, y2);
        }

        @Override
        public void setLine(double x1, double y1, double x2, double y2) {
            this.x1 = (float)x1;
            this.y1 = (float)y1;
            this.x2 = (float)x2;
            this.y2 = (float)y2;
        }

        public void setLine(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public Rectangle2D getBounds2D() {
            float rx, ry, rw, rh;
            if (x1 < x2) {
                rx = x1;
                rw = x2 - x1;
            } else {
                rx = x2;
                rw = x1 - x2;
            }
            if (y1 < y2) {
                ry = y1;
                rh = y2 - y1;
            } else {
                ry = y2;
                rh = y1 - y2;
            }
            return new Rectangle2D.Float(rx, ry, rw, rh);
        }
    }

    public static class Double extends Line2D {

        public double x1;
        public double y1;
        public double x2;
        public double y2;

        public Double() {
        }

        public Double(double x1, double y1, double x2, double y2) {
            setLine(x1, y1, x2, y2);
        }

        public Double(Point2D p1, Point2D p2) {
            setLine(p1, p2);
        }

        @Override
        public double getX1() {
            return x1;
        }

        @Override
        public double getY1() {
            return y1;
        }

        @Override
        public double getX2() {
            return x2;
        }

        @Override
        public double getY2() {
            return y2;
        }

        @Override
        public Point2D getP1() {
            return new Point2D.Double(x1, y1);
        }

        @Override
        public Point2D getP2() {
            return new Point2D.Double(x2, y2);
        }

        @Override
        public void setLine(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public Rectangle2D getBounds2D() {
            double rx, ry, rw, rh;
            if (x1 < x2) {
                rx = x1;
                rw = x2 - x1;
            } else {
                rx = x2;
                rw = x1 - x2;
            }
            if (y1 < y2) {
                ry = y1;
                rh = y2 - y1;
            } else {
                ry = y2;
                rh = y1 - y2;
            }
            return new Rectangle2D.Double(rx, ry, rw, rh);
        }
    }

    /*
     * Line2D path iterator 
     */
    class Iterator implements PathIterator {

        /**
         * The x coordinate of the start line point
         */
        double x1;
        
        /**
         * The y coordinate of the start line point
         */
        double y1;
        
        /**
         * The x coordinate of the end line point
         */
        double x2;
        
        /**
         * The y coordinate of the end line point
         */
        double y2;

        /**
         * The path iterator transformation
         */
        AffineTransform t;

        /**
         * The current segmenet index
         */
        int index;

        /**
         * Constructs a new Line2D.Iterator for given line and transformation
         * @param l - the source Line2D object
         * @param at - the AffineTransform object to apply rectangle path
         */
        Iterator(Line2D l, AffineTransform at) {
            this.x1 = l.getX1();
            this.y1 = l.getY1();
            this.x2 = l.getX2();
            this.y2 = l.getY2();
            this.t = at;
        }

        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        public boolean isDone() {
            return index > 1;
        }

        public void next() {
            index++;
        }

        public int currentSegment(double[] coords) {
            if (isDone()) {
                // awt.4B=Iterator out of bounds
                throw new NoSuchElementException(/*AR Messages.getString(*/"awt.4B"/*AR )*/); //$NON-NLS-1$
            }
            int type;
            if (index == 0) {
                type = SEG_MOVETO;
                coords[0] = x1;
                coords[1] = y1;
            } else {
                type = SEG_LINETO;
                coords[0] = x2;
                coords[1] = y2;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, 1);
            }
            return type;
        }

        public int currentSegment(float[] coords) {
            if (isDone()) {
                // awt.4B=Iterator out of bounds
                throw new NoSuchElementException(/*AR Messages.getString(*/"awt.4B"/*AR )*/); //$NON-NLS-1$
            }
            int type;
            if (index == 0) {
                type = SEG_MOVETO;
                coords[0] = (float)x1;
                coords[1] = (float)y1;
            } else {
                type = SEG_LINETO;
                coords[0] = (float)x2;
                coords[1] = (float)y2;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, 1);
            }
            return type;
        }

    }

    protected Line2D() {
    }

    public abstract double getX1();

    public abstract double getY1();

    public abstract double getX2();

    public abstract double getY2();

    public abstract Point2D getP1();

    public abstract Point2D getP2();

    public abstract void setLine(double x1, double y1, double x2, double y2);

    public void setLine(Point2D p1, Point2D p2) {
        setLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public void setLine(Line2D line) {
        setLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    public static int relativeCCW(double x1, double y1, double x2, double y2, double px, double py) {
        /*
         * A = (x2-x1, y2-y1) P = (px-x1, py-y1)
         */
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double t = px * y2 - py * x2; // PxA
        if (t == 0.0) {
            t = px * x2 + py * y2; // P*A
            if (t > 0.0) {
                px -= x2; // B-A
                py -= y2;
                t = px * x2 + py * y2; // (P-A)*A
                if (t < 0.0) {
                    t = 0.0;
                }
            }
        }

        return t < 0.0 ? -1 : (t > 0.0 ? 1 : 0);
    }

    public int relativeCCW(double px, double py) {
        return relativeCCW(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    public int relativeCCW(Point2D p) {
        return relativeCCW(getX1(), getY1(), getX2(), getY2(), p.getX(), p.getY());
    }

    public static boolean linesIntersect(double x1, double y1, double x2,
            double y2, double x3, double y3, double x4, double y4)
    {
        /*
         * A = (x2-x1, y2-y1) B = (x3-x1, y3-y1) C = (x4-x1, y4-y1) D = (x4-x3,
         * y4-y3) = C-B E = (x1-x3, y1-y3) = -B F = (x2-x3, y2-y3) = A-B
         *
         * Result is ((AxB) * (AxC) <=0) and ((DxE) * (DxF) <= 0)
         *
         * DxE = (C-B)x(-B) = BxB-CxB = BxC DxF = (C-B)x(A-B) = CxA-CxB-BxA+BxB =
         * AxB+BxC-AxC
         */

        x2 -= x1; // A
        y2 -= y1;
        x3 -= x1; // B
        y3 -= y1;
        x4 -= x1; // C
        y4 -= y1;

        double AvB = x2 * y3 - x3 * y2;
        double AvC = x2 * y4 - x4 * y2;

        // Online
        if (AvB == 0.0 && AvC == 0.0) {
            if (x2 != 0.0) {
                return
                    (x4 * x3 <= 0.0) ||
                    ((x3 * x2 >= 0.0) &&
                     (x2 > 0.0 ? x3 <= x2 || x4 <= x2 : x3 >= x2 || x4 >= x2));
            }
            if (y2 != 0.0) {
                return
                    (y4 * y3 <= 0.0) ||
                    ((y3 * y2 >= 0.0) &&
                     (y2 > 0.0 ? y3 <= y2 || y4 <= y2 : y3 >= y2 || y4 >= y2));
            }
            return false;
        }

        double BvC = x3 * y4 - x4 * y3;

        return (AvB * AvC <= 0.0) && (BvC * (AvB + BvC - AvC) <= 0.0);
    }

    public boolean intersectsLine(double x1, double y1, double x2, double y2) {
        return linesIntersect(x1, y1, x2, y2, getX1(), getY1(), getX2(), getY2());
    }

    public boolean intersectsLine(Line2D l) {
        return linesIntersect(l.getX1(), l.getY1(), l.getX2(), l.getY2(), getX1(), getY1(), getX2(), getY2());
    }

    public static double ptSegDistSq(double x1, double y1, double x2, double y2, double px, double py) {
        /*
         * A = (x2 - x1, y2 - y1) P = (px - x1, py - y1)
         */
        x2 -= x1; // A = (x2, y2)
        y2 -= y1;
        px -= x1; // P = (px, py)
        py -= y1;
        double dist;
        if (px * x2 + py * y2 <= 0.0) { // P*A
            dist = px * px + py * py;
        } else {
            px = x2 - px; // P = A - P = (x2 - px, y2 - py)
            py = y2 - py;
            if (px * x2 + py * y2 <= 0.0) { // P*A
                dist = px * px + py * py;
            } else {
                dist = px * y2 - py * x2;
                dist = dist * dist / (x2 * x2 + y2 * y2); // pxA/|A|
            }
        }
        if (dist < 0) {
            dist = 0;
        }
        return dist;
    }

    public static double ptSegDist(double x1, double y1, double x2, double y2, double px, double py) {
        return Math.sqrt(ptSegDistSq(x1, y1, x2, y2, px, py));
    }

    public double ptSegDistSq(double px, double py) {
        return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    public double ptSegDistSq(Point2D p) {
        return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), p.getX(), p.getY());
    }

    public double ptSegDist(double px, double py) {
        return ptSegDist(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    public double ptSegDist(Point2D p) {
        return ptSegDist(getX1(), getY1(), getX2(), getY2(), p.getX(), p.getY());
    }

    public static double ptLineDistSq(double x1, double y1, double x2, double y2, double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double s = px * y2 - py * x2;
        return s * s / (x2 * x2 + y2 * y2);
    }

    public static double ptLineDist(double x1, double y1, double x2, double y2, double px, double py) {
        return Math.sqrt(ptLineDistSq(x1, y1, x2, y2, px, py));
    }

    public double ptLineDistSq(double px, double py) {
        return ptLineDistSq(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    public double ptLineDistSq(Point2D p) {
        return ptLineDistSq(getX1(), getY1(), getX2(), getY2(), p.getX(), p.getY());
    }

    public double ptLineDist(double px, double py) {
        return ptLineDist(getX1(), getY1(), getX2(), getY2(), px, py);
    }

    public double ptLineDist(Point2D p) {
        return ptLineDist(getX1(), getY1(), getX2(), getY2(), p.getX(), p.getY());
    }

    public boolean contains(double px, double py) {
        return false;
    }

    public boolean contains(Point2D p) {
        return false;
    }

    public boolean contains(Rectangle2D r) {
        return false;
    }

    public boolean contains(double rx, double ry, double rw, double rh) {
        return false;
    }

    public boolean intersects(double rx, double ry, double rw, double rh) {
        return intersects(new Rectangle2D.Double(rx, ry, rw, rh));
    }

    public boolean intersects(Rectangle2D r) {
        return r.intersectsLine(getX1(), getY1(), getX2(), getY2());
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return new Iterator(this, at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new Iterator(this, at);
    }

    /*AR @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }*/

}
