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
 * Created on January 26, 2004, 9:18 PM
 */
package com.kitfox.svg.pathcmd;

import java.awt.geom.Point2D;

/**
 * When building a path from command segments, most need to cache information
 * (such as the point finished at) for future commands. This structure allows
 * that
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class BuildHistory
{

//    Point2D.Float[] history = new Point2D.Float[2];
//    Point2D.Float[] history = {new Point2D.Float(), new Point2D.Float()};
//    Point2D.Float start = new Point2D.Float();
    Point2D.Float startPoint = new Point2D.Float();
    Point2D.Float lastPoint = new Point2D.Float();
    Point2D.Float lastKnot = new Point2D.Float();
    boolean init;
    //int length = 0;

    /**
     * Creates a new instance of BuildHistory
     */
    public BuildHistory()
    {
    }
    
    public void setStartPoint(float x, float y)
    {
        startPoint.setLocation(x, y);
    }
    
    public void setLastPoint(float x, float y)
    {
        lastPoint.setLocation(x, y);
    }
    
    public void setLastKnot(float x, float y)
    {
        lastKnot.setLocation(x, y);
    }
//    public void setPoint(float x, float y)
//    {
//        history[0].setLocation(x, y);
//        length = 1;
//    }
//    public void setStart(float x, float y)
//    {
//        start.setLocation(x, y);
//    }
//    public void setPointAndKnot(float x, float y, float kx, float ky)
//    {
//        history[0].setLocation(x, y);
//        history[1].setLocation(kx, ky);
//        length = 2;
//    }
}
