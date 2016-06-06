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
package org.geogebra.common.kernel.discrete.geom.algorithms.logging.convex_hull.jarvis_march;

import org.geogebra.common.kernel.discrete.geom.Point2D;
import org.geogebra.common.kernel.discrete.geom.algorithms.logging.TemporaryLogEvent;

/**
 *
 * @author cyberpython
 */
public class JarvisPointsCheckEvent extends TemporaryLogEvent {

    Point2D referencePoint;
    Point2D p1;
    Double polarAngle;
    boolean rightChain;

    public JarvisPointsCheckEvent(Point2D p0, Point2D p1, Double polarAngle, boolean rightChain) {

        this.referencePoint = p0;
        this.p1 = p1;
        this.polarAngle = polarAngle==null ? null : new Double(polarAngle);
        this.rightChain = rightChain;

    }

    public Point2D getReferencePoint(){
        return this.referencePoint;
    }

    public Point2D getPoint(){
        return this.p1;
    }

    public Double getPolarAngle(){
        return this.polarAngle;
    }

    public boolean isRightChainEvent(){
        return this.rightChain;
    }

}
