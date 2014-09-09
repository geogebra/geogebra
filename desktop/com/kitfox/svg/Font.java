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
 * Created on February 20, 2004, 10:00 PM
 */
package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.util.HashMap;

/**
 * Implements an embedded font.
 *
 * SVG specification: http://www.w3.org/TR/SVG/fonts.html
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Font extends SVGElement
{

    public static final String TAG_NAME = "font";
    int horizOriginX = 0;
    int horizOriginY = 0;
    int horizAdvX = -1;  //Must be specified
    int vertOriginX = -1;  //Defaults to horizAdvX / 2
    int vertOriginY = -1;  //Defaults to font's ascent
    int vertAdvY = -1;  //Defaults to one 'em'.  See font-face
    FontFace fontFace = null;
    MissingGlyph missingGlyph = null;
    final HashMap glyphs = new HashMap();

    /**
     * Creates a new instance of Font
     */
    public Font()
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

        if (child instanceof Glyph)
        {
            glyphs.put(((Glyph) child).getUnicode(), child);
        } else if (child instanceof MissingGlyph)
        {
            missingGlyph = (MissingGlyph) child;
        } else if (child instanceof FontFace)
        {
            fontFace = (FontFace) child;
        }
    }

    public void loaderEndElement(SVGLoaderHelper helper) throws SVGParseException
    {
        super.loaderEndElement(helper);

        //build();

        helper.universe.registerFont(this);
    }

    protected void build() throws SVGException
    {
        super.build();

        StyleAttribute sty = new StyleAttribute();

        if (getPres(sty.setName("horiz-origin-x")))
        {
            horizOriginX = sty.getIntValue();
        }

        if (getPres(sty.setName("horiz-origin-y")))
        {
            horizOriginY = sty.getIntValue();
        }

        if (getPres(sty.setName("horiz-adv-x")))
        {
            horizAdvX = sty.getIntValue();
        }

        if (getPres(sty.setName("vert-origin-x")))
        {
            vertOriginX = sty.getIntValue();
        }

        if (getPres(sty.setName("vert-origin-y")))
        {
            vertOriginY = sty.getIntValue();
        }

        if (getPres(sty.setName("vert-adv-y")))
        {
            vertAdvY = sty.getIntValue();
        }
    }

    public FontFace getFontFace()
    {
        return fontFace;
    }

    public MissingGlyph getGlyph(String unicode)
    {
        Glyph retVal = (Glyph) glyphs.get(unicode);
        if (retVal == null)
        {
            return missingGlyph;
        }
        return retVal;
    }

    public int getHorizOriginX()
    {
        return horizOriginX;
    }

    public int getHorizOriginY()
    {
        return horizOriginY;
    }

    public int getHorizAdvX()
    {
        return horizAdvX;
    }

    public int getVertOriginX()
    {
        if (vertOriginX != -1)
        {
            return vertOriginX;
        }
        vertOriginX = getHorizAdvX() / 2;
        return vertOriginX;
    }

    public int getVertOriginY()
    {
        if (vertOriginY != -1)
        {
            return vertOriginY;
        }
        vertOriginY = fontFace.getAscent();
        return vertOriginY;
    }

    public int getVertAdvY()
    {
        if (vertAdvY != -1)
        {
            return vertAdvY;
        }
        vertAdvY = fontFace.getUnitsPerEm();
        return vertAdvY;
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
        //Fonts can't change
        return false;
        /*
         if (trackManager.getNumTracks() == 0) return false;
        
         //Get current values for parameters
         StyleAttribute sty = new StyleAttribute();
         boolean stateChange = false;
        
         if (getPres(sty.setName("horiz-origin-x")))
         {
         int newVal = sty.getIntValue();
         if (newVal != horizOriginX)
         {
         horizOriginX = newVal;
         stateChange = true;
         }
         }
        
         if (getPres(sty.setName("horiz-origin-y")))
         {
         int newVal = sty.getIntValue();
         if (newVal != horizOriginY)
         {
         horizOriginY = newVal;
         stateChange = true;
         }
         }
        
         if (getPres(sty.setName("horiz-adv-x")))
         {
         int newVal = sty.getIntValue();
         if (newVal != horizAdvX)
         {
         horizAdvX = newVal;
         stateChange = true;
         }
         }
        
         if (getPres(sty.setName("vert-origin-x")))
         {
         int newVal = sty.getIntValue();
         if (newVal != vertOriginX)
         {
         vertOriginX = newVal;
         stateChange = true;
         }
         }
        
         if (getPres(sty.setName("vert-origin-y")))
         {
         int newVal = sty.getIntValue();
         if (newVal != vertOriginY)
         {
         vertOriginY = newVal;
         stateChange = true;
         }
         }
        
         if (getPres(sty.setName("vert-adv-y")))
         {
         int newVal = sty.getIntValue();
         if (newVal != vertAdvY)
         {
         vertAdvY = newVal;
         stateChange = true;
         }
         }
        
         return shapeChange;
         */
    }
}
