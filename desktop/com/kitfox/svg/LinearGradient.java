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
 * Created on January 26, 2004, 1:54 AM
 */
package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class LinearGradient extends Gradient
{
    public static final String TAG_NAME = "lineargradient";
    
    float x1 = 0f;
    float y1 = 0f;
    float x2 = 1f;
    float y2 = 0f;

    /**
     * Creates a new instance of LinearGradient
     */
    public LinearGradient()
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
    }

    public Paint getPaint(Rectangle2D bounds, AffineTransform xform)
    {
        com.kitfox.svg.batik.MultipleGradientPaint.CycleMethodEnum method;
        switch (spreadMethod)
        {
            default:
            case SM_PAD:
                method = com.kitfox.svg.batik.MultipleGradientPaint.NO_CYCLE;
                break;
            case SM_REPEAT:
                method = com.kitfox.svg.batik.MultipleGradientPaint.REPEAT;
                break;
            case SM_REFLECT:
                method = com.kitfox.svg.batik.MultipleGradientPaint.REFLECT;
                break;
        }

        Paint paint;
        Point2D.Float pt1 = new Point2D.Float(x1, y1);
        Point2D.Float pt2 = new Point2D.Float(x2, y2);
        if (pt1.equals(pt2))
        {
            Color[] colors = getStopColors();
            paint = colors.length > 0 ? colors[0] : Color.black;
        } else if (gradientUnits == GU_USER_SPACE_ON_USE)
        {
            paint = new com.kitfox.svg.batik.LinearGradientPaint(
                pt1,
                pt2,
                getStopFractions(),
                getStopColors(),
                method,
                com.kitfox.svg.batik.MultipleGradientPaint.SRGB,
                gradientTransform == null
                ? new AffineTransform()
                : gradientTransform);
        } else
        {
            AffineTransform viewXform = new AffineTransform();
            viewXform.translate(bounds.getX(), bounds.getY());

            //This is a hack to get around shapes that have a width or height of 0.  Should be close enough to the true answer.
            double width = Math.max(1, bounds.getWidth());
            double height = Math.max(1, bounds.getHeight());
            viewXform.scale(width, height);

            if (gradientTransform != null)
            {
                viewXform.concatenate(gradientTransform);
            }

            paint = new com.kitfox.svg.batik.LinearGradientPaint(
                pt1,
                pt2,
                getStopFractions(),
                getStopColors(),
                method,
                com.kitfox.svg.batik.MultipleGradientPaint.SRGB,
                viewXform);
        }

        return paint;
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
//        if (trackManager.getNumTracks() == 0) return stopChange;
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

        return changeState || shapeChange;
    }
}
