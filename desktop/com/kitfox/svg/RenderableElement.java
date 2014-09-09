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
 * Created on January 26, 2004, 9:00 AM
 */
package com.kitfox.svg;

import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URI;
import java.util.List;

/**
 * Maintains bounding box for this element
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
abstract public class RenderableElement extends TransformableElement
{
    AffineTransform cachedXform = null;
    
    Shape cachedClip = null;
    public static final int VECTOR_EFFECT_NONE = 0;
    public static final int VECTOR_EFFECT_NON_SCALING_STROKE = 1;
    int vectorEffect;

    /**
     * Creates a new instance of BoundedElement
     */
    public RenderableElement()
    {
    }

    public RenderableElement(String id, SVGElement parent)
    {
        super(id, parent);
    }

    protected void build() throws SVGException
    {
        super.build();

        StyleAttribute sty = new StyleAttribute();

        if (getPres(sty.setName("vector-effect")))
        {
            if ("non-scaling-stroke".equals(sty.getStringValue()))
            {
                vectorEffect = VECTOR_EFFECT_NON_SCALING_STROKE;
            } else
            {
                vectorEffect = VECTOR_EFFECT_NONE;
            }
        } else
        {
            vectorEffect = VECTOR_EFFECT_NONE;
        }
    }

    abstract public void render(Graphics2D g) throws SVGException;

    abstract void pick(Point2D point, boolean boundingBox, List retVec) throws SVGException;

    abstract void pick(Rectangle2D pickArea, AffineTransform ltw, boolean boundingBox, List retVec) throws SVGException;

    abstract public Rectangle2D getBoundingBox() throws SVGException;
    /*
     public void loaderStartElement(SVGLoaderHelper helper, Attributes attrs, SVGElement parent)
     {
     super.loaderStartElement(helper, attrs, parent);
     }
     */

    /**
     * Pushes transform stack, transforms to local coordinates and sets up
     * clipping mask.
     */
    protected void beginLayer(Graphics2D g) throws SVGException
    {
        if (xform != null)
        {
            cachedXform = g.getTransform();
            g.transform(xform);
        }

        StyleAttribute styleAttrib = new StyleAttribute();

        //Get clipping path
//        StyleAttribute styleAttrib = getStyle("clip-path", false);
        Shape clipPath = null;
        int clipPathUnits = ClipPath.CP_USER_SPACE_ON_USE;
        if (getStyle(styleAttrib.setName("clip-path"), false)
             && !"none".equals(styleAttrib.getStringValue()))
        {
            URI uri = styleAttrib.getURIValue(getXMLBase());
            if (uri != null)
            {
                ClipPath ele = (ClipPath) diagram.getUniverse().getElement(uri);
                clipPath = ele.getClipPathShape();
                clipPathUnits = ele.getClipPathUnits();
            }
        }

        //Return if we're out of clipping range
        if (clipPath != null)
        {
            if (clipPathUnits == ClipPath.CP_OBJECT_BOUNDING_BOX && (this instanceof ShapeElement))
            {
                Rectangle2D rect = ((ShapeElement) this).getBoundingBox();
                AffineTransform at = new AffineTransform();
                at.scale(rect.getWidth(), rect.getHeight());
                clipPath = at.createTransformedShape(clipPath);
            }

            cachedClip = g.getClip();
            if (cachedClip == null)
            {
                g.setClip(clipPath);
            } else
            {
                Area newClip = new Area(cachedClip);
                newClip.intersect(new Area(clipPath));
                g.setClip(newClip);
            }
        }
    }

    /**
     * Restores transform and clipping values to the way they were before this
     * layer was drawn.
     */
    protected void finishLayer(Graphics2D g)
    {
        if (cachedClip != null)
        {
            g.setClip(cachedClip);
        }

        if (cachedXform != null)
        {
            g.setTransform(cachedXform);
        }
    }
}
