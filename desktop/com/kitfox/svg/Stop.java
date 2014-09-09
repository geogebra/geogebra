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
 * Created on January 26, 2004, 1:56 AM
 */
package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Color;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Stop extends SVGElement
{

    public static final String TAG_NAME = "stop";
    float offset = 0f;
    float opacity = 1f;
    Color color = Color.black;

    /**
     * Creates a new instance of Stop
     */
    public Stop()
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

        if (getPres(sty.setName("offset")))
        {
            offset = sty.getFloatValue();
            String units = sty.getUnits();
            if (units != null && units.equals("%"))
            {
                offset /= 100f;
            }
            if (offset > 1)
            {
                offset = 1;
            }
            if (offset < 0)
            {
                offset = 0;
            }
        }

        if (getStyle(sty.setName("stop-color")))
        {
            color = sty.getColorValue();
        }

        if (getStyle(sty.setName("stop-opacity")))
        {
            opacity = sty.getRatioValue();
        }
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

        //Get current values for parameters
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;

        if (getPres(sty.setName("offset")))
        {
            float newVal = sty.getFloatValue();
            if (newVal != offset)
            {
                offset = newVal;
                shapeChange = true;
            }
        }

        if (getStyle(sty.setName("stop-color")))
        {
            Color newVal = sty.getColorValue();
            if (newVal != color)
            {
                color = newVal;
                shapeChange = true;
            }
        }

        if (getStyle(sty.setName("stop-opacity")))
        {
            float newVal = sty.getFloatValue();
            if (newVal != opacity)
            {
                opacity = newVal;
                shapeChange = true;
            }
        }

        return shapeChange;
    }
}
