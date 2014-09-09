/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package com.kitfox.svg.batik;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

/**
 * <p>
 * This class provides a way to fill a shape with a circular radial color 
 * gradient pattern. The user may specify 2 or more gradient colors, and this 
 * paint will provide an interpolation between each color.
 * <p>
 *
 * The user must provide an array of floats specifying how to distribute the 
 * colors along the gradient.  These values should range from 0.0 to 1.0 and 
 * act like keyframes along the gradient (they mark where the gradient should 
 * be exactly a particular color).
 *
 * <p>
 * This paint will map the first color of the gradient to a focus point within
 * the circle, and the last color to the perimeter of the circle, interpolating
 * smoothly for any inbetween colors specified by the user.  Any line drawn 
 * from the focus point to the circumference will span the all the gradient 
 * colors.  By default the focus is set to be the center of the circle.
 *
 * <p>
 * Specifying a focus point outside of the circle's radius will result in the 
 * focus being set to the intersection point of the focus-center line and the 
 * perimenter of the circle.
 * <p>
 *
 * Specifying a cycle method allows the user to control the painting behavior 
 * outside of the bounds of the circle's radius.  See LinearGradientPaint for 
 * more details.
 *
 * <p>
 * The following code demonstrates typical usage of RadialGradientPaint:
 * <p>
 * <code>
 * Point2D center = new Point2D.Float(0, 0);<br>
 * float radius = 20;
 * float[] dist = {0.0, 0.2, 1.0};<br>
 * Color[] colors = {Color.red, Color.white, Color.blue};<br>
 * RadialGradientPaint p = new RadialGradientPaint(center, radius, 
 * dist, colors);
 * </code>
 *
 * <p> In the event that the user does not set the first keyframe value equal
 * to 0 and the last keyframe value equal to 1, keyframes will be created at
 * these positions and the first and last colors will be replicated there.
 * So, if a user specifies the following arrays to construct a gradient:<br>
 * {Color.blue, Color.red}, {.3, .7}<br>
 * this will be converted to a gradient with the following keyframes:
 * {Color.blue, Color.blue, Color.red, Color.red}, {0, .3, .7, 1}
 *
 *
 * <p>
 * <img src = "radial.jpg">
 * <p>
 * This image demonstrates a radial gradient with NO_CYCLE and default focus.
 * <p>
 *
 * <img src = "radial2.jpg">
 * <p>
 * This image demonstrates a radial gradient with NO_CYCLE and non-centered 
 * focus.
 * <p>
 * 
 * <img src = "radial3.jpg">
 * <p>
 * This image demonstrates a radial gradient with REFLECT and non-centered 
 * focus.
 *
 * @author  Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id: RadialGradientPaint.java,v 1.1 2004/09/06 19:35:39 kitfox Exp $
 *
 */

public final class RadialGradientPaint extends MultipleGradientPaint {

    /** Focus point which defines the 0% gradient stop x coordinate. */
    private Point2D focus;

    /** Center of the circle defining the 100% gradient stop x coordinate. */
    private Point2D center;

    /** Radius of the outermost circle defining the 100% gradient stop. */
    private float radius;

    /**
     * <p>
     *
     * Constructs a <code>RadialGradientPaint</code>, using the center as the 
     * focus point.
     *
     * @param cx the x coordinate in user space of the center point of the 
     * circle defining the gradient.  The last color of the gradient is mapped
     * to the perimeter of this circle
     *
     * @param cy the y coordinate in user space of the center point of the 
     * circle defining the gradient.  The last color of the gradient is mapped
     * to the perimeter of this circle
     *
     * @param radius the radius of the circle defining the extents of the 
     * color gradient   
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors to use in the gradient. The first color 
     * is used at the focus point, the last color around the perimeter of the 
     * circle.
     *        
     *
     * @throws IllegalArgumentException  
     *         if fractions.length != colors.length, or if colors is less 
     *         than 2 in size, or if radius < 0
     *
     *
     */
    public RadialGradientPaint(float cx, float cy, float radius,
                               float[] fractions, Color[] colors) {
        this(cx, cy,
             radius,
             cx, cy,
             fractions,
             colors);
    }    
    
    /**
     * <p>
     *
     * Constructs a <code>RadialGradientPaint</code>, using the center as the 
     * focus point.
     *
     * @param center the center point, in user space, of the circle defining 
     * the gradient
     *
     * @param radius the radius of the circle defining the extents of the 
     * color gradient
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors to use in the gradient. The first color 
     * is used at the focus point, the last color around the perimeter of the 
     * circle.
     *   
     * @throws NullPointerException if center point is null
     *
     * @throws IllegalArgumentException  
     *         if fractions.length != colors.length, or if colors is less 
     *         than 2 in size, or if radius < 0
     *
     *
     */
    public RadialGradientPaint(Point2D center, float radius,
                               float[] fractions, Color[] colors) {
        this(center,
             radius,
             center,	    
             fractions,
             colors);
    }

    /**
     * <p>
     *
     * Constructs a <code>RadialGradientPaint</code>.
     *
     * @param cx the x coordinate in user space of the center point of the 
     * circle defining the gradient.  The last color of the gradient is mapped
     * to the perimeter of this circle
     *
     * @param cy the y coordinate in user space of the center point of the 
     * circle defining the gradient.  The last color of the gradient is mapped
     * to the perimeter of this circle
     *
     * @param radius the radius of the circle defining the extents of the 
     * color gradient
     *
     * @param fx the x coordinate of the point in user space to which the 
     * first color is mapped
     *
     * @param fy the y coordinate of the point in user space to which the 
     * first color is mapped
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors to use in the gradient. The first color 
     * is used at the focus point, the last color around the perimeter of the 
     * circle.
     *  
     * @throws IllegalArgumentException  
     *         if fractions.length != colors.length, or if colors is less 
     *         than 2 in size, or if radius < 0
     *
     *
     */
    public RadialGradientPaint(float cx, float cy, float radius,
                               float fx, float fy,
                               float[] fractions, Color[] colors) {
        this(new Point2D.Float(cx, cy),
             radius,
             new Point2D.Float(fx, fy),
             fractions,
             colors,
             NO_CYCLE,
             SRGB);
    }
    
    /**
     * <p>
     *
     * Constructs a <code>RadialGradientPaint</code>.
     *
     * @param center the center point, in user space, of the circle defining 
     * the gradient. The last color of the gradient is mapped to the perimeter
     * of this circle
     *
     * @param radius the radius of the circle defining the extents of the color
     * gradient
     *
     * @param focus the point, in user space, to which the first color is 
     * mapped    
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors to use in the gradient. The first color 
     * is used at the focus point, the last color around the perimeter of the
     * circle.
     *   
     * @throws NullPointerException if one of the points is null
     *
     * @throws IllegalArgumentException  
     *         if fractions.length != colors.length, or if colors is less 
     *         than 2 in size, or if radius < 0
     *
     */
    public RadialGradientPaint(Point2D center, float radius,
                               Point2D focus,
                               float[] fractions, Color[] colors) {
        this(center,
             radius,
             focus,
             fractions,
             colors,
             NO_CYCLE,
             SRGB);	
    }
    
    /**
     * <p>
     *
     * Constructs a <code>RadialGradientPaint</code>.
     *
     * @param center the center point in user space of the circle defining the
     * gradient. The last color of the gradient is mapped to the perimeter of 
     * this circle
     *
     * @param radius the radius of the circle defining the extents of the color
     * gradient
     *
     * @param focus the point in user space to which the first color is mapped
     *   
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors to use in the gradient. The first color is
     * used at the focus point, the last color around the perimeter of the 
     * circle.
     *
     * @param cycleMethod either NO_CYCLE, REFLECT, or REPEAT
     *
     * @param colorSpace which colorspace to use for interpolation, 
     * either SRGB or LINEAR_RGB
     *   
     * @throws NullPointerException if one of the points is null
     *
     * @throws IllegalArgumentException 
     *         if fractions.length != colors.length, or if colors is less 
     *         than 2 in size, or if radius < 0
     *
     */
    public RadialGradientPaint(Point2D center, float radius,
                               Point2D focus,
                               float[] fractions, Color[] colors,
                               CycleMethodEnum cycleMethod, 
                               ColorSpaceEnum colorSpace) {
        this(center,
             radius,
             focus,
             fractions,
             colors,
             cycleMethod,
             colorSpace,
             new AffineTransform());
    }

    /**
     * <p>
     *
     * Constructs a <code>RadialGradientPaint</code>.
     *
     * @param center the center point in user space of the circle defining the
     * gradient.  The last color of the gradient is mapped to the perimeter of
     * this circle
     *
     * @param radius the radius of the circle defining the extents of the color
     * gradient. 
     *
     * @param focus the point in user space to which the first color is mapped
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors to use in the gradient. The first color is
     * used at the focus point, the last color around the perimeter of the 
     * circle.
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
     * @throws IllegalArgumentException 
     *         if fractions.length != colors.length, or if colors is less 
     *         than 2 in size, or if radius < 0
     *
     */
    public RadialGradientPaint(Point2D center,
                               float radius,
                               Point2D focus,
                               float[] fractions,  Color[] colors,
                               CycleMethodEnum cycleMethod, 
                               ColorSpaceEnum colorSpace,
                               AffineTransform gradientTransform){
        super(fractions, colors, cycleMethod, colorSpace, gradientTransform);

        // Check input arguments
        if (center == null) {
            throw new NullPointerException("Center point should not be null.");
        }
	
        if (focus == null) {
            throw new NullPointerException("Focus point should not be null.");
        }

        if (radius <= 0) {
            throw new IllegalArgumentException("radius should be greater than zero");
        }

        //copy parameters
        this.center = (Point2D)center.clone();
        this.focus = (Point2D)focus.clone();
        this.radius = radius;
    }
    
    /**
     * <p>
     *
     * Constructs a <code>RadialGradientPaint</code>, the gradient circle is 
     * defined by a bounding box.
     *    
     * @param gradientBounds the bounding box, in user space, of the circle 
     * defining outermost extent of the gradient.
     *
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the 
     * distribution of colors along the gradient
     *
     * @param colors array of colors to use in the gradient. The first color 
     * is used at the focus point, the last color around the perimeter of the 
     * circle.
     *
     * @throws NullPointerException if the gradientBounds is null
     *
     * @throws IllegalArgumentException 
     *         if fractions.length != colors.length, or if colors is less 
     *         than 2 in size, or if radius < 0
     *
     */    
    public RadialGradientPaint(Rectangle2D gradientBounds,
                               float[] fractions,  Color[] colors) {

        //calculate center point and radius based on bounding box coordinates.
        this((float)gradientBounds.getX() +
             ( (float)gradientBounds.getWidth() / 2),
	     
             (float)gradientBounds.getY() +
             ( (float)gradientBounds.getWidth() / 2),
	     
             (float)gradientBounds.getWidth() / 2, 
             fractions, colors);
    }


    /** <p>
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
     * @throws IllegalArgumentException if the transform is not invertible
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
        // incorporate the gradient transform
        transform.concatenate(gradientTransform);

        try{
            return new RadialGradientPaintContext
                (cm, deviceBounds, userBounds, transform, hints,
                 (float)center.getX(), (float)center.getY(), radius,
                 (float)focus.getX(), (float)focus.getY(),
                 fractions, colors, cycleMethod, colorSpace);       	    
        }
	
        catch(NoninvertibleTransformException e){
            throw new IllegalArgumentException("transform should be " +
                                               "invertible");
        }
    }

    /**
     * Returns a copy of the center point of the radial gradient.
     * @return a {@link Point2D} object that is a copy of the center point     
     */
    public Point2D getCenterPoint() {
        return new Point2D.Double(center.getX(), center.getY());
    }
    
    /** Returns a copy of the end point of the gradient axis.
     * @return a {@link Point2D} object that is a copy of the focus point     
     */
    public Point2D getFocusPoint() {
        return new Point2D.Double(focus.getX(), focus.getY());
    }

    /** Returns the radius of the circle defining the radial gradient.
     * @return the radius of the circle defining the radial gradient
     */
    public float getRadius() {
        return radius;
    }
    
}

