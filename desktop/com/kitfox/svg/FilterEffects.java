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
 * Created on March 18, 2004, 6:52 AM
 */
package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.net.URI;
import java.net.URL;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class FilterEffects extends SVGElement
{
    public static final String TAG_NAME = "filtereffects";
    
    public static final int FP_SOURCE_GRAPHIC = 0;
    public static final int FP_SOURCE_ALPHA = 1;
    public static final int FP_BACKGROUND_IMAGE = 2;
    public static final int FP_BACKGROUND_ALPHA = 3;
    public static final int FP_FILL_PAINT = 4;
    public static final int FP_STROKE_PAINT = 5;
    public static final int FP_CUSTOM = 5;
    private int filterPrimitiveTypeIn;
    private String filterPrimitiveRefIn;
    float x = 0f;
    float y = 0f;
    float width = 1f;
    float height = 1f;
    String result = "defaultFilterName";
    URL href = null;

    /**
     * Creates a new instance of FillElement
     */
    public FilterEffects()
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

        if (child instanceof FilterEffects)
        {
//            filterEffects.add(child);
        }
    }

    protected void build() throws SVGException
    {
        super.build();

        StyleAttribute sty = new StyleAttribute();
        String strn;
        /*
         if (getPres(sty.setName("filterUnits")))
         {
         strn = sty.getStringValue().toLowerCase();
         if (strn.equals("userspaceonuse")) filterUnits = FU_USER_SPACE_ON_USE;
         else filterUnits = FU_OBJECT_BOUNDING_BOX;
         }

         if (getPres(sty.setName("primitiveUnits")))
         {
         strn = sty.getStringValue().toLowerCase();
         if (strn.equals("userspaceonuse")) primitiveUnits = PU_USER_SPACE_ON_USE;
         else primitiveUnits = PU_OBJECT_BOUNDING_BOX;
         }

         if (getPres(sty.setName("x"))) x = sty.getFloatValue();

         if (getPres(sty.setName("y"))) y = sty.getFloatValue();

         if (getPres(sty.setName("width"))) width = sty.getFloatValue();

         if (getPres(sty.setName("height"))) height = sty.getFloatValue();

         try {
         if (getPres(sty.setName("xlink:href")))
         {
         URI src = sty.getURIValue(getXMLBase());
         href = src.toURL();
         }
         }
         catch (Exception e)
         {
         throw new SVGException(e);
         }
         */
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public boolean updateTime(double curTime) throws SVGException
    {
//        if (trackManager.getNumTracks() == 0) return false;

        //Get current values for parameters
        StyleAttribute sty = new StyleAttribute();
        boolean stateChange = false;

        if (getPres(sty.setName("x")))
        {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != x)
            {
                x = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("y")))
        {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != y)
            {
                y = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("width")))
        {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != width)
            {
                width = newVal;
                stateChange = true;
            }
        }

        if (getPres(sty.setName("height")))
        {
            float newVal = sty.getFloatValueWithUnits();
            if (newVal != height)
            {
                height = newVal;
                stateChange = true;
            }
        }

        try
        {
            if (getPres(sty.setName("xlink:href")))
            {
                URI src = sty.getURIValue(getXMLBase());
                URL newVal = src.toURL();

                if (!newVal.equals(href))
                {
                    href = newVal;
                    stateChange = true;
                }
            }
        } catch (Exception e)
        {
            throw new SVGException(e);
        }

        /*
         if (getPres(sty.setName("filterUnits")))
         {
         int newVal;
         String strn = sty.getStringValue().toLowerCase();
         if (strn.equals("userspaceonuse")) newVal = FU_USER_SPACE_ON_USE;
         else newVal = FU_OBJECT_BOUNDING_BOX;
         if (newVal != filterUnits)
         {
         filterUnits = newVal;
         stateChange = true;
         }
         }

         if (getPres(sty.setName("primitiveUnits")))
         {
         int newVal;
         String strn = sty.getStringValue().toLowerCase();
         if (strn.equals("userspaceonuse")) newVal = PU_USER_SPACE_ON_USE;
         else newVal = PU_OBJECT_BOUNDING_BOX;
         if (newVal != filterUnits)
         {
         primitiveUnits = newVal;
         stateChange = true;
         }
         }

         */

        return stateChange;
    }
}
