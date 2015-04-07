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
 * Created on May 10, 2005, 5:56 AM
 */

package com.kitfox.svg.pathcmd;

import java.awt.geom.*;

/**
 *
 * @author kitfox
 */
public class PathUtil
{
    
    /** Creates a new instance of PathUtil */
    public PathUtil()
    {
    }
    
    /**
     * Converts a GeneralPath into an SVG representation
     */
    public static String buildPathString(GeneralPath path)
    {
        float[] coords = new float[6];
        
        StringBuffer sb = new StringBuffer();
        
        for (PathIterator pathIt = path.getPathIterator(new AffineTransform()); !pathIt.isDone(); pathIt.next())
        {
            int segId = pathIt.currentSegment(coords);
            
            switch (segId)
            {
                case PathIterator.SEG_CLOSE:
                {
                    sb.append(" Z");
                    break;
                }
                case PathIterator.SEG_CUBICTO:
                {
                    sb.append(" C " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " " + coords[5]);
                    break;
                }
                case PathIterator.SEG_LINETO:
                {
                    sb.append(" L " + coords[0] + " " + coords[1]);
                    break;
                }
                case PathIterator.SEG_MOVETO:
                {
                    sb.append(" M " + coords[0] + " " + coords[1]);
                    break;
                }
                case PathIterator.SEG_QUADTO:
                {
                    sb.append(" Q " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3]);
                    break;
                }
            }
        }
        
        return sb.toString();
    }
}
