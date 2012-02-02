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

import java.util.NoSuchElementException;

//AR import org.apache.harmony.awt.internal.nls.Messages;

public abstract class Arc2D extends RectangularShape {

    public final static int OPEN = 0;
    public final static int CHORD = 1;
    public final static int PIE = 2;

    public static class Float extends Arc2D {

        public float x;
        public float y;
        public float width;
        public float height;
        public float start;
        public float extent;

        public Float() {
            super(OPEN);
        }

        public Float(int type) {
            super(type);
        }

        public Float(float x, float y, float width, float height, float start, float extent, int type) {
            super(type);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }

        public Float(Rectangle2D bounds, float start, float extent, int type) {
            super(type);
            this.x = (float)bounds.getX();
            this.y = (float)bounds.getY();
            this.width = (float)bounds.getWidth();
            this.height = (float)bounds.getHeight();
            this.start = start;
            this.extent = extent;
        }

        @Override
        public double getX() {
            return x;
        }

       @Override
    public double getY() {
            return y;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public double getAngleStart() {
            return start;
        }

        @Override
        public double getAngleExtent() {
            return extent;
        }

        @Override
        public boolean isEmpty() {
            return width <= 0.0f || height <= 0.0f;
        }

        @Override
        public void setArc(double x, double y, double width, double height,
                double start, double extent, int type)
        {
            this.setArcType(type);
            this.x = (float)x;
            this.y = (float)y;
            this.width = (float)width;
            this.height = (float)height;
            this.start = (float)start;
            this.extent = (float)extent;
        }

        @Override
        public void setAngleStart(double start) {
            this.start = (float)start;
        }

        @Override
        public void setAngleExtent(double extent) {
            this.extent = (float)extent;
        }

        @Override
        protected Rectangle2D makeBounds(double x, double y, double width, double height) {
            return new Rectangle2D.Float((float)x, (float)y, (float)width, (float)height);
        }

    }

    public static class Double extends Arc2D {

        public double x;
        public double y;
        public double width;
        public double height;
        public double start;
        public double extent;

        public Double() {
            super(OPEN);
        }

        public Double(int type) {
            super(type);
        }

        public Double(double x, double y, double width, double height,
                double start, double extent, int type)
        {
            super(type);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }

        public Double(Rectangle2D bounds, double start, double extent, int type) {
            super(type);
            this.x = bounds.getX();
            this.y = bounds.getY();
            this.width = bounds.getWidth();
            this.height = bounds.getHeight();
            this.start = start;
            this.extent = extent;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public double getAngleStart() {
            return start;
        }

        @Override
        public double getAngleExtent() {
            return extent;
        }

        @Override
        public boolean isEmpty() {
            return width <= 0.0 || height <= 0.0;
        }

        @Override
        public void setArc(double x, double y, double width, double height,
                double start, double extent, int type)
        {
            this.setArcType(type);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }

        @Override
        public void setAngleStart(double start) {
            this.start = start;
        }

        @Override
        public void setAngleExtent(double extent) {
            this.extent = extent;
        }

        @Override
        protected Rectangle2D makeBounds(double x, double y, double width, double height) {
            return new Rectangle2D.Double(x, y, width, height);
        }

    }

    /*
     * Arc2D path iterator  
     */
    class Iterator implements PathIterator {

        /**
         * The x coordinate of left-upper corner of the arc rectangle bounds
         */
        double x;

        /**
         * The y coordinate of left-upper corner of the arc rectangle bounds
         */
        double y;

        /**
         * The width of the arc rectangle bounds
         */
        double width;
        
        /**
         * The height of the arc rectangle bounds
         */
        double height;
        
        /**
         * The start angle of the arc in degrees
         */
        double angle;
        
        /**
         * The angle extent in degrees
         */
        double extent;
         
        /**
         * The closure type of the arc
         */
        int type;
        
        /**
         * The path iterator transformation
         */
        AffineTransform t;
        
        /**
         * The current segmenet index
         */
        int index;
        
        /**
         * The number of arc segments the source arc subdivided to be approximated by Bezier curves.
         * Depends on extent value.  
         */
        int arcCount;
        
        /**
         * The number of line segments. Depends on closure type. 
         */
        int lineCount;
        
        /**
         * The step to calculate next arc subdivision point
         */
        double step;
        
        /**
         * The tempopary value of cosinus of the current angle 
         */
        double cos;

        /**
         * The tempopary value of sinus of the current angle 
         */
        double sin;
        
        /**
         * The coefficient to calculate control points of Bezier curves
         */
        double k;
        
        /**
         * The tempopary value of x coordinate of the Bezier curve control vector
         */
        double kx;

        /**
         * The tempopary value of y coordinate of the Bezier curve control vector
         */
        double ky;
        
        /**
         * The x coordinate of the first path point (MOVE_TO)
         */
        double mx;
        
        /**
         * The y coordinate of the first path point (MOVE_TO)
         */
        double my;

        /**
         * Constructs a new Arc2D.Iterator for given line and transformation
         * @param a - the source Arc2D object
         * @param at - the AffineTransform object to apply rectangle path
         */
        Iterator(Arc2D a, AffineTransform t) {
            if (width < 0 || height < 0) {
                arcCount = 0;
                lineCount = 0;
                index = 1;
                return;
            }

            this.width = a.getWidth() / 2.0;
            this.height = a.getHeight() / 2.0;
            this.x = a.getX() + width;
            this.y = a.getY() + height;
            this.angle = -Math.toRadians(a.getAngleStart());
            this.extent = -a.getAngleExtent();
            this.type = a.getArcType();
            this.t = t;

            if (Math.abs(extent) >= 360.0) {
                arcCount = 4;
                k = 4.0 / 3.0 * (Math.sqrt(2.0) - 1.0);
                step = Math.PI / 2.0;
                if (extent < 0.0) {
                    step = -step;
                    k = -k;
                }
            } else {
                arcCount = (int)Math.rint(Math.abs(extent) / 90.0);
                step = Math.toRadians(extent / arcCount);
                k = 4.0 / 3.0 * (1.0 - Math.cos(step / 2.0))
                        / Math.sin(step / 2.0);
            }

            lineCount = 0;
            if (type == Arc2D.CHORD) {
                lineCount++;
            } else if (type == Arc2D.PIE) {
                lineCount += 2;
            }
        }

        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        public boolean isDone() {
            return index > arcCount + lineCount;
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
            int count;
            if (index == 0) {
                type = SEG_MOVETO;
                count = 1;
                cos = Math.cos(angle);
                sin = Math.sin(angle);
                kx = k * width * sin;
                ky = k * height * cos;
                coords[0] = mx = x + cos * width;
                coords[1] = my = y + sin * height;
            } else if (index <= arcCount) {
                type = SEG_CUBICTO;
                count = 3;
                coords[0] = mx - kx;
                coords[1] = my + ky;
                angle += step;
                cos = Math.cos(angle);
                sin = Math.sin(angle);
                kx = k * width * sin;
                ky = k * height * cos;
                coords[4] = mx = x + cos * width;
                coords[5] = my = y + sin * height;
                coords[2] = mx + kx;
                coords[3] = my - ky;
            } else if (index == arcCount + lineCount) {
                type = SEG_CLOSE;
                count = 0;
            } else {
                type = SEG_LINETO;
                count = 1;
                coords[0] = x;
                coords[1] = y;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, count);
            }
            return type;
        }

        public int currentSegment(float[] coords) {
            if (isDone()) {
                // awt.4B=Iterator out of bounds
                throw new NoSuchElementException(/*AR Messages.getString(*/"awt.4B"/*AR )*/); //$NON-NLS-1$
            }
            int type;
            int count;
            if (index == 0) {
                type = SEG_MOVETO;
                count = 1;
                cos = Math.cos(angle);
                sin = Math.sin(angle);
                kx = k * width * sin;
                ky = k * height * cos;
                coords[0] = (float)(mx = x + cos * width);
                coords[1] = (float)(my = y + sin * height);
            } else if (index <= arcCount) {
                type = SEG_CUBICTO;
                count = 3;
                coords[0] = (float)(mx - kx);
                coords[1] = (float)(my + ky);
                angle += step;
                cos = Math.cos(angle);
                sin = Math.sin(angle);
                kx = k * width * sin;
                ky = k * height * cos;
                coords[4] = (float)(mx = x + cos * width);
                coords[5] = (float)(my = y + sin * height);
                coords[2] = (float)(mx + kx);
                coords[3] = (float)(my - ky);
            } else if (index == arcCount + lineCount) {
                type = SEG_CLOSE;
                count = 0;
            } else {
                type = SEG_LINETO;
                count = 1;
                coords[0] = (float)x;
                coords[1] = (float)y;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, count);
            }
            return type;
        }

    }

    /**
     * The closure type of the arc
     */
    private int type;

    protected Arc2D(int type) {
        setArcType(type);
    }

    protected abstract Rectangle2D makeBounds(double x, double y, double width, double height);

    public abstract double getAngleStart();

    public abstract double getAngleExtent();

    public abstract void setAngleStart(double start);

    public abstract void setAngleExtent(double extent);

    public abstract void setArc(double x, double y, double width,
            double height, double start, double extent, int type);

    public int getArcType() {
        return type;
    }

    public void setArcType(int type) {
        if (type != OPEN && type != CHORD && type != PIE) {
            // awt.205=Invalid type of Arc: {0}
            throw new IllegalArgumentException(/*AR Messages.getString(*/"awt.205"/*AR , type)*/); //$NON-NLS-1$
        }
        this.type = type;
    }

    public Point2D getStartPoint() {
        double a = Math.toRadians(getAngleStart());
        return new Point2D.Double(
                getX() + (1.0 + Math.cos(a)) * getWidth() / 2.0,
                getY() + (1.0 - Math.sin(a)) * getHeight() / 2.0);
    }

    public Point2D getEndPoint() {
        double a = Math.toRadians(getAngleStart() + getAngleExtent());
        return new Point2D.Double(
                getX() + (1.0 + Math.cos(a)) * getWidth() / 2.0,
                getY() + (1.0 - Math.sin(a)) * getHeight() / 2.0);
    }

    public Rectangle2D getBounds2D() {
        if (isEmpty()) {
            return makeBounds(getX(), getY(), getWidth(), getHeight());
        }
        double rx1 = getX();
        double ry1 = getY();
        double rx2 = rx1 + getWidth();
        double ry2 = ry1 + getHeight();

        Point2D p1 = getStartPoint();
        Point2D p2 = getEndPoint();

        double bx1 = containsAngle(180.0) ? rx1 : Math.min(p1.getX(), p2.getX());
        double by1 = containsAngle(90.0)  ? ry1 : Math.min(p1.getY(), p2.getY());
        double bx2 = containsAngle(0.0)   ? rx2 : Math.max(p1.getX(), p2.getX());
        double by2 = containsAngle(270.0) ? ry2 : Math.max(p1.getY(), p2.getY());

        if (type == PIE) {
            double cx = getCenterX();
            double cy = getCenterY();
            bx1 = Math.min(bx1, cx);
            by1 = Math.min(by1, cy);
            bx2 = Math.max(bx2, cx);
            by2 = Math.max(by2, cy);
        }
        return makeBounds(bx1, by1, bx2 - bx1, by2 - by1);
    }

    @Override
    public void setFrame(double x, double y, double width, double height) {
        setArc(x, y, width, height, getAngleStart(), getAngleExtent(), type);
    }

    public void setArc(Point2D point, Dimension2D size, double start, double extent, int type) {
        setArc(point.getX(), point.getY(), size.getWidth(), size.getHeight(), start, extent, type);
    }

    public void setArc(Rectangle2D rect, double start, double extent, int type) {
        setArc(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), start, extent, type);
    }

    public void setArc(Arc2D arc) {
        setArc(arc.getX(), arc.getY(), arc.getWidth(), arc.getHeight(), arc
                .getAngleStart(), arc.getAngleExtent(), arc.getArcType());
    }

    public void setArcByCenter(double x, double y, double radius, double start, double extent, int type) {
        setArc(x - radius, y - radius, radius * 2.0, radius * 2.0, start, extent, type);
    }

    public void setArcByTangent(Point2D p1, Point2D p2, Point2D p3, double radius) {
        // Used simple geometric calculations of arc center, radius and angles by tangents
        double a1 = -Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX());
        double a2 = -Math.atan2(p3.getY() - p2.getY(), p3.getX() - p2.getX());
        double am = (a1 + a2) / 2.0;
        double ah = a1 - am;
        double d = radius / Math.abs(Math.sin(ah));
        double x = p2.getX() + d * Math.cos(am);
        double y = p2.getY() - d * Math.sin(am);
        ah = ah >= 0.0 ? Math.PI * 1.5 - ah : Math.PI * 0.5 - ah;
        a1 = getNormAngle(Math.toDegrees(am - ah));
        a2 = getNormAngle(Math.toDegrees(am + ah));
        double delta = a2 - a1;
        if (delta <= 0.0) {
            delta += 360.0;
        }
        setArcByCenter(x, y, radius, a1, delta, type);
    }

    public void setAngleStart(Point2D point) {
        double angle = Math.atan2(point.getY() - getCenterY(), point.getX() - getCenterX());
        setAngleStart(getNormAngle(-Math.toDegrees(angle)));
    }

    public void setAngles(double x1, double y1, double x2, double y2) {
        double cx = getCenterX();
        double cy = getCenterY();
        double a1 = getNormAngle(-Math.toDegrees(Math.atan2(y1 - cy, x1 - cx)));
        double a2 = getNormAngle(-Math.toDegrees(Math.atan2(y2 - cy, x2 - cx)));
        a2 -= a1;
        if (a2 <= 0.0) {
            a2 += 360.0;
        }
        setAngleStart(a1);
        setAngleExtent(a2);
    }

    public void setAngles(Point2D p1, Point2D p2) {
        setAngles(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * Normalizes angle 
     * @param angle - the source angle in degrees
     * @return a normalized angle
     */
    double getNormAngle(double angle) {
        double n = Math.floor(angle / 360.0);
        return angle - n * 360.0;
    }

    public boolean containsAngle(double angle) {
        double extent = getAngleExtent();
        if (extent >= 360.0) {
            return true;
        }
        angle = getNormAngle(angle);
        double a1 = getNormAngle(getAngleStart());
        double a2 = a1 + extent;
        if (a2 > 360.0) {
            return angle >= a1 || angle <= a2 - 360.0;
        }
        if (a2 < 0.0) {
            return angle >= a2 + 360.0 || angle <= a1;
        }
        return extent > 0.0 ? a1 <= angle && angle <= a2 : a2 <= angle
                && angle <= a1;
    }

    public boolean contains(double px, double py) {
        // Normalize point
        double nx = (px - getX()) / getWidth() - 0.5;
        double ny = (py - getY()) / getHeight() - 0.5;

        if ((nx * nx + ny * ny) > 0.25) {
            return false;
        }

        double extent = getAngleExtent();
        double absExtent = Math.abs(extent);
        if (absExtent >= 360.0) {
            return true;
        }

        boolean containsAngle = containsAngle(Math.toDegrees(-Math
                .atan2(ny, nx)));
        if (type == PIE) {
            return containsAngle;
        }
        if (absExtent <= 180.0 && !containsAngle) {
            return false;
        }

        Line2D l = new Line2D.Double(getStartPoint(), getEndPoint());
        int ccw1 = l.relativeCCW(px, py);
        int ccw2 = l.relativeCCW(getCenterX(), getCenterY());
        return ccw1 == 0 || ccw2 == 0
                || ((ccw1 + ccw2) == 0 ^ absExtent > 180.0);
    }

    public boolean contains(double rx, double ry, double rw, double rh) {

        if (!(contains(rx, ry) && contains(rx + rw, ry)
                && contains(rx + rw, ry + rh) && contains(rx, ry + rh))) {
            return false;
        }

        double absExtent = Math.abs(getAngleExtent());
        if (type != PIE || absExtent <= 180.0 || absExtent >= 360.0) {
            return true;
        }

        Rectangle2D r = new Rectangle2D.Double(rx, ry, rw, rh);

        double cx = getCenterX();
        double cy = getCenterY();
        if (r.contains(cx, cy)) {
            return false;
        }

        Point2D p1 = getStartPoint();
        Point2D p2 = getEndPoint();

        return !r.intersectsLine(cx, cy, p1.getX(), p1.getY())
                && !r.intersectsLine(cx, cy, p2.getX(), p2.getY());
    }

    @Override
    public boolean contains(Rectangle2D rect) {
        return contains(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    public boolean intersects(double rx, double ry, double rw, double rh) {

        if (isEmpty() || rw <= 0.0 || rh <= 0.0) {
            return false;
        }

        // Check: Does arc contain rectangle's points
        if (contains(rx, ry) || contains(rx + rw, ry) || contains(rx, ry + rh)
                || contains(rx + rw, ry + rh)) {
            return true;
        }

        double cx = getCenterX();
        double cy = getCenterY();
        Point2D p1 = getStartPoint();
        Point2D p2 = getEndPoint();
        Rectangle2D r = new Rectangle2D.Double(rx, ry, rw, rh);

        // Check: Does rectangle contain arc's points
        if (r.contains(p1) || r.contains(p2) || (type == PIE && r.contains(cx, cy))) {
            return true;
        }

        if (type == PIE) {
            if (r.intersectsLine(p1.getX(), p1.getY(), cx, cy) ||
                r.intersectsLine(p2.getX(), p2.getY(), cx, cy))
            {
                return true;
            }
        } else {
            if (r.intersectsLine(p1.getX(), p1.getY(), p2.getX(), p2.getY())) {
                return true;
            }
        }

        // Nearest rectangle point
        double nx = cx < rx ? rx : (cx > rx + rw ? rx + rw : cx);
        double ny = cy < ry ? ry : (cy > ry + rh ? ry + rh : cy);
        return contains(nx, ny);
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return new Iterator(this, at);
    }

}
