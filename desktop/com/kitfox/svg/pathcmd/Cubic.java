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
 * Created on January 26, 2004, 8:40 PM
 */

package com.kitfox.svg.pathcmd;

//import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import java.awt.geom.*;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Cubic extends PathCommand {

    public float k1x = 0f;
    public float k1y = 0f;
    public float k2x = 0f;
    public float k2y = 0f;
    public float x = 0f;
    public float y = 0f;

    /** Creates a new instance of MoveTo */
    public Cubic() {
    }

    public String toString()
    {
        return "C " + k1x + " " + k1y
             + " " + k2x + " " + k2y
             + " " + x + " " + y;
    }

    public Cubic(boolean isRelative, float k1x, float k1y, float k2x, float k2y, float x, float y) {
        super(isRelative);
        this.k1x = k1x;
        this.k1y = k1y;
        this.k2x = k2x;
        this.k2y = k2y;
        this.x = x;
        this.y = y;
    }

//    public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
    public void appendPath(GeneralPath path, BuildHistory hist)
    {
        float offx = isRelative ? hist.lastPoint.x : 0f;
        float offy = isRelative ? hist.lastPoint.y : 0f;

        path.curveTo(k1x + offx, k1y + offy,
            k2x + offx, k2y + offy, 
            x + offx, y + offy);
//        hist.setPointAndKnot(x + offx, y + offy, k2x + offx, k2y + offy);
        hist.setLastPoint(x + offx, y + offy);
        hist.setLastKnot(k2x + offx, k2y + offy);
    }

    public int getNumKnotsAdded()
    {
        return 6;
    }
}
