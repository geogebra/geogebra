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

import com.kitfox.svg.pathcmd.BuildHistory;
import com.kitfox.svg.pathcmd.PathCommand;
import com.kitfox.svg.xml.StyleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * Implements an embedded font.
 *
 * SVG specification: http://www.w3.org/TR/SVG/fonts.html
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class MissingGlyph extends ShapeElement
{
    public static final String TAG_NAME = "missingglyph";
    
    //We may define a path
    Shape path = null;
    //Alternately, we may have child graphical elements
    int horizAdvX = -1;  //Inherits font's value if not set
    int vertOriginX = -1;  //Inherits font's value if not set
    int vertOriginY = -1;  //Inherits font's value if not set
    int vertAdvY = -1;  //Inherits font's value if not set

    /**
     * Creates a new instance of Font
     */
    public MissingGlyph()
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

        String commandList = "";
        if (getPres(sty.setName("d")))
        {
            commandList = sty.getStringValue();
        }


        //If glyph path was specified, calculate it
        if (commandList != null)
        {
            String fillRule = getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";

            PathCommand[] commands = parsePathList(commandList);

            GeneralPath buildPath = new GeneralPath(
                fillRule.equals("evenodd") ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO,
                commands.length);

            BuildHistory hist = new BuildHistory();

            for (int i = 0; i < commands.length; i++)
            {
                PathCommand cmd = commands[i];
                cmd.appendPath(buildPath, hist);
            }

            //Reflect glyph path to put it in user coordinate system
            AffineTransform at = new AffineTransform();
            at.scale(1, -1);
            path = at.createTransformedShape(buildPath);
        }


        //Read glyph spacing info
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

    public Shape getPath()
    {
        return path;
    }

    public void render(Graphics2D g) throws SVGException
    {
        //Do not push or pop stack

        if (path != null)
        {
            renderShape(g, path);
        }

        Iterator it = children.iterator();
        while (it.hasNext())
        {
            SVGElement ele = (SVGElement) it.next();
            if (ele instanceof RenderableElement)
            {
                ((RenderableElement) ele).render(g);
            }
        }

        //Do not push or pop stack
    }

    public int getHorizAdvX()
    {
        if (horizAdvX == -1)
        {
            horizAdvX = ((Font) parent).getHorizAdvX();
        }
        return horizAdvX;
    }

    public int getVertOriginX()
    {
        if (vertOriginX == -1)
        {
            vertOriginX = getHorizAdvX() / 2;
        }
        return vertOriginX;
    }

    public int getVertOriginY()
    {
        if (vertOriginY == -1)
        {
            vertOriginY = ((Font) parent).getFontFace().getAscent();
        }
        return vertOriginY;
    }

    public int getVertAdvY()
    {
        if (vertAdvY == -1)
        {
            vertAdvY = ((Font) parent).getFontFace().getUnitsPerEm();
        }
        return vertAdvY;

    }

    public Shape getShape()
    {
        if (path != null)
        {
            return shapeToParent(path);
        }
        return null;
    }

    public Rectangle2D getBoundingBox() throws SVGException
    {
        if (path != null)
        {
            return boundsToParent(includeStrokeInBounds(path.getBounds2D()));
        }
        return null;
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
    }
}
