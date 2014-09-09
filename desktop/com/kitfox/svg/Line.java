/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on January 26, 2004, 5:25 PM
 */
package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Line extends ShapeElement
{
    public static final String TAG_NAME = "line";
    
    float x1 = 0f;
    float y1 = 0f;
    float x2 = 0f;
    float y2 = 0f;
    Line2D.Float line;

    /**
     * Creates a new instance of Rect
     */
    public Line()
    {
    }

    public String getTagName()
    {
        return TAG_NAME;
    }

    protected void build() throws SVGException
    {
        super.build();

        StyleAttribute sty = new StyleAttribute();

        if (getPres(sty.setName("x1")))
        {
            x1 = sty.getFloatValueWithUnits();
        }

        if (getPres(sty.setName("y1")))
        {
            y1 = sty.getFloatValueWithUnits();
        }

        if (getPres(sty.setName("x2")))
        {
            x2 = sty.getFloatValueWithUnits();
        }

        if (getPres(sty.setName("y2")))
        {
            y2 = sty.getFloatValueWithUnits();
        }

        line = new Line2D.Float(x1, y1, x2, y2);
    }

    public void render(Graphics2D g) throws SVGException
    {
        beginLayer(g);
        renderShape(g, line);
        finishLayer(g);
    }

    public Shape getShape()
    {
        return shapeToParent(line);
    }

    public Rectangle2D getBoundingBox() throws SVGException
    {
        return boundsToParent(includeStrokeInBounds(line.getBounds2D()));
    }

    /**
     * Updates all attributes in this diagram associated with a time event. Ie,
     * all attributes with track information.
     *
     * @return - true if this node has changed state as a result of the time
     * update
     */
    public boolean updateTime(double curTime) throws SVGException
    {
//        if (trackManager.getNumTracks() == 0) return false;
        boolean changeState = super.updateTime(curTime);

        //Get current values for parameters
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;

        if (getPres(sty.setName("x1")))
        {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x1)
            {
                x1 = newVal;
                shapeChange = true;
            }
        }

        if (getPres(sty.setName("y1")))
        {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y1)
            {
                y1 = newVal;
                shapeChange = true;
            }
        }

        if (getPres(sty.setName("x2")))
        {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x2)
            {
                x2 = newVal;
                shapeChange = true;
            }
        }

        if (getPres(sty.setName("y2")))
        {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y2)
            {
                y2 = newVal;
                shapeChange = true;
            }
        }

        if (shapeChange)
        {
            build();
        }

        return changeState || shapeChange;
    }
}
