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
 * Created on April 1, 2004, 6:41 AM
 */

package com.kitfox.svg.composite;

import java.awt.*;
import java.awt.image.*;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class AdobeCompositeContext implements CompositeContext
{
    final int compositeType;
    final float extraAlpha;

    float[] rgba_src = new float[4];
    float[] rgba_dstIn = new float[4];
    float[] rgba_dstOut = new float[4];

    /** Creates a new instance of AdobeCompositeContext */
    public AdobeCompositeContext(int compositeType, float extraAlpha)
    {
        this.compositeType = compositeType;
        this.extraAlpha = extraAlpha;

        rgba_dstOut[3] = 1f;
    }

    public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
    {
        int width = src.getWidth();
        int height = src.getHeight();

        for (int j = 0; j < height; j++)
        {
            for (int i = 0; i < width; i++)
            {
                src.getPixel(i, j, rgba_src);
                dstIn.getPixel(i, j, rgba_dstIn);

                //Ignore transparent pixels
                if (rgba_src[3] == 0)
                {
//                    dstOut.setPixel(i, j, rgba_dstIn);
                    continue;
                }

                float alpha = rgba_src[3];

                switch (compositeType)
                {
                    default:
                    case AdobeComposite.CT_NORMAL:
                        rgba_dstOut[0] = rgba_src[0] * alpha + rgba_dstIn[0] * (1f - alpha);
                        rgba_dstOut[1] = rgba_src[1] * alpha + rgba_dstIn[1] * (1f - alpha);
                        rgba_dstOut[2] = rgba_src[2] * alpha + rgba_dstIn[2] * (1f - alpha);
                        break;
                    case AdobeComposite.CT_MULTIPLY:
                        rgba_dstOut[0] = rgba_src[0] * rgba_dstIn[0] * alpha + rgba_dstIn[0] * (1f - alpha);
                        rgba_dstOut[1] = rgba_src[1] * rgba_dstIn[1] * alpha + rgba_dstIn[1] * (1f - alpha);
                        rgba_dstOut[2] = rgba_src[2] * rgba_dstIn[2] * alpha + rgba_dstIn[2] * (1f - alpha);
                        break;
                }
            }
        }
    }

    public void dispose() {
    }

}
