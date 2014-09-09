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
 * Created on February 18, 2004, 5:33 PM
 */

package com.kitfox.svg;

import com.kitfox.svg.xml.NumberWithUnits;
import com.kitfox.svg.xml.StyleAttribute;
import com.kitfox.svg.xml.StyleSheet;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * The root element of an SVG tree.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGRoot extends Group
{
    public static final String TAG_NAME = "svg";

    NumberWithUnits x;
    NumberWithUnits y;
    NumberWithUnits width;
    NumberWithUnits height;

    Rectangle2D.Float viewBox = null;

    public static final int PA_X_NONE = 0;
    public static final int PA_X_MIN = 1;
    public static final int PA_X_MID = 2;
    public static final int PA_X_MAX = 3;

    public static final int PA_Y_NONE = 0;
    public static final int PA_Y_MIN = 1;
    public static final int PA_Y_MID = 2;
    public static final int PA_Y_MAX = 3;

    public static final int PS_MEET = 0;
    public static final int PS_SLICE = 1;

    int parSpecifier = PS_MEET;
    int parAlignX = PA_X_MID;
    int parAlignY = PA_Y_MID;

    final AffineTransform viewXform = new AffineTransform();
    final Rectangle2D.Float clipRect = new Rectangle2D.Float();

    private StyleSheet styleSheet;
    
    /** Creates a new instance of SVGRoot */
    public SVGRoot()
    {
    }

    public String getTagName()
    {
        return TAG_NAME;
    }
    
    public void build() throws SVGException
    {
        super.build();
        
        StyleAttribute sty = new StyleAttribute();
        
        if (getPres(sty.setName("x")))
        {
            x = sty.getNumberWithUnits();
        }
        
        if (getPres(sty.setName("y")))
        {
            y = sty.getNumberWithUnits();
        }
        
        if (getPres(sty.setName("width")))
        {
            width = sty.getNumberWithUnits();
        }
        
        if (getPres(sty.setName("height")))
        {
            height = sty.getNumberWithUnits();
        }
        
        if (getPres(sty.setName("viewBox"))) 
        {
            float[] coords = sty.getFloatList();
            viewBox = new Rectangle2D.Float(coords[0], coords[1], coords[2], coords[3]);
        }
        
        if (getPres(sty.setName("preserveAspectRatio")))
        {
            String preserve = sty.getStringValue();
            
            if (contains(preserve, "none")) { parAlignX = PA_X_NONE; parAlignY = PA_Y_NONE; }
            else if (contains(preserve, "xMinYMin")) { parAlignX = PA_X_MIN; parAlignY = PA_Y_MIN; }
            else if (contains(preserve, "xMidYMin")) { parAlignX = PA_X_MID; parAlignY = PA_Y_MIN; }
            else if (contains(preserve, "xMaxYMin")) { parAlignX = PA_X_MAX; parAlignY = PA_Y_MIN; }
            else if (contains(preserve, "xMinYMid")) { parAlignX = PA_X_MIN; parAlignY = PA_Y_MID; }
            else if (contains(preserve, "xMidYMid")) { parAlignX = PA_X_MID; parAlignY = PA_Y_MID; }
            else if (contains(preserve, "xMaxYMid")) { parAlignX = PA_X_MAX; parAlignY = PA_Y_MID; }
            else if (contains(preserve, "xMinYMax")) { parAlignX = PA_X_MIN; parAlignY = PA_Y_MAX; }
            else if (contains(preserve, "xMidYMax")) { parAlignX = PA_X_MID; parAlignY = PA_Y_MAX; }
            else if (contains(preserve, "xMaxYMax")) { parAlignX = PA_X_MAX; parAlignY = PA_Y_MAX; }

            if (contains(preserve, "meet"))
            {
                parSpecifier = PS_MEET;
            }
            else if (contains(preserve, "slice"))
            {
                parSpecifier = PS_SLICE;
            }
        }
        
        prepareViewport();
    }
    
    private boolean contains(String text, String find) 
    {
        return (text.indexOf(find) != -1);
    }

    public SVGRoot getRoot()
    {
        return this;
    }
    
    protected void prepareViewport()
    {
        Rectangle deviceViewport = diagram.getDeviceViewport();
        
        Rectangle2D defaultBounds;
        try
        {
            defaultBounds = getBoundingBox();
        }
        catch (SVGException ex)
        {
            defaultBounds= new Rectangle2D.Float();
        }
        
        //Determine destination rectangle
        float xx, yy, ww, hh;
        if (width != null)
        {
            xx = (x == null) ? 0 : StyleAttribute.convertUnitsToPixels(x.getUnits(), x.getValue());
            if (width.getUnits() == NumberWithUnits.UT_PERCENT)
            {
                ww = width.getValue() * deviceViewport.width;
            }
            else
            {
                ww = StyleAttribute.convertUnitsToPixels(width.getUnits(), width.getValue());
            }
        }
        else if (viewBox != null)
        {
            xx = (float)viewBox.x;
            ww = (float)viewBox.width;
            width = new NumberWithUnits(ww, NumberWithUnits.UT_PX);
            x = new NumberWithUnits(xx, NumberWithUnits.UT_PX);
        }
        else
        {
            //Estimate size from scene bounding box
            xx = (float)defaultBounds.getX();
            ww = (float)defaultBounds.getWidth();
            width = new NumberWithUnits(ww, NumberWithUnits.UT_PX);
            x = new NumberWithUnits(xx, NumberWithUnits.UT_PX);
        }
        
        if (height != null)
        {
            yy = (y == null) ? 0 : StyleAttribute.convertUnitsToPixels(y.getUnits(), y.getValue());
            if (height.getUnits() == NumberWithUnits.UT_PERCENT)
            {
                hh = height.getValue() * deviceViewport.height;
            }
            else
            {
                hh = StyleAttribute.convertUnitsToPixels(height.getUnits(), height.getValue());
            }
        }
        else if (viewBox != null)
        {
            yy = (float)viewBox.y;
            hh = (float)viewBox.height;
            height = new NumberWithUnits(hh, NumberWithUnits.UT_PX);
            y = new NumberWithUnits(yy, NumberWithUnits.UT_PX);
        }
        else
        {
            //Estimate size from scene bounding box
            yy = (float)defaultBounds.getY();
            hh = (float)defaultBounds.getHeight();
            height = new NumberWithUnits(hh, NumberWithUnits.UT_PX);
            y = new NumberWithUnits(yy, NumberWithUnits.UT_PX);
        }

        clipRect.setRect(xx, yy, ww, hh);

//        if (viewBox == null)
//        {
//            viewXform.setToIdentity();
//        }
//        else
//        {
//            //If viewport window is set, we are drawing to entire viewport
//            clipRect.setRect(deviceViewport);
//            
//            viewXform.setToIdentity();
//            viewXform.setToTranslation(deviceViewport.x, deviceViewport.y);
//            viewXform.scale(deviceViewport.width, deviceViewport.height);
//            viewXform.scale(1 / viewBox.width, 1 / viewBox.height);
//            viewXform.translate(-viewBox.x, -viewBox.y);
//        }
    }

    public void renderToViewport(Graphics2D g) throws SVGException
    {
        prepareViewport();

        if (viewBox == null)
        {
            viewXform.setToIdentity();
        }
        else
        {
            Rectangle deviceViewport = g.getClipBounds();
            //If viewport window is set, we are drawing to entire viewport
            clipRect.setRect(deviceViewport);
            
            viewXform.setToIdentity();
            viewXform.setToTranslation(deviceViewport.x, deviceViewport.y);
            viewXform.scale(deviceViewport.width, deviceViewport.height);
            viewXform.scale(1 / viewBox.width, 1 / viewBox.height);
            viewXform.translate(-viewBox.x, -viewBox.y);
        }
        
        AffineTransform cachedXform = g.getTransform();
        g.transform(viewXform);
        
        super.render(g);
        
        g.setTransform(cachedXform);
    }

    public void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List retVec) throws SVGException
    {
        if (viewXform != null)
        {
            ltw = new AffineTransform(ltw);
            ltw.concatenate(viewXform);
        }
        
        super.pick(pickArea, ltw, boundingBox, retVec);
    }
    
    public void pick(Point2D point, boolean boundingBox, List retVec) throws SVGException
    {
        Point2D xPoint = new Point2D.Double(point.getX(), point.getY());
        if (viewXform != null)
        {
            try
            {
                viewXform.inverseTransform(point, xPoint);
            } catch (NoninvertibleTransformException ex)
            {
                throw new SVGException(ex);
            }
        }
        
        super.pick(xPoint, boundingBox, retVec);
    }

    public Shape getShape()
    {
        Shape shape = super.getShape();
        return viewXform.createTransformedShape(shape);
    }

    public Rectangle2D getBoundingBox() throws SVGException
    {
        Rectangle2D bbox = super.getBoundingBox();
        return viewXform.createTransformedShape(bbox).getBounds2D();
    }
    
    public float getDeviceWidth()
    {
        return clipRect.width;
    }
    
    public float getDeviceHeight()
    {
        return clipRect.height;
    }
    
    public Rectangle2D getDeviceRect(Rectangle2D rect)
    {
        rect.setRect(clipRect);
        return rect;
    }

    /**
     * Updates all attributes in this diagram associated with a time event.
     * Ie, all attributes with track information.
     * @return - true if this node has changed state as a result of the time
     * update
     */
    public boolean updateTime(double curTime) throws SVGException
    {
        boolean changeState = super.updateTime(curTime);
        
        StyleAttribute sty = new StyleAttribute();
        boolean shapeChange = false;
        
        if (getPres(sty.setName("x")))
        {
            NumberWithUnits newVal = sty.getNumberWithUnits();
            if (!newVal.equals(x))
            {
                x = newVal;
                shapeChange = true;
            }
        }

        if (getPres(sty.setName("y")))
        {
            NumberWithUnits newVal = sty.getNumberWithUnits();
            if (!newVal.equals(y))
            {
                y = newVal;
                shapeChange = true;
            }
        }

        if (getPres(sty.setName("width")))
        {
            NumberWithUnits newVal = sty.getNumberWithUnits();
            if (!newVal.equals(width))
            {
                width = newVal;
                shapeChange = true;
            }
        }

        if (getPres(sty.setName("height")))
        {
            NumberWithUnits newVal = sty.getNumberWithUnits();
            if (!newVal.equals(height))
            {
                height = newVal;
                shapeChange = true;
            }
        }
        
        if (getPres(sty.setName("viewBox"))) 
        {
            float[] coords = sty.getFloatList();
            Rectangle2D.Float newViewBox = new Rectangle2D.Float(coords[0], coords[1], coords[2], coords[3]);
            if (!newViewBox.equals(viewBox))
            {
                viewBox = newViewBox;
                shapeChange = true;
            }
        }

        if (shapeChange)
        {
            build();
        }

        return changeState || shapeChange;
    }

    /**
     * @return the styleSheet
     */
    public StyleSheet getStyleSheet()
    {
        if (styleSheet == null)
        {
            for (int i = 0; i < getNumChildren(); ++i)
            {
                SVGElement ele = getChild(i);
                if (ele instanceof Style)
                {
                    return ((Style)ele).getStyleSheet();
                }
            }
        }
        
        return styleSheet;
    }

    /**
     * @param styleSheet the styleSheet to set
     */
    public void setStyleSheet(StyleSheet styleSheet)
    {
        this.styleSheet = styleSheet;
    }

}
