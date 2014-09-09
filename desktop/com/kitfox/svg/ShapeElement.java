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
 * Created on January 26, 2004, 5:21 PM
 */

package com.kitfox.svg;

import com.kitfox.svg.Marker.MarkerLayout;
import com.kitfox.svg.Marker.MarkerPos;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;



/**
 * Parent of shape objects
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
abstract public class ShapeElement extends RenderableElement 
{

    /**
     * This is necessary to get text elements to render the stroke the correct
     * width.  It is an alternative to producing new font glyph sets at different
     * sizes.
     */
    protected float strokeWidthScalar = 1f;

    /** Creates a new instance of ShapeElement */
    public ShapeElement() {
    }

    abstract public void render(java.awt.Graphics2D g) throws SVGException;

    /*
    protected void setStrokeWidthScalar(float strokeWidthScalar)
    {
        this.strokeWidthScalar = strokeWidthScalar;
    }
     */

    void pick(Point2D point, boolean boundingBox, List retVec) throws SVGException
    {
//        StyleAttribute styleAttrib = new StyleAttribute();
//        if (getStyle(styleAttrib.setName("fill")) && getShape().contains(point))
        if ((boundingBox ? getBoundingBox() : getShape()).contains(point))
        {
            retVec.add(getPath(null));
        }
    }

    void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List retVec) throws SVGException
    {
        StyleAttribute styleAttrib = new StyleAttribute();
//        if (getStyle(styleAttrib.setName("fill")) && getShape().contains(point))
        if (ltw.createTransformedShape((boundingBox ? getBoundingBox() : getShape())).intersects(pickArea))
        {
            retVec.add(getPath(null));
        }
    }

    private Paint handleCurrentColor(StyleAttribute styleAttrib) throws SVGException
    {
        if (styleAttrib.getStringValue().equals("currentColor"))
        {
            StyleAttribute currentColorAttrib = new StyleAttribute();
            if (getStyle(currentColorAttrib.setName("color")))
            {
                if (!currentColorAttrib.getStringValue().equals("none"))
                {
                    return currentColorAttrib.getColorValue();
                }
            }
            return null;
        }
        else
        {
            return styleAttrib.getColorValue();
        }
    }

    protected void renderShape(Graphics2D g, Shape shape) throws SVGException
    {
//g.setColor(Color.green);

        StyleAttribute styleAttrib = new StyleAttribute();
        
        //Don't process if not visible
        if (getStyle(styleAttrib.setName("visibility")))
        {
            if (!styleAttrib.getStringValue().equals("visible")) return;
        }

        if (getStyle(styleAttrib.setName("display")))
        {
            if (styleAttrib.getStringValue().equals("none")) return;
        }

        //None, solid color, gradient, pattern
        Paint fillPaint = Color.black;  //Default to black.  Must be explicitly set to none for no fill.
        if (getStyle(styleAttrib.setName("fill")))
        {
            if (styleAttrib.getStringValue().equals("none")) fillPaint = null;
            else
            {
                fillPaint = handleCurrentColor(styleAttrib);
                if (fillPaint == null)
                {
                    URI uri = styleAttrib.getURIValue(getXMLBase());
                    if (uri != null)
                    {
                        Rectangle2D bounds = shape.getBounds2D();
                        AffineTransform xform = g.getTransform();

                        SVGElement ele = diagram.getUniverse().getElement(uri);
                        if (ele != null)
                        {
                            fillPaint = ((FillElement)ele).getPaint(bounds, xform);
                        }
                    }
                }
            }
        }

        //Default opacity
        float opacity = 1f;
        if (getStyle(styleAttrib.setName("opacity")))
        {
            opacity = styleAttrib.getRatioValue();
        }
        
        float fillOpacity = opacity;
        if (getStyle(styleAttrib.setName("fill-opacity")))
        {
            fillOpacity *= styleAttrib.getRatioValue();
        }


        Paint strokePaint = null;  //Default is to stroke with none
        if (getStyle(styleAttrib.setName("stroke")))
        {
            if (styleAttrib.getStringValue().equals("none")) strokePaint = null;
            else
            {
                strokePaint = handleCurrentColor(styleAttrib);
                if (strokePaint == null)
                {
                    URI uri = styleAttrib.getURIValue(getXMLBase());
                    if (uri != null)
                    {
                        Rectangle2D bounds = shape.getBounds2D();
                        AffineTransform xform = g.getTransform();

                        SVGElement ele = diagram.getUniverse().getElement(uri);
                        if (ele != null)
                        {
                            strokePaint = ((FillElement)ele).getPaint(bounds, xform);
                        }
                    }
                }
            }
        }

        float[] strokeDashArray = null;
        if (getStyle(styleAttrib.setName("stroke-dasharray")))
        {
            strokeDashArray = styleAttrib.getFloatList();
            if (strokeDashArray.length == 0) strokeDashArray = null;
        }

        float strokeDashOffset = 0f;
        if (getStyle(styleAttrib.setName("stroke-dashoffset")))
        {
            strokeDashOffset = styleAttrib.getFloatValueWithUnits();
        }

        int strokeLinecap = BasicStroke.CAP_BUTT;
        if (getStyle(styleAttrib.setName("stroke-linecap")))
        {
            String val = styleAttrib.getStringValue();
            if (val.equals("round"))
            {
                strokeLinecap = BasicStroke.CAP_ROUND;
            }
            else if (val.equals("square"))
            {
                strokeLinecap = BasicStroke.CAP_SQUARE;
            }
        }

        int strokeLinejoin = BasicStroke.JOIN_MITER;
        if (getStyle(styleAttrib.setName("stroke-linejoin")))
        {
            String val = styleAttrib.getStringValue();
            if (val.equals("round"))
            {
                strokeLinejoin = BasicStroke.JOIN_ROUND;
            }
            else if (val.equals("bevel"))
            {
                strokeLinejoin = BasicStroke.JOIN_BEVEL;
            }
        }

        float strokeMiterLimit = 4f;
        if (getStyle(styleAttrib.setName("stroke-miterlimit")))
        {
            strokeMiterLimit = Math.max(styleAttrib.getFloatValueWithUnits(), 1);
        }

        float strokeOpacity = opacity;
        if (getStyle(styleAttrib.setName("stroke-opacity")))
        {
            strokeOpacity *= styleAttrib.getRatioValue();
        }

        float strokeWidth = 1f;
        if (getStyle(styleAttrib.setName("stroke-width")))
        {
            strokeWidth = styleAttrib.getFloatValueWithUnits();
        }
//        if (strokeWidthScalar != 1f)
//        {
            strokeWidth *= strokeWidthScalar;
//        }

        Marker markerStart = null;
        if (getStyle(styleAttrib.setName("marker-start")))
        {
            if (!styleAttrib.getStringValue().equals("none"))
            {
                URI uri = styleAttrib.getURIValue(getXMLBase());
                markerStart = (Marker)diagram.getUniverse().getElement(uri);
            }
        }

        Marker markerMid = null;
        if (getStyle(styleAttrib.setName("marker-mid")))
        {
            if (!styleAttrib.getStringValue().equals("none"))
            {
                URI uri = styleAttrib.getURIValue(getXMLBase());
                markerMid = (Marker)diagram.getUniverse().getElement(uri);
            }
        }

        Marker markerEnd = null;
        if (getStyle(styleAttrib.setName("marker-end")))
        {
            if (!styleAttrib.getStringValue().equals("none"))
            {
                URI uri = styleAttrib.getURIValue(getXMLBase());
                markerEnd = (Marker)diagram.getUniverse().getElement(uri);
            }
        }


        //Draw the shape
        if (fillPaint != null && fillOpacity != 0f)
        {
            if (fillOpacity <= 0)
            {
                //Do nothing
            }
            else if (fillOpacity < 1f)
            {
                Composite cachedComposite = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fillOpacity));

                g.setPaint(fillPaint);
                g.fill(shape);
            
                g.setComposite(cachedComposite);
            }
            else
            {
                g.setPaint(fillPaint);
                g.fill(shape);
            }
        }


        if (strokePaint != null && strokeOpacity != 0f)
        {
            BasicStroke stroke;
            if (strokeDashArray == null)
            {
                stroke = new BasicStroke(strokeWidth, strokeLinecap, strokeLinejoin, strokeMiterLimit);
            }
            else
            {
                stroke = new BasicStroke(strokeWidth, strokeLinecap, strokeLinejoin, strokeMiterLimit, strokeDashArray, strokeDashOffset);
            }

            Shape strokeShape;
            AffineTransform cacheXform = g.getTransform();
            if (vectorEffect == VECTOR_EFFECT_NON_SCALING_STROKE)
            {
                strokeShape = cacheXform.createTransformedShape(shape);
                strokeShape = stroke.createStrokedShape(strokeShape);
            }
            else
            {
                strokeShape = stroke.createStrokedShape(shape);
            }

            if (strokeOpacity <= 0)
            {
                //Do nothing
            }
            else
            {
                Composite cachedComposite = g.getComposite();

                if (strokeOpacity < 1f)
                {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, strokeOpacity));
                }

                if (vectorEffect == VECTOR_EFFECT_NON_SCALING_STROKE)
                {
                    //Set to identity
                    g.setTransform(new AffineTransform());
                }

                g.setPaint(strokePaint);
                g.fill(strokeShape);

                if (vectorEffect == VECTOR_EFFECT_NON_SCALING_STROKE)
                {
                    //Set to identity
                    g.setTransform(cacheXform);
                }

                if (strokeOpacity < 1f)
                {
                    g.setComposite(cachedComposite);
                }
            }
        }

        if (markerStart != null || markerMid != null || markerEnd != null)
        {
            MarkerLayout layout = new MarkerLayout();
            layout.layout(shape);
            
            ArrayList list = layout.getMarkerList();
            for (int i = 0; i < list.size(); ++i)
            {
                MarkerPos pos = (MarkerPos)list.get(i);

                switch (pos.type)
                {
                    case Marker.MARKER_START:
                        if (markerStart != null)
                        {
                            markerStart.render(g, pos, strokeWidth);
                        }
                        break;
                    case Marker.MARKER_MID:
                        if (markerMid != null)
                        {
                            markerMid.render(g, pos, strokeWidth);
                        }
                        break;
                    case Marker.MARKER_END:
                        if (markerEnd != null)
                        {
                            markerEnd.render(g, pos, strokeWidth);
                        }
                        break;
                }
            }
        }
    }
    
    abstract public Shape getShape();

    protected Rectangle2D includeStrokeInBounds(Rectangle2D rect) throws SVGException
    {
        StyleAttribute styleAttrib = new StyleAttribute();
        if (!getStyle(styleAttrib.setName("stroke"))) return rect;

        double strokeWidth = 1;
        if (getStyle(styleAttrib.setName("stroke-width"))) strokeWidth = styleAttrib.getDoubleValue();

        rect.setRect(
            rect.getX() - strokeWidth / 2,
            rect.getY() - strokeWidth / 2,
            rect.getWidth() + strokeWidth,
            rect.getHeight() + strokeWidth);

        return rect;
    }

}
