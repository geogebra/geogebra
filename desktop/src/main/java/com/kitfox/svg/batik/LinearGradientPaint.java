/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package com.kitfox.svg.batik;

import com.kitfox.svg.SVGConst;
import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The <code>LinearGradientPaint</code> class provides a way to fill
 * a {@link java.awt.Shape} with a linear color gradient pattern.  The user may
 * specify 2 or more gradient colors, and this paint will provide an
 * interpolation between each color.  The user also specifies start and end
 * points which define where in user space the color gradient should begin 
 * and end.
 * <p>
 * The user must provide an array of floats specifying how to distribute the
 * colors along the gradient.  These values should range from 0.0 to 1.0 and 
 * act like keyframes along the gradient (they mark where the gradient should 
 * be exactly a particular color).
 * <p>
 * For example:
 * <br>
 * <code>
 * <p>
 * Point2D start = new Point2D.Float(0, 0);<br>
 * Point2D end = new Point2D.Float(100,100);<br>
 * float[] dist = {0.0, 0.2, 1.0};<br>
 * Color[] colors = {Color.red, Color.white, Color.blue};<br>
 * LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
 * </code>
 *<p>
 * This code will create a LinearGradientPaint which interpolates between 
 * red and white for the first 20% of the gradient and between white and blue 
 * for the remaining 80%.
 *
 * <p> In the event that the user does not set the first keyframe value equal
 * to 0 and the last keyframe value equal to 1, keyframes will be created at
 * these positions and the first and last colors will be replicated there.
 * So, if a user specifies the following arrays to construct a gradient:<br>
 * {Color.blue, Color.red}, {.3, .7}<br>
 * this will be converted to a gradient with the following keyframes:
 * {Color.blue, Color.blue, Color.red, Color.red}, {0, .3, .7, 1}
 *
 * <p>
 * The user may also select what action the LinearGradientPaint should take
 * when filling color outside the start and end points. If no cycle method is
 * specified, NO_CYCLE will be chosen by default, so the endpoint colors 
 * will be used to fill the remaining area.  
 *
 * <p> The following image demonstrates the options NO_CYCLE and REFLECT.
 *
 * <p>
 * <img src = "cyclic.jpg">
 *
 * <p> The colorSpace parameter allows the user to specify in which colorspace
 *  the interpolation should be performed, default sRGB or linearized RGB.
 *  
 *
 * @author Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id: LinearGradientPaint.java,v 1.2 2004/09/27 09:27:27 kitfox Exp $
 * @see java.awt.Paint
 * @see java.awt.Graphics2D#setPaint
 *
 */

public final class LinearGradientPaint extends MultipleGradientPaint {

    /** Gradient start and end points. */
    private Point2D start, end;   
       
    /**<p>
     * Constructs an <code>LinearGradientPaint</code> with the default 
     * NO_CYCLE repeating method and SRGB colorspace.
     *
     * @param startX the x coordinate of the gradient axis start point 
     * in user space
     *
     * @param startY the y coordinate of the gradient axis start point 
     * in user space
     *
     * @param endX the x coordinate of the gradient axis end point 
     * in user space
     *
     * @param endY the y coordinate of the gradient axis end point 
     * in user space
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors corresponding to each fractional value
     *     
     *
     * @throws IllegalArgumentException if start and end points are the 
     * same points, or if fractions.length != colors.length, or if colors 
     * is less than 2 in size.
     *
     */
    public LinearGradientPaint(float startX, float startY, 
                               float endX, float endY, 
                               float[] fractions, Color[] colors) {

        this(new Point2D.Float(startX, startY),
             new Point2D.Float(endX, endY), 
             fractions, 
             colors,
             NO_CYCLE,
             SRGB);
    }

    /**<p>
     * Constructs an <code>LinearGradientPaint</code> with default SRGB 
     * colorspace.
     *
     * @param startX the x coordinate of the gradient axis start point 
     * in user space
     *
     * @param startY the y coordinate of the gradient axis start point 
     * in user space
     *
     * @param endX the x coordinate of the gradient axis end point 
     * in user space
     * 
     * @param endY the y coordinate of the gradient axis end point 
     * in user space
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors corresponding to each fractional value
     *
     * @param cycleMethod either NO_CYCLE, REFLECT, or REPEAT
     *
     * @throws IllegalArgumentException if start and end points are the 
     * same points, or if fractions.length != colors.length, or if colors 
     * is less than 2 in size.
     *
     */
    public LinearGradientPaint(float startX, float startY, 
                               float endX, float endY, 
                               float[] fractions, Color[] colors, 
                               CycleMethodEnum cycleMethod) {
        this(new Point2D.Float(startX, startY), 
             new Point2D.Float(endX, endY), 
             fractions, 
             colors,
             cycleMethod,
             SRGB);
    }

    /**<p>
     * Constructs a <code>LinearGradientPaint</code> with the default 
     * NO_CYCLE repeating method and SRGB colorspace.
     *
     * @param start the gradient axis start <code>Point</code> in user space
     *
     * @param end the gradient axis end <code>Point</code> in user space
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors corresponding to each fractional value
     *
     * @throws NullPointerException if one of the points is null
     *
     * @throws IllegalArgumentException if start and end points are the 
     * same points, or if fractions.length != colors.length, or if colors 
     * is less than 2 in size.
     *
     */
    public LinearGradientPaint(Point2D start, Point2D end, float[] fractions,
                               Color[] colors) {

        this(start, end, fractions, colors, NO_CYCLE, SRGB);
    }
    
    /**<p>
     * Constructs a <code>LinearGradientPaint</code>.
     *
     * @param start the gradient axis start <code>Point</code> in user space
     *
     * @param end the gradient axis end <code>Point</code> in user space
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors corresponding to each fractional value
     *
     * @param cycleMethod either NO_CYCLE, REFLECT, or REPEAT
     *
     * @param colorSpace which colorspace to use for interpolation, 
     * either SRGB or LINEAR_RGB
     *   
     * @throws NullPointerException if one of the points is null
     *
     * @throws IllegalArgumentException if start and end points are the 
     * same points, or if fractions.length != colors.length, or if colors 
     * is less than 2 in size.
     *
     */
    public LinearGradientPaint(Point2D start, Point2D end, float[] fractions,
                               Color[] colors, 
                               CycleMethodEnum cycleMethod, 
                               ColorSpaceEnum colorSpace) {
	
        this(start, end, fractions, colors, cycleMethod, colorSpace, 
             new AffineTransform());
	
    }
    
    /**<p>
     * Constructs a <code>LinearGradientPaint</code>.
     *
     * @param start the gradient axis start <code>Point</code> in user space
     *
     * @param end the gradient axis end <code>Point</code> in user space
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors corresponding to each fractional value
     *
     * @param cycleMethod either NO_CYCLE, REFLECT, or REPEAT
     *
     * @param colorSpace which colorspace to use for interpolation, 
     * either SRGB or LINEAR_RGB
     *
     * @param gradientTransform transform to apply to the gradient
     *     
     * @throws NullPointerException if one of the points is null, 
     * or gradientTransform is null
     *
     * @throws IllegalArgumentException if start and end points are the 
     * same points, or if fractions.length != colors.length, or if colors 
     * is less than 2 in size.
     *
     */
    public LinearGradientPaint(Point2D start, Point2D end, float[] fractions,
                               Color[] colors,
                               CycleMethodEnum cycleMethod, 
                               ColorSpaceEnum colorSpace, 
                               AffineTransform gradientTransform) {
        super(fractions, colors, cycleMethod, colorSpace, gradientTransform);

        //
        // Check input parameters
        //	
        if (start == null || end == null) {
            throw new NullPointerException("Start and end points must be" +
                                           "non-null");
        }

        if (start.equals(end)) {
            throw new IllegalArgumentException("Start point cannot equal" +
                                               "endpoint");
        }

        //copy the points...
        this.start = (Point2D)start.clone();

        this.end = (Point2D)end.clone();
	
    }
    
    /**
     * Creates and returns a PaintContext used to generate the color pattern,
     * for use by the internal rendering engine.
     *
     * @param cm {@link ColorModel} that receives
     * the <code>Paint</code> data. This is used only as a hint.
     *
     * @param deviceBounds the device space bounding box of the 
     * graphics primitive being rendered
     *
     * @param userBounds the user space bounding box of the 
     * graphics primitive being rendered
     *
     * @param transform the {@link AffineTransform} from user
     * space into device space
     *
     * @param hints the hints that the context object uses to choose
     * between rendering alternatives
     *
     * @return the {@link PaintContext} that generates color patterns.
     *
     * @see PaintContext
     */
    public PaintContext createContext(ColorModel cm,
                                      Rectangle deviceBounds,
                                      Rectangle2D userBounds,
                                      AffineTransform transform,
                                      RenderingHints hints) {

        // Can't modify the transform passed in...
        transform = new AffineTransform(transform);
        //incorporate the gradient transform
        transform.concatenate(gradientTransform); 

        try {
            return new LinearGradientPaintContext(cm, 
                                                  deviceBounds,
                                                  userBounds, 
                                                  transform,
                                                  hints,
                                                  start, 
                                                  end,
                                                  fractions,
                                                  this.getColors(),
                                                  cycleMethod,
                                                  colorSpace);
        }
        catch(NoninvertibleTransformException e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
            throw new IllegalArgumentException("transform should be" + 
                                               "invertible");
        }
    }
    
    /**
     * Returns a copy of the start point of the gradient axis
     * @return a {@link Point2D} object that is a copy of the point
     * that anchors the first color of this 
     * <code>LinearGradientPaint</code>.  
     */
    public Point2D getStartPoint() {
        return new Point2D.Double(start.getX(), start.getY());
    }
    
    /** Returns a copy of the end point of the gradient axis
     * @return a {@link Point2D} object that is a copy of the point
     * that anchors the last color of this 
     * <code>LinearGradientPaint</code>.  
     */
    public Point2D getEndPoint() {
        return new Point2D.Double(end.getX(), end.getY());
    }
        
}


