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
 * Created on April 1, 2004, 3:37 AM
 */

package com.kitfox.svg.pattern;

import com.kitfox.svg.SVGConst;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class PatternPaintContext implements PaintContext
{
    BufferedImage source;  //Image we're rendering from
    Rectangle deviceBounds;  //int size of rectangle we're rendering to
//    AffineTransform userXform;  //xform from user space to device space
//    AffineTransform distortXform;  //distortion applied to this pattern

    AffineTransform xform;  //distortion applied to this pattern

    int sourceWidth;
    int sourceHeight;

    //Raster we use to build tile
    BufferedImage buf;

    /** Creates a new instance of PatternPaintContext */
    public PatternPaintContext(BufferedImage source, Rectangle deviceBounds, AffineTransform userXform, AffineTransform distortXform)
    {
//System.err.println("Bounds " + deviceBounds);
        this.source = source;
        this.deviceBounds = deviceBounds;
        try {
//            this.distortXform = distortXform.createInverse();
//            this.userXform = userXform.createInverse();

//            xform = userXform.createInverse();
//            xform.concatenate(distortXform.createInverse());
            xform = distortXform.createInverse();
            xform.concatenate(userXform.createInverse());
        }
        catch (Exception e) 
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
        }

        sourceWidth = source.getWidth();
        sourceHeight = source.getHeight();
    }

    public void dispose() {
    }

    public ColorModel getColorModel() {
        return source.getColorModel();
    }

    public Raster getRaster(int x, int y, int w, int h)
    {
//System.err.println("" + x + ", " + y + ", " + w + ", " + h);
        if (buf == null || buf.getWidth() != w || buf.getHeight() != h)
        {
            buf = new BufferedImage(w, h, source.getType());
        }

//        Point2D.Float srcPt = new Point2D.Float(), srcPt2 = new Point2D.Float(), destPt = new Point2D.Float();
        Point2D.Float srcPt = new Point2D.Float(), destPt = new Point2D.Float();
        for (int j = 0; j < h; j++)
        {
            for (int i = 0; i < w; i++)
            {
                destPt.setLocation(i + x, j + y);

                xform.transform(destPt, srcPt);

//                userXform.transform(destPt, srcPt2);
//                distortXform.transform(srcPt2, srcPt);

                int ii = ((int)srcPt.x) % sourceWidth;
                if (ii < 0) ii += sourceWidth;
                int jj = ((int)srcPt.y) % sourceHeight;
                if (jj < 0) jj += sourceHeight;

                buf.setRGB(i, j, source.getRGB(ii, jj));
            }
        }

        return buf.getData();
    }

    public static void main(String[] argv)
    {
        int i = -4;
        System.err.println("Hello " + (i % 4));
    }

}
