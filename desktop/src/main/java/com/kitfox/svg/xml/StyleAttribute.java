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
 * Created on January 27, 2004, 2:53 PM
 */

package com.kitfox.svg.xml;

import com.kitfox.svg.SVGConst;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class StyleAttribute implements Serializable
{
    public static final long serialVersionUID = 0;

    static final Pattern patternUrl = Pattern.compile("\\s*url\\((.*)\\)\\s*");
    static final Matcher matchFpNumUnits = Pattern.compile("\\s*([-+]?((\\d*\\.\\d+)|(\\d+))([-+]?[eE]\\d+)?)\\s*(px|cm|mm|in|pc|pt|em|ex)\\s*").matcher("");
    
    String name;
    String stringValue;

    boolean colorCompatable = false;
    boolean urlCompatable = false;

    /** Creates a new instance of StyleAttribute */
    public StyleAttribute()
    {
        this(null, null);
    }
    
    public StyleAttribute(String name) 
    {
        this.name = name;
        stringValue = null;
    }

    public StyleAttribute(String name, String stringValue) 
    {
        this.name = name;
        this.stringValue = stringValue;
    }

    public String getName() {
        return name;
    }
    
    public StyleAttribute setName(String name)
    {
        this.name = name;
        return this;
    }
    
    public String getStringValue()
    {
        return stringValue; 
    }

    public String[] getStringList() 
    { 
        return XMLParseUtil.parseStringList(stringValue);
    }

    public void setStringValue(String value)
    {
        stringValue = value;
    }

    public boolean getBooleanValue() {
        return stringValue.toLowerCase().equals("true");
    }

    public int getIntValue() {
        return XMLParseUtil.findInt(stringValue);
    }

    public int[] getIntList() {
        return XMLParseUtil.parseIntList(stringValue);
    }

    public double getDoubleValue() {
        return XMLParseUtil.findDouble(stringValue);
    }

    public double[] getDoubleList() {
        return XMLParseUtil.parseDoubleList(stringValue);
    }

    public float getFloatValue() {
        return XMLParseUtil.findFloat(stringValue);
    }

    public float[] getFloatList() {
        return XMLParseUtil.parseFloatList(stringValue);
    }

    public float getRatioValue() {
        return (float)XMLParseUtil.parseRatio(stringValue);
//        try { return Float.parseFloat(stringValue); }
//        catch (Exception e) {}
//        return 0f;
    }

    public String getUnits() {
        matchFpNumUnits.reset(stringValue);
        if (!matchFpNumUnits.matches()) return null;
        return matchFpNumUnits.group(6);
    }

    public NumberWithUnits getNumberWithUnits() {
        return XMLParseUtil.parseNumberWithUnits(stringValue);
    }

    public float getFloatValueWithUnits()
    {
        NumberWithUnits number = getNumberWithUnits();
        return convertUnitsToPixels(number.getUnits(), number.getValue());
    }
    
    static public float convertUnitsToPixels(int unitType, float value)
    {
        if (unitType == NumberWithUnits.UT_UNITLESS || unitType == NumberWithUnits.UT_PERCENT)
        {
            return value;
        }
        
        float pixPerInch;
        try 
        {
            pixPerInch = (float)Toolkit.getDefaultToolkit().getScreenResolution();
        }
        catch (HeadlessException ex)
        {
            //default to 72 dpi
            pixPerInch = 72;
        }
        final float inchesPerCm = .3936f;

        switch (unitType)
        {
            case NumberWithUnits.UT_IN:
                return value * pixPerInch;
            case NumberWithUnits.UT_CM:
                return value * inchesPerCm * pixPerInch;
            case NumberWithUnits.UT_MM:
                return value * .1f * inchesPerCm * pixPerInch;
            case NumberWithUnits.UT_PT:
                return value * (1f / 72f) * pixPerInch;
            case NumberWithUnits.UT_PC:
                return value *  (1f / 6f) * pixPerInch;
        }

        return value;
    }

    public Color getColorValue()
    {
        return ColorTable.parseColor(stringValue);
    }

    public String parseURLFn()
    {
        Matcher matchUrl = patternUrl.matcher(stringValue);
        if (!matchUrl.matches()) 
        {
            return null;
        }
        return matchUrl.group(1);
    }

    public URL getURLValue(URL docRoot)
    {
        String fragment = parseURLFn();
        if (fragment == null) return null;
        try {
            return new URL(docRoot, fragment);
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
            return null;
        }
    }

    public URL getURLValue(URI docRoot)
    {
        String fragment = parseURLFn();
        if (fragment == null) return null;
        try {
            URI ref = docRoot.resolve(fragment);
            return ref.toURL();
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
            return null;
        }
    }

    public URI getURIValue()
    {
        return getURIValue(null);
    }
    
    /**
     * Parse this sytle attribute as a URL and return it in URI form resolved
     * against the passed base.
     *
     * @param base - URI to resolve against.  If null, will return value without
     * attempting to resolve it.
     */
    public URI getURIValue(URI base)
    {
        try {
            String fragment = parseURLFn();
            if (fragment == null) fragment = stringValue.replaceAll("\\s+", "");
            if (fragment == null) return null;
            
            //======================
            //This gets around a bug in the 1.5.0 JDK
            if (Pattern.matches("[a-zA-Z]:!\\\\.*", fragment))
            {
                File file = new File(fragment);
                return file.toURI();
            }
            //======================

            //[scheme:]scheme-specific-part[#fragment]
            
            URI uriFrag = new URI(fragment);
            if (uriFrag.isAbsolute())
            {
                //Has scheme
                return uriFrag;
            }
        
            if (base == null) return uriFrag;
        
            URI relBase = new URI(null, base.getSchemeSpecificPart(), null);
            URI relUri;
            if (relBase.isOpaque())
            {
                relUri = new URI(null, base.getSchemeSpecificPart(), uriFrag.getFragment());
            }
            else
            {
                relUri = relBase.resolve(uriFrag);
            }
            return new URI(base.getScheme() + ":" + relUri);
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
            return null;
        }
    }
    
    public static void main(String[] args)
    {
        try
        {
            URI uri = new URI("jar:http://www.kitfox.com/jackal/jackal.jar!/res/doc/about.svg");
            uri = uri.resolve("#myFragment");
            
            System.err.println(uri.toString());
            
            uri = new URI("http://www.kitfox.com/jackal/jackal.html");
            uri = uri.resolve("#myFragment");
            
            System.err.println(uri.toString());
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
        }
    }
}
