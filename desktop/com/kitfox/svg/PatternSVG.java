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

import com.kitfox.svg.pattern.PatternPaint;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class PatternSVG extends FillElement
{
    public static final String TAG_NAME = "pattern";
    
    public static final int GU_OBJECT_BOUNDING_BOX = 0;
    public static final int GU_USER_SPACE_ON_USE = 1;
    int gradientUnits = GU_OBJECT_BOUNDING_BOX;
    float x;
    float y;
    float width;
    float height;
    AffineTransform patternXform = new AffineTransform();
    Rectangle2D.Float viewBox;
    Paint texPaint;

    /**
     * Creates a new instance of Gradient
     */
    public PatternSVG()
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
    }

    protected void build() throws SVGException
    {
        super.build();

        StyleAttribute sty = new StyleAttribute();

        //Load style string
        String href = null;
        if (getPres(sty.setName("xlink:href")))
        {
            href = sty.getStringValue();
        }
        //String href = attrs.getValue("xlink:href");
        //If we have a link to another pattern, initialize ourselves with it's values
        if (href != null)
        {
//System.err.println("Gradient.loaderStartElement() href '" + href + "'");
            try
            {
                URI src = getXMLBase().resolve(href);
                PatternSVG patSrc = (PatternSVG) diagram.getUniverse().getElement(src);

                gradientUnits = patSrc.gradientUnits;
                x = patSrc.x;
                y = patSrc.y;
                width = patSrc.width;
                height = patSrc.height;
                viewBox = patSrc.viewBox;
                patternXform.setTransform(patSrc.patternXform);
                children.addAll(patSrc.children);
            } catch (Exception e)
            {
                Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Could not parse xlink:href", e);
            }
        }

        String gradientUnits = "";
        if (getPres(sty.setName("gradientUnits")))
        {
            gradientUnits = sty.getStringValue().toLowerCase();
        }
        if (gradientUnits.equals("userspaceonuse"))
        {
            this.gradientUnits = GU_USER_SPACE_ON_USE;
        } else
        {
            this.gradientUnits = GU_OBJECT_BOUNDING_BOX;
        }

        String patternTransform = "";
        if (getPres(sty.setName("patternTransform")))
        {
            patternTransform = sty.getStringValue();
        }
        patternXform = parseTransform(patternTransform);


        if (getPres(sty.setName("x")))
        {
            x = sty.getFloatValueWithUnits();
        }

        if (getPres(sty.setName("y")))
        {
            y = sty.getFloatValueWithUnits();
        }

        if (getPres(sty.setName("width")))
        {
            width = sty.getFloatValueWithUnits();
        }

        if (getPres(sty.setName("height")))
        {
            height = sty.getFloatValueWithUnits();
        }

        if (getPres(sty.setName("viewBox")))
        {
            float[] dim = sty.getFloatList();
            viewBox = new Rectangle2D.Float(dim[0], dim[1], dim[2], dim[3]);
        }

        preparePattern();
    }

    /*
     public void loaderEndElement(SVGLoaderHelper helper)
     {
     build();
     }
     */
    protected void preparePattern() throws SVGException
    {
        //For now, treat all fills as UserSpaceOnUse.  Otherwise, we'll need
        // a different paint for every object.
        int tileWidth = (int) width;
        int tileHeight = (int) height;

        float stretchX = 1f, stretchY = 1f;
        if (!patternXform.isIdentity())
        {
            //Scale our source tile so that we can have nice sampling from it.
            float xlateX = (float) patternXform.getTranslateX();
            float xlateY = (float) patternXform.getTranslateY();

            Point2D.Float pt = new Point2D.Float(), pt2 = new Point2D.Float();

            pt.setLocation(width, 0);
            patternXform.transform(pt, pt2);
            pt2.x -= xlateX;
            pt2.y -= xlateY;
            stretchX = (float) Math.sqrt(pt2.x * pt2.x + pt2.y * pt2.y) * 1.5f / width;

            pt.setLocation(height, 0);
            patternXform.transform(pt, pt2);
            pt2.x -= xlateX;
            pt2.y -= xlateY;
            stretchY = (float) Math.sqrt(pt2.x * pt2.x + pt2.y * pt2.y) * 1.5f / height;

            tileWidth *= stretchX;
            tileHeight *= stretchY;
        }

        if (tileWidth == 0 || tileHeight == 0)
        {
            //Use defaults if tile has degenerate size
            return;
        }

        BufferedImage buf = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buf.createGraphics();
        g.setClip(0, 0, tileWidth, tileHeight);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Iterator it = children.iterator(); it.hasNext();)
        {
            SVGElement ele = (SVGElement) it.next();
            if (ele instanceof RenderableElement)
            {
                AffineTransform xform = new AffineTransform();

                if (viewBox == null)
                {
                    xform.translate(-x, -y);
                } else
                {
                    xform.scale(tileWidth / viewBox.width, tileHeight / viewBox.height);
                    xform.translate(-viewBox.x, -viewBox.y);
                }

                g.setTransform(xform);
                ((RenderableElement) ele).render(g);
            }
        }

        g.dispose();

//try {
//javax.imageio.ImageIO.write(buf, "png", new java.io.File("c:\\tmp\\texPaint.png"));
//} catch (Exception e ) {}

        if (patternXform.isIdentity())
        {
            texPaint = new TexturePaint(buf, new Rectangle2D.Float(x, y, width, height));
        } else
        {
            patternXform.scale(1 / stretchX, 1 / stretchY);
            texPaint = new PatternPaint(buf, patternXform);
        }
    }

    public Paint getPaint(Rectangle2D bounds, AffineTransform xform)
    {
        return texPaint;
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
        //Patterns don't change state
        return false;
    }
}
