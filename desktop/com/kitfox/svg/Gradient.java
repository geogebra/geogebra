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
 * Created on January 26, 2004, 3:25 AM
 */
package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
abstract public class Gradient extends FillElement
{
    public static final String TAG_NAME = "gradient";
    
    public static final int SM_PAD = 0;
    public static final int SM_REPEAT = 1;
    public static final int SM_REFLECT = 2;
    int spreadMethod = SM_PAD;
    public static final int GU_OBJECT_BOUNDING_BOX = 0;
    public static final int GU_USER_SPACE_ON_USE = 1;
    protected int gradientUnits = GU_OBJECT_BOUNDING_BOX;
    //Either this gradient contains a list of stops, or it will take it's
    // stops from the referenced gradient
    ArrayList stops = new ArrayList();
    URI stopRef = null;
    protected AffineTransform gradientTransform = null;
    
    //Cache arrays of stop values here
    float[] stopFractions;
    Color[] stopColors;

    /**
     * Creates a new instance of Gradient
     */
    public Gradient()
    {
    }

    public String getTagName()
    {
        return TAG_NAME;
    }

    /**
     * Called after the start element but before the end element to indicate
     * each child tag that has been processed
     */
    public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException
    {
        super.loaderAddChild(helper, child);

        if (!(child instanceof Stop))
        {
            return;
        }
        appendStop((Stop) child);
    }

    protected void build() throws SVGException
    {
        super.build();

        StyleAttribute sty = new StyleAttribute();
        String strn;

        if (getPres(sty.setName("spreadMethod")))
        {
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("repeat"))
            {
                spreadMethod = SM_REPEAT;
            } else if (strn.equals("reflect"))
            {
                spreadMethod = SM_REFLECT;
            } else
            {
                spreadMethod = SM_PAD;
            }
        }

        if (getPres(sty.setName("gradientUnits")))
        {
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("userspaceonuse"))
            {
                gradientUnits = GU_USER_SPACE_ON_USE;
            } else
            {
                gradientUnits = GU_OBJECT_BOUNDING_BOX;
            }
        }

        if (getPres(sty.setName("gradientTransform")))
        {
            gradientTransform = parseTransform(sty.getStringValue());
        }
        //If we still don't have one, set it to identity
        if (gradientTransform == null)
        {
            gradientTransform = new AffineTransform();
        }


        //Check to see if we're using our own stops or referencing someone else's
        if (getPres(sty.setName("xlink:href")))
        {
            try
            {
                stopRef = sty.getURIValue(getXMLBase());
//System.err.println("Gradient: " + sty.getStringValue() + ", " + getXMLBase() + ", " + src);
//                URI src = getXMLBase().resolve(href);
//                stopRef = (Gradient)diagram.getUniverse().getElement(src);
            } catch (Exception e)
            {
                throw new SVGException("Could not resolve relative URL in Gradient: " + sty.getStringValue() + ", " + getXMLBase(), e);
            }
        }
    }

    public float[] getStopFractions()
    {
        if (stopRef != null)
        {
            Gradient grad = (Gradient) diagram.getUniverse().getElement(stopRef);
            return grad.getStopFractions();
        }

        if (stopFractions != null)
        {
            return stopFractions;
        }

        stopFractions = new float[stops.size()];
        int idx = 0;
        for (Iterator it = stops.iterator(); it.hasNext();)
        {
            Stop stop = (Stop) it.next();
            float val = stop.offset;
            if (idx != 0 && val < stopFractions[idx - 1])
            {
                val = stopFractions[idx - 1];
            }
            stopFractions[idx++] = val;
        }

        return stopFractions;
    }

    public Color[] getStopColors()
    {
        if (stopRef != null)
        {
            Gradient grad = (Gradient) diagram.getUniverse().getElement(stopRef);
            return grad.getStopColors();
        }

        if (stopColors != null)
        {
            return stopColors;
        }

        stopColors = new Color[stops.size()];
        int idx = 0;
        for (Iterator it = stops.iterator(); it.hasNext();)
        {
            Stop stop = (Stop) it.next();
            int stopColorVal = stop.color.getRGB();
            Color stopColor = new Color((stopColorVal >> 16) & 0xff, (stopColorVal >> 8) & 0xff, stopColorVal & 0xff, clamp((int) (stop.opacity * 255), 0, 255));
            stopColors[idx++] = stopColor;
        }

        return stopColors;
    }

    public void setStops(Color[] colors, float[] fractions)
    {
        if (colors.length != fractions.length)
        {
            throw new IllegalArgumentException();
        }

        this.stopColors = colors;
        this.stopFractions = fractions;
        stopRef = null;
    }

    private int clamp(int val, int min, int max)
    {
        if (val < min)
        {
            return min;
        }
        if (val > max)
        {
            return max;
        }
        return val;
    }

    public void setStopRef(URI grad)
    {
        stopRef = grad;
    }

    public void appendStop(Stop stop)
    {
        stops.add(stop);
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
        boolean stateChange = false;

        //Get current values for parameters
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        String strn;


        if (getPres(sty.setName("spreadMethod")))
        {
            int newVal;
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("repeat"))
            {
                newVal = SM_REPEAT;
            } else if (strn.equals("reflect"))
            {
                newVal = SM_REFLECT;
            } else
            {
                newVal = SM_PAD;
            }
            if (spreadMethod != newVal)
            {
                spreadMethod = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("gradientUnits")))
        {
            int newVal;
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("userspaceonuse"))
            {
                newVal = GU_USER_SPACE_ON_USE;
            } else
            {
                newVal = GU_OBJECT_BOUNDING_BOX;
            }
            if (newVal != gradientUnits)
            {
                gradientUnits = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("gradientTransform")))
        {
            AffineTransform newVal = parseTransform(sty.getStringValue());
            if (newVal != null && newVal.equals(gradientTransform))
            {
                gradientTransform = newVal;
                stateChange = true;
            }
        }


        //Check to see if we're using our own stops or referencing someone else's
        if (getPres(sty.setName("xlink:href")))
        {
            try
            {
                URI newVal = sty.getURIValue(getXMLBase());
                if ((newVal == null && stopRef != null) || !newVal.equals(stopRef))
                {
                    stopRef = newVal;
                    stateChange = true;
                }
            } catch (Exception e)
            {
                Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not parse xlink:href", e);
            }
        }

        //Check stops, if any
        for (Iterator it = stops.iterator(); it.hasNext();)
        {
            Stop stop = (Stop) it.next();
            if (stop.updateTime(curTime))
            {
                stateChange = true;
                stopFractions = null;
                stopColors = null;
            }
        }

        return stateChange;
    }
}
