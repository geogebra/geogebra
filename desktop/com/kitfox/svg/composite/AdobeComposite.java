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
 * Created on April 1, 2004, 6:40 AM
 */

package com.kitfox.svg.composite;

import com.kitfox.svg.SVGConst;
import java.awt.*;
import java.awt.image.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class AdobeComposite implements Composite
{
    public static final int CT_NORMAL = 0;
    public static final int CT_MULTIPLY = 1;
    public static final int CT_LAST = 2;

    final int compositeType;
    final float extraAlpha;

    /** Creates a new instance of AdobeComposite */
    public AdobeComposite(int compositeType, float extraAlpha)
    {
        this.compositeType = compositeType;
        this.extraAlpha = extraAlpha;

        if (compositeType < 0 || compositeType >= CT_LAST)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, "Invalid composite type");
        }

        if (extraAlpha < 0f || extraAlpha > 1f)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, "Invalid alpha");
        }
    }

    public int getCompositeType() { return compositeType; }

    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints)
    {
        return new AdobeCompositeContext(compositeType, extraAlpha);
    }

}
