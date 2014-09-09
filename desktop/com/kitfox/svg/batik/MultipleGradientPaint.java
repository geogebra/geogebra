/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package com.kitfox.svg.batik;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

/** This is the superclass for Paints which use a multiple color
 * gradient to fill in their raster.  It provides storage for variables and
 * enumerated values common to LinearGradientPaint and RadialGradientPaint.
 *
 *
 * @author Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id: MultipleGradientPaint.java,v 1.2 2004/09/27 09:27:27 kitfox Exp $
 *
 */

public abstract class MultipleGradientPaint implements Paint {

    /** Transparency. */
    protected int transparency;

    /** Gradient keyframe values in the range 0 to 1. */
    protected float[] fractions;

    /** Gradient colors. */
    protected Color[] colors;

    /** Transform to apply to gradient. */
    protected AffineTransform gradientTransform;

    /** The method to use when painting out of the gradient bounds. */
    protected CycleMethodEnum cycleMethod;

    /** The colorSpace in which to perform the interpolation. */
    protected ColorSpaceEnum colorSpace;

    /** Inner class to allow for typesafe enumerated ColorSpace values. */
    public static class ColorSpaceEnum {
    }

    /** Inner class to allow for typesafe enumerated CycleMethod values. */
    public static class CycleMethodEnum {
    }

    /** Indicates (if the gradient starts or ends inside the target region)
     *  to use the terminal colors to fill the remaining area. (default)
     */
    public static final CycleMethodEnum NO_CYCLE = new CycleMethodEnum();

    /** Indicates (if the gradient starts or ends inside the target region),
     *  to cycle the gradient colors start-to-end, end-to-start to fill the
     *  remaining area.
     */
    public static final CycleMethodEnum REFLECT = new CycleMethodEnum();

    /** Indicates (if the gradient starts or ends inside the target region),
     *  to cycle the gradient colors start-to-end, start-to-end to fill the
     *  remaining area.
     */
    public static final CycleMethodEnum REPEAT = new CycleMethodEnum();

    /** Indicates that the color interpolation should occur in sRGB space.
     *  (default)
     */
    public static final ColorSpaceEnum SRGB = new ColorSpaceEnum();

    /** Indicates that the color interpolation should occur in linearized
     *  RGB space.
     */
    public static final ColorSpaceEnum LINEAR_RGB = new ColorSpaceEnum();


     /**
     * Superclass constructor, typical user should never have to call this.
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
     * @throws NullPointerException if arrays are null, or
     * gradientTransform is null
     *
     * @throws IllegalArgumentException if fractions.length != colors.length,
     * or if colors is less than 2 in size, or if an enumerated value is bad.
     *
     * @see java.awt.PaintContext
     */
    public MultipleGradientPaint(float[] fractions,
                                 Color[] colors,
                                 CycleMethodEnum cycleMethod,
                                 ColorSpaceEnum colorSpace,
                                 AffineTransform gradientTransform) {

        if (fractions == null) {
            throw new IllegalArgumentException("Fractions array cannot be " +
                                               "null");
        }

        if (colors == null) {
            throw new IllegalArgumentException("Colors array cannot be null");
        }

        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Colors and fractions must " +
                                               "have equal size");
        }

        if (colors.length < 2) {
            throw new IllegalArgumentException("User must specify at least " +
                                               "2 colors");
        }

        if ((colorSpace != LINEAR_RGB) &&
            (colorSpace != SRGB)) {
            throw new IllegalArgumentException("Invalid colorspace for " +
                                               "interpolation.");
        }

        if ((cycleMethod != NO_CYCLE) &&
            (cycleMethod != REFLECT) &&
            (cycleMethod != REPEAT)) {
            throw new IllegalArgumentException("Invalid cycle method.");
        }

        if (gradientTransform == null) {
            throw new IllegalArgumentException("Gradient transform cannot be "+
                                               "null.");
        }

        //copy the fractions array
        this.fractions = new float[fractions.length];
        System.arraycopy(fractions, 0, this.fractions, 0, fractions.length);

        //copy the colors array
        this.colors = new Color[colors.length];
        System.arraycopy(colors, 0, this.colors, 0, colors.length);

        //copy some flags
        this.colorSpace = colorSpace;
        this.cycleMethod = cycleMethod;

        //copy the gradient transform
        this.gradientTransform = (AffineTransform)gradientTransform.clone();

        // Process transparency
        boolean opaque = true;
        for(int i=0; i<colors.length; i++){
            opaque = opaque && (colors[i].getAlpha()==0xff);
        }

        if(opaque) {
            transparency = OPAQUE;
        }

        else {
            transparency = TRANSLUCENT;
        }
    }

    /**
     * Returns a copy of the array of colors used by this gradient.
     * @return a copy of the array of colors used by this gradient
     *
     */
    public Color[] getColors() {
        Color colors[] = new Color[this.colors.length];
        System.arraycopy(this.colors, 0, colors, 0, this.colors.length);
        return colors;
    }

    /**
     * Returns a copy of the array of floats used by this gradient
     * to calculate color distribution.
     * @return a copy of the array of floats used by this gradient to
     * calculate color distribution
     *
     */
    public float[] getFractions() {
        float fractions[] = new float[this.fractions.length];
        System.arraycopy(this.fractions, 0, fractions, 0, this.fractions.length);
        return fractions;
    }

    /**
     * Returns the transparency mode for this LinearGradientPaint.
     * @return an integer value representing this LinearGradientPaint object's
     * transparency mode.
     * @see java.awt.Transparency
     */
    public int getTransparency() {
        return transparency;
    }

    /**
     * Returns the enumerated type which specifies cycling behavior.
     * @return the enumerated type which specifies cycling behavior
     */
    public CycleMethodEnum getCycleMethod() {
        return cycleMethod;
    }

    /**
     * Returns the enumerated type which specifies color space for
     * interpolation.
     * @return the enumerated type which specifies color space for
     * interpolation
     */
    public ColorSpaceEnum getColorSpace() {
        return colorSpace;
    }

    /**
     * Returns a copy of the transform applied to the gradient.
     * @return a copy of the transform applied to the gradient.
     */
    public AffineTransform getTransform() {
        return (AffineTransform)gradientTransform.clone();
    }
}
