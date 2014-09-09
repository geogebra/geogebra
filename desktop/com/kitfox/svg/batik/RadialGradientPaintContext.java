/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package com.kitfox.svg.batik;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

/**
 * Provides the actual implementation for the RadialGradientPaint.
 * This is where the pixel processing is done.  A RadialGradienPaint
 * only supports circular gradients, but it should be possible to scale
 * the circle to look approximately elliptical, by means of a
 * gradient transform passed into the RadialGradientPaint constructor.
 *
 * @author Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id: RadialGradientPaintContext.java,v 1.2 2005/10/12 20:36:55 kitfox Exp $
 *
 */
final class RadialGradientPaintContext extends MultipleGradientPaintContext {  
    
    /** True when (focus == center)  */
    private boolean isSimpleFocus = false;

    /** True when (cycleMethod == NO_CYCLE) */
    private boolean isNonCyclic = false;
       
    /** Radius of the outermost circle defining the 100% gradient stop. */
    private float radius;   
    
    /** Variables representing center and focus points. */
    private float centerX, centerY, focusX, focusY;     

    /** Radius of the gradient circle squared. */
    private float radiusSq; 
        
    /** Constant part of X, Y user space coordinates. */
    private float constA, constB;
       
    /** This value represents the solution when focusX == X.  It is called
     * trivial because it is easier to calculate than the general case.
     */
    private float trivial;       

    private static final int FIXED_POINT_IMPL = 1;
    private static final int DEFAULT_IMPL     = 2;
    private static final int ANTI_ALIAS_IMPL  = 3;

    private int fillMethod;
    
    /** Amount for offset when clamping focus. */
    private static final float SCALEBACK = .97f;
    
    /** 
     * Constructor for RadialGradientPaintContext.
     *
     *  @param cm {@link ColorModel} that receives
     *  the <code>Paint</code> data. This is used only as a hint.
     *
     *  @param deviceBounds the device space bounding box of the 
     *  graphics primitive being rendered
     *
     *  @param userBounds the user space bounding box of the 
     *  graphics primitive being rendered
     * 
     *  @param t the {@link AffineTransform} from user
     *  space into device space (gradientTransform should be 
     *  concatenated with this)    
     *
     *  @param hints the hints that the context object uses to choose
     *  between rendering alternatives
     *       
     *  @param cx the center point in user space of the circle defining 
     *  the gradient.  The last color of the gradient is mapped to the 
     *  perimeter of this circle X coordinate
     *
     *  @param cy the center point in user space of the circle defining 
     *  the gradient.  The last color of the gradient is mapped to the 
     *  perimeter of this circle Y coordinate
     *     
     *  @param r the radius of the circle defining the extents of the 
     *  color gradient
     *
     *  @param fx the point in user space to which the first color is mapped
     *  X coordinate
     *
     *  @param fy the point in user space to which the first color is mapped
     *  Y coordinate
     *
     *  @param fractions the fractions specifying the gradient distribution
     *
     *  @param colors the gradient colors
     *
     *  @param cycleMethod either NO_CYCLE, REFLECT, or REPEAT
     *
     *  @param colorSpace which colorspace to use for interpolation, 
     *  either SRGB or LINEAR_RGB
     *
     */
    public RadialGradientPaintContext(ColorModel cm,
                                      Rectangle deviceBounds,
                                      Rectangle2D userBounds,
                                      AffineTransform t,
                                      RenderingHints hints,
                                      float cx, float cy,
                                      float r,
                                      float fx, float fy,
                                      float[] fractions,
                                      Color[] colors,
                                      MultipleGradientPaint.CycleMethodEnum 
                                      cycleMethod,
                                      MultipleGradientPaint.ColorSpaceEnum 
                                      colorSpace)
        throws NoninvertibleTransformException
    {       	
        super(cm, deviceBounds, userBounds, t, hints, fractions, colors, 
              cycleMethod, colorSpace);

        //copy some parameters.
        centerX = cx;
        centerY = cy;	
        focusX = fx;
        focusY = fy;
        radius = r;

        this.isSimpleFocus = (focusX == centerX) && (focusY == centerY);
        this.isNonCyclic = (cycleMethod == RadialGradientPaint.NO_CYCLE);
	
        //for use in the quadractic equation
        radiusSq = radius * radius;

        float dX = focusX - centerX;
        float dY = focusY - centerY;

        double dist = Math.sqrt((dX * dX) + (dY * dY));

        //test if distance from focus to center is greater than the radius
        if (dist > radius* SCALEBACK) { //clamp focus to radius
          double angle = Math.atan2(dY, dX);
			
          //x = r cos theta, y = r sin theta
          focusX = (float)(SCALEBACK * radius * Math.cos(angle)) + centerX;
          
          focusY = (float)(SCALEBACK * radius * Math.sin(angle)) + centerY;
        }

        //calculate the solution to be used in the case where X == focusX
        //in cyclicCircularGradientFillRaster
        dX = focusX - centerX;
        trivial = (float)Math.sqrt(radiusSq - (dX * dX));

        // constant parts of X, Y user space coordinates 
        constA = a02 - centerX;
        constB = a12 - centerY;

        Object colorRend;
        Object rend;
        //hints can be null on Mac OSX
        if (hints == null)
        {
            colorRend = RenderingHints.VALUE_COLOR_RENDER_DEFAULT;
            rend = RenderingHints.VALUE_RENDER_DEFAULT;
        }
        else
        {
            colorRend = hints.get(RenderingHints.KEY_COLOR_RENDERING);
            rend      = hints.get(RenderingHints.KEY_RENDERING);
        }

        fillMethod = 0;

        if ((rend      == RenderingHints.VALUE_RENDER_QUALITY) ||
            (colorRend == RenderingHints.VALUE_COLOR_RENDER_QUALITY)) {
            // System.out.println("AAHints set: " + rend + ", " + colorRend);
            fillMethod = ANTI_ALIAS_IMPL;
        }

        if ((rend      == RenderingHints.VALUE_RENDER_SPEED) ||
            (colorRend == RenderingHints.VALUE_COLOR_RENDER_SPEED)) {
            // System.out.println("SPHints set: " + rend + ", " + colorRend);
            fillMethod = DEFAULT_IMPL;
        }

        // We are in the 'default' case, no hint or hint set to
        // DEFAULT values...
        if (fillMethod == 0) {
            // For now we will always use the 'default' impl if 
            // one is not specified.
            fillMethod = DEFAULT_IMPL;

            if (false) {
                // This could be used for a 'smart' choice in
                // the default case, if the gradient has obvious
                // discontinuites use AA, otherwise default
                if (hasDiscontinuity) {
                    fillMethod = ANTI_ALIAS_IMPL;
                } else {
                    fillMethod = DEFAULT_IMPL;
                }
            }
        }

        if ((fillMethod == DEFAULT_IMPL) &&
            (isSimpleFocus && isNonCyclic && isSimpleLookup)) {
            this.calculateFixedPointSqrtLookupTable();
            fillMethod = FIXED_POINT_IMPL;
        }
    }
    
    /**
     * Return a Raster containing the colors generated for the graphics
     * operation.
     * @param x,y,w,h The area in device space for which colors are
     * generated.
     */
    protected void fillRaster(int pixels[], int off, int adjust,
                              int x, int y, int w, int h) {
        switch(fillMethod) {
        case FIXED_POINT_IMPL:
            // System.out.println("Calling FP");
            fixedPointSimplestCaseNonCyclicFillRaster(pixels, off, adjust, x, 
                                                      y, w, h);
            break;
        case ANTI_ALIAS_IMPL:
            // System.out.println("Calling AA");
            antiAliasFillRaster(pixels, off, adjust, x, y, w, h);
            break;
        case DEFAULT_IMPL:
        default:
            // System.out.println("Calling Default");
            cyclicCircularGradientFillRaster(pixels, off, adjust, x, y, w, h);
        }
    }    
    
    /**
     * This code works in the simplest of cases, where the focus == center 
     * point, the gradient is noncyclic, and the gradient lookup method is 
     * fast (single array index, no conversion necessary).
     *
     */         
    private void fixedPointSimplestCaseNonCyclicFillRaster(int pixels[], 
                                                           int off,
                                                           int adjust, 
                                                           int x, int y, 
                                                           int w, int h) {
        float iSq=0;  // Square distance index
        final float indexFactor = fastGradientArraySize / radius;      

        //constant part of X and Y coordinates for the entire raster
        final float constX = (a00*x) + (a01*y) + constA;
        final float constY = (a10*x) + (a11*y) + constB;
        final float deltaX = indexFactor * a00; //incremental change in dX
        final float deltaY = indexFactor * a10; //incremental change in dY
        float dX, dY; //the current distance from center
        final int fixedArraySizeSq=
            (fastGradientArraySize * fastGradientArraySize);
        float g, gDelta, gDeltaDelta, temp; //gradient square value
        int gIndex; // integer number used to index gradient array
        int iSqInt; // Square distance index       		   
	
        int end, j; //indexing variables
        int indexer = off;//used to index pixels array

        temp        = ((deltaX * deltaX) + (deltaY * deltaY));
        gDeltaDelta = ((temp * 2));

        if (temp > fixedArraySizeSq) {
            // This combination of scale and circle radius means
            // essentially no pixels will be anything but the end
            // stop color.  This also avoids math problems.
            final int val = gradientOverflow;
            for(j = 0; j < h; j++){ //for every row
                //for every column (inner loop begins here)
                for (end = indexer+w; indexer < end; indexer++) 
                    pixels[indexer] = val;
                indexer += adjust;
            }
            return;
        }

        // For every point in the raster, calculate the color at that point
        for(j = 0; j < h; j++){ //for every row
            //x and y (in user space) of the first pixel of this row
            dX = indexFactor * ((a01*j) + constX);
            dY = indexFactor * ((a11*j) + constY);	   	   

            // these values below here allow for an incremental calculation
            // of dX^2 + dY^2 

            //initialize to be equal to distance squared
            g = (((dY * dY) + (dX * dX)) );
            gDelta =  (((((deltaY * dY) + (deltaX * dX))* 2) + 
                        temp));	 
	    
            //for every column (inner loop begins here)
            for (end = indexer+w; indexer < end; indexer++) {	       
                //determine the distance to the center
		
                //since this is a non cyclic fill raster, crop at "1" and 0
                if (g >= fixedArraySizeSq) {
                    pixels[indexer] = gradientOverflow;
                }
		
                // This should not happen as gIndex is a square
                // quantity. Code commented out on purpose, can't underflow.
                // else if (g < 0) {
                //    gIndex = 0;		    
                // }
		
                else {
                    iSq = (g * invSqStepFloat);
                    
                    iSqInt = (int)iSq; //chop off fractional part
                    iSq -= iSqInt;		    
                    gIndex = sqrtLutFixed[iSqInt];
                    gIndex += (int)(iSq * (sqrtLutFixed[iSqInt + 1]-gIndex));
                    pixels[indexer] = gradient[gIndex]; 
                }
		
				
                //incremental calculation
                g += gDelta;
                gDelta += gDeltaDelta;		
            }	  
            indexer += adjust;
        }
    }

    /** Length of a square distance intervale in the lookup table */
    private float invSqStepFloat; 
    
    /** Used to limit the size of the square root lookup table */
    private int MAX_PRECISION = 256;
    
    /** Square root lookup table */
    private int sqrtLutFixed[] = new int[MAX_PRECISION];
    
    /**
     * Build square root lookup table
     */       
    private void calculateFixedPointSqrtLookupTable() {	      
        float sqStepFloat;
        sqStepFloat = ((fastGradientArraySize  * fastGradientArraySize) 
                       / (MAX_PRECISION - 2));
	
        // The last two values are the same so that linear square root 
        // interpolation can happen on the maximum reachable element in the 
        // lookup table (precision-2)
        int i;
        for (i = 0; i < MAX_PRECISION - 1; i++) {
            sqrtLutFixed[i] = (int)(Math.sqrt(i*sqStepFloat));
        }
        sqrtLutFixed[i] = sqrtLutFixed[i-1];	
        invSqStepFloat = 1/sqStepFloat;
    }
    
    /** Fill the raster, cycling the gradient colors when a point falls outside
     *  of the perimeter of the 100% stop circle.          
     * 
     *  This calculation first computes the intersection point of the line
     *  from the focus through the current point in the raster, and the
     *  perimeter of the gradient circle.
     * 
     *  Then it determines the percentage distance of the current point along
     *  that line (focus is 0%, perimeter is 100%). 
     *
     *  Equation of a circle centered at (a,b) with radius r:
     *  (x-a)^2 + (y-b)^2 = r^2
     *  Equation of a line with slope m and y-intercept b
     *  y = mx + b
     *  replacing y in the cirlce equation and solving using the quadratic
     *  formula produces the following set of equations.  Constant factors have
     *  been extracted out of the inner loop.
     *
     */   
    private void cyclicCircularGradientFillRaster(int pixels[], int off, 
                                                  int adjust, 
                                                  int x, int y, 
                                                  int w, int h) {
        // Constant part of the C factor of the quadratic equation
        final double constC = 
            -(radiusSq) + (centerX * centerX) + (centerY * centerY);
        double A; //coefficient of the quadratic equation (Ax^2 + Bx + C = 0)
        double B; //coefficient of the quadratic equation
        double C; //coefficient of the quadratic equation
        double slope; //slope of the focus-perimeter line
        double yintcpt; //y-intercept of the focus-perimeter line
        double solutionX;//intersection with circle X coordinate
        double solutionY;//intersection with circle Y coordinate       
       	final float constX = (a00*x) + (a01*y) + a02;//const part of X coord
        final float constY = (a10*x) + (a11*y) + a12; //const part of Y coord
       	final float precalc2 = 2 * centerY;//const in inner loop quad. formula
        final float precalc3 =-2 * centerX;//const in inner loop quad. formula
        float X; // User space point X coordinate 
        float Y; // User space point Y coordinate
        float g;//value between 0 and 1 specifying position in the gradient
        float det; //determinant of quadratic formula (should always be >0)
        float currentToFocusSq;//sq distance from the current pt. to focus
        float intersectToFocusSq;//sq distance from the intersect pt. to focus
        float deltaXSq; //temp variable for a change in X squared.
        float deltaYSq; //temp variable for a change in Y squared.
        int indexer = off; //index variable for pixels array
        int i, j; //indexing variables for FOR loops
        int pixInc = w+adjust;//incremental index change for pixels array

        for (j = 0; j < h; j++) { //for every row
	    
            X = (a01*j) + constX; //constants from column to column
            Y = (a11*j) + constY;
	    
            //for every column (inner loop begins here)
            for (i = 0; i < w; i++) {	       			
	
                // special case to avoid divide by zero or very near zero
                if (((X-focusX)>-0.000001) &&
                    ((X-focusX)< 0.000001)) {		   
                    solutionX = focusX;
		    
                    solutionY = centerY;
		    
                    solutionY += (Y > focusY)?trivial:-trivial;
                }
		
                else {    
		    
                    //slope of the focus-current line
                    slope =   (Y - focusY) / (X - focusX);
		    
                    yintcpt = Y - (slope * X); //y-intercept of that same line
		    
                    //use the quadratic formula to calculate the intersection
                    //point		  
                    A = (slope * slope) + 1; 
		    
                    B =  precalc3 + (-2 * slope * (centerY - yintcpt));
		    
                    C =  constC + (yintcpt* (yintcpt - precalc2));
		    
                    det = (float)Math.sqrt((B * B) - ( 4 * A * C));
		    
                    solutionX = -B;
		    
                    //choose the positive or negative root depending
                    //on where the X coord lies with respect to the focus.
                    solutionX += (X < focusX)?-det:det;
		    
                    solutionX = solutionX / (2 * A);//divisor
		    
                    solutionY = (slope * solutionX) + yintcpt;
                }	                    	

                //calculate the square of the distance from the current point 
                //to the focus and the square of the distance from the 
                //intersection point to the focus. Want the squares so we can
                //do 1 square root after division instead of 2 before.

                deltaXSq = (float)solutionX - focusX;
                deltaXSq = deltaXSq * deltaXSq;

                deltaYSq = (float)solutionY - focusY;
                deltaYSq = deltaYSq * deltaYSq;

                intersectToFocusSq = deltaXSq + deltaYSq;

                deltaXSq = X - focusX;
                deltaXSq = deltaXSq * deltaXSq;

                deltaYSq = Y - focusY;
                deltaYSq = deltaYSq * deltaYSq;

                currentToFocusSq = deltaXSq + deltaYSq;

                //want the percentage (0-1) of the current point along the 
                //focus-circumference line
                g = (float)Math.sqrt(currentToFocusSq / intersectToFocusSq);

                //Get the color at this point
                pixels[indexer + i] = indexIntoGradientsArrays(g);
		
                X += a00; //incremental change in X, Y
                Y += a10;	
            } //end inner loop
            indexer += pixInc;
        } //end outer loop
    }


    /** Fill the raster, cycling the gradient colors when a point
     *  falls outside of the perimeter of the 100% stop circle. Use
     *  the anti-aliased gradient lookup.
     *
     *  This calculation first computes the intersection point of the line
     *  from the focus through the current point in the raster, and the
     *  perimeter of the gradient circle.
     * 
     *  Then it determines the percentage distance of the current point along
     *  that line (focus is 0%, perimeter is 100%). 
     *
     *  Equation of a circle centered at (a,b) with radius r:
     *  (x-a)^2 + (y-b)^2 = r^2
     *  Equation of a line with slope m and y-intercept b
     *  y = mx + b
     *  replacing y in the cirlce equation and solving using the quadratic
     *  formula produces the following set of equations.  Constant factors have
     *  been extracted out of the inner loop.
     * */
    private void antiAliasFillRaster(int pixels[], int off, 
                                     int adjust, 
                                     int x, int y, 
                                     int w, int h) {
        // Constant part of the C factor of the quadratic equation
        final double constC = 
            -(radiusSq) + (centerX * centerX) + (centerY * centerY);
        //coefficients of the quadratic equation (Ax^2 + Bx + C = 0)
       	final float precalc2 = 2 * centerY;//const in inner loop quad. formula
        final float precalc3 =-2 * centerX;//const in inner loop quad. formula

        //const part of X,Y coord (shifted to bottom left corner of pixel.
       	final float constX = (a00*(x-.5f)) + (a01*(y+.5f)) + a02;
        final float constY = (a10*(x-.5f)) + (a11*(y+.5f)) + a12;
        float X; // User space point X coordinate 
        float Y; // User space point Y coordinate
        int i, j; //indexing variables for FOR loops
        int indexer = off-1; //index variable for pixels array

        // Size of a pixel in user space.
        double pixSzSq = (float)(a00*a00+a01*a01+a10*a10+a11*a11);
        double [] prevGs = new double[w+1];
        double deltaXSq, deltaYSq;
        double solutionX, solutionY;
        double slope, yintcpt, A, B, C, det;
        double intersectToFocusSq, currentToFocusSq;
        double g00, g01, g10, g11;

        // Set X,Y to top left corner of first pixel of first row.
        X = constX - a01;
        Y = constY - a11;

        // Calc top row of g's.
        for (i=0; i <= w; i++) {
            // special case to avoid divide by zero or very near zero
            if (((X-focusX)>-0.000001) &&
                ((X-focusX)< 0.000001)) {		   
                solutionX = focusX;
                solutionY = centerY;
                solutionY += (Y > focusY)?trivial:-trivial;
            }
            else { 
                // Formula for Circle: (X-Xc)^2 + (Y-Yc)^2 - R^2 = 0
                // Formula line:        Y = Slope*x + Y0;
                // 
                // So you substitue line into Circle and apply
                // Quadradic formula.


                //slope of the focus-current line
                slope =   (Y - focusY) / (X - focusX);
		    
                yintcpt = Y - (slope * X); //y-intercept of that same line
		    
                //use the quadratic formula to calculate the intersection
                //point		  
                A = (slope * slope) + 1; 
		    
                B =  precalc3 + (-2 * slope * (centerY - yintcpt));
		    
                C =  constC + (yintcpt* (yintcpt - precalc2));
		    
                det = Math.sqrt((B * B) - ( 4 * A * C));
		    
                solutionX = -B;
		    
                //choose the positive or negative root depending
                //on where the X coord lies with respect to the focus.
                solutionX += (X < focusX)?-det:det;
		    
                solutionX = solutionX / (2 * A);//divisor
		    
                solutionY = (slope * solutionX) + yintcpt;
            }	                    	

            //calculate the square of the distance from the current point 
            //to the focus and the square of the distance from the 
            //intersection point to the focus. Want the squares so we can
            //do 1 square root after division instead of 2 before.
            deltaXSq = solutionX - focusX;
            deltaXSq = deltaXSq * deltaXSq;
        
            deltaYSq = solutionY - focusY;
            deltaYSq = deltaYSq * deltaYSq;
        
            intersectToFocusSq = deltaXSq + deltaYSq;
        
            deltaXSq = X - focusX;
            deltaXSq = deltaXSq * deltaXSq;
        
            deltaYSq = Y - focusY;
            deltaYSq = deltaYSq * deltaYSq;
        
            currentToFocusSq = deltaXSq + deltaYSq;
        
            //want the percentage (0-1) of the current point along the 
            //focus-circumference line
            prevGs[i] = Math.sqrt(currentToFocusSq / intersectToFocusSq);

            X += a00; //incremental change in X, Y
            Y += a10;	
        }

        for (j = 0; j < h; j++) { //for every row
	    
            // Set X,Y to bottom edge of pixel row.
            X = (a01*j) + constX; //constants from row to row
            Y = (a11*j) + constY;

            g10 = prevGs[0];
            // special case to avoid divide by zero or very near zero
            if (((X-focusX)>-0.000001) &&
                ((X-focusX)< 0.000001)) {		   
                solutionX = focusX;
                solutionY = centerY;
                solutionY += (Y > focusY)?trivial:-trivial;
            }
            else { 
                // Formula for Circle: (X-Xc)^2 + (Y-Yc)^2 - R^2 = 0
                // Formula line:        Y = Slope*x + Y0;
                // 
                // So you substitue line into Circle and apply
                // Quadradic formula.


                //slope of the focus-current line
                slope =   (Y - focusY) / (X - focusX);
		    
                yintcpt = Y - (slope * X); //y-intercept of that same line
		    
                //use the quadratic formula to calculate the intersection
                //point		  
                A = (slope * slope) + 1; 
		    
                B =  precalc3 + (-2 * slope * (centerY - yintcpt));
		    
                C =  constC + (yintcpt* (yintcpt - precalc2));
		    
                det = Math.sqrt((B * B) - ( 4 * A * C));
		    
                solutionX = -B;
		    
                //choose the positive or negative root depending
                //on where the X coord lies with respect to the focus.
                solutionX += (X < focusX)?-det:det;
		    
                solutionX = solutionX / (2 * A);//divisor
		    
                solutionY = (slope * solutionX) + yintcpt;
            }	                    	

            //calculate the square of the distance from the current point 
            //to the focus and the square of the distance from the 
            //intersection point to the focus. Want the squares so we can
            //do 1 square root after division instead of 2 before.
            deltaXSq = solutionX - focusX;
            deltaXSq = deltaXSq * deltaXSq;
        
            deltaYSq = solutionY - focusY;
            deltaYSq = deltaYSq * deltaYSq;
        
            intersectToFocusSq = deltaXSq + deltaYSq;
        
            deltaXSq = X - focusX;
            deltaXSq = deltaXSq * deltaXSq;
        
            deltaYSq = Y - focusY;
            deltaYSq = deltaYSq * deltaYSq;
                
            currentToFocusSq = deltaXSq + deltaYSq;
            g11 = Math.sqrt(currentToFocusSq / intersectToFocusSq);
            prevGs[0] = g11;
            
            X += a00; //incremental change in X, Y
            Y += a10;	

            //for every column (inner loop begins here)
            for (i=1; i <= w; i++) {
                g00 = g10;
                g01 = g11;
                g10 = prevGs[i];

                // special case to avoid divide by zero or very near zero
                if (((X-focusX)>-0.000001) &&
                    ((X-focusX)< 0.000001)) {		   
                    solutionX = focusX;
                    solutionY = centerY;
                    solutionY += (Y > focusY)?trivial:-trivial;
                }
                else { 
                    // Formula for Circle: (X-Xc)^2 + (Y-Yc)^2 - R^2 = 0
                    // Formula line:        Y = Slope*x + Y0;
                    // 
                    // So you substitue line into Circle and apply
                    // Quadradic formula.


                    //slope of the focus-current line
                    slope =   (Y - focusY) / (X - focusX);
		    
                    yintcpt = Y - (slope * X); //y-intercept of that same line
		    
                    //use the quadratic formula to calculate the intersection
                    //point		  
                    A = (slope * slope) + 1; 
		    
                    B =  precalc3 + (-2 * slope * (centerY - yintcpt));
		    
                    C =  constC + (yintcpt* (yintcpt - precalc2));
		    
                    det = Math.sqrt((B * B) - ( 4 * A * C));
		    
                    solutionX = -B;
		    
                    //choose the positive or negative root depending
                    //on where the X coord lies with respect to the focus.
                    solutionX += (X < focusX)?-det:det;
		    
                    solutionX = solutionX / (2 * A);//divisor
		    
                    solutionY = (slope * solutionX) + yintcpt;
                }	                    	

                //calculate the square of the distance from the current point 
                //to the focus and the square of the distance from the 
                //intersection point to the focus. Want the squares so we can
                //do 1 square root after division instead of 2 before.
                deltaXSq = solutionX - focusX;
                deltaXSq = deltaXSq * deltaXSq;
        
                deltaYSq = solutionY - focusY;
                deltaYSq = deltaYSq * deltaYSq;
        
                intersectToFocusSq = deltaXSq + deltaYSq;
        
                deltaXSq = X - focusX;
                deltaXSq = deltaXSq * deltaXSq;
        
                deltaYSq = Y - focusY;
                deltaYSq = deltaYSq * deltaYSq;
        
                currentToFocusSq = deltaXSq + deltaYSq;
                g11 = Math.sqrt(currentToFocusSq / intersectToFocusSq);
                prevGs[i] = g11;

                //Get the color at this point
                pixels[indexer+i] = indexGradientAntiAlias
                    ((float)((g00+g01+g10+g11)/4), 
                     (float)Math.max(Math.abs(g11-g00),
                                     Math.abs(g10-g01)));

                X += a00; //incremental change in X, Y
                Y += a10;	
            } //end inner loop
            indexer += (w+adjust);
        } //end outer loop
    }
}
