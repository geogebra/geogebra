/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoEllipseFociPoint.java
 * 
 * Ellipse with Foci A and B passing through point C
 *
 * Michael Borcherds
 * 2008-04-06
 * adapted from EllipseFociLength
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author  Markus
 * @version 
 */
public abstract class AlgoEllipseFociPointND extends AlgoElement {

	protected GeoPointND A, B, C; // input    
    protected GeoConicND ellipse; // output             


    
    public AlgoEllipseFociPointND(
            Construction cons,
            String label,
            GeoPointND A,
            GeoPointND B,
            GeoPointND C, 
            GeoDirectionND orientation) {
        	this(cons, A, B, C, orientation);
            ellipse.setLabel(label);
        }
    

    public AlgoEllipseFociPointND(
            Construction cons,
            GeoPointND A,
            GeoPointND B,
            GeoPointND C, 
            GeoDirectionND orientation) {
            super(cons);
            this.A = A;
            this.B = B;
            this.C = C;
            setOrientation(orientation);
            ellipse = newGeoConic(cons);
            setInputOutput(); // for AlgoElement

            compute();
        	addIncidence();
        }
    
    /**
     * for 3D, set an orientation
     * @param orientation orientation
     */
	protected void setOrientation(GeoDirectionND orientation){
    	// not needed in 2D
    }
    
    /**
     * 
     * @param cons construction
     * @return new conic
     */
    abstract protected GeoConicND newGeoConic(Construction cons);

    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
	private void addIncidence() {
		if (C != null)
			C.addIncidence( ellipse);

	}

	@Override
	public Commands getClassName() {
		return Commands.Ellipse;
	}
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
    }
    
    
    /**
     * set the input
     */
	protected void setInput(){
    	input = new GeoElement[3];
        input[0] = (GeoElement) A;
        input[1] = (GeoElement) B;
        input[2] = (GeoElement) C;
    }
	
    // for AlgoElement
    @Override
    protected void setInputOutput() {
    	setInput();

    	super.setOutputLength(1);
        super.setOutput(0, ellipse);
        setDependencies(); // done by AlgoElement
    }

    public GeoConicND getEllipse() {
        return ellipse;
    }
    
    public GeoPointND getFocus1() {
    	// Public for LocusEqu
        return A;
    }
    public GeoPointND getFocus2() {
    	// Public for LocusEqu
        return B;
    }
    
    /**
     * Method for LocusEqu
     * @return returns external point for ellipse.
     */
    public GeoPointND getExternalPoint() {
    	return C;
    }

    // compute ellipse with foci A, B passing through C
    @Override
	public void compute() {
    	
		double xyA[] = new double[2];
		double xyB[] = new double[2];
		double xyC[] = new double[2];
		getA2d().getInhomCoords(xyA);
		getB2d().getInhomCoords(xyB);
		getC2d().getInhomCoords(xyC);
		
		double length = Math.sqrt((xyA[0]-xyC[0])*(xyA[0]-xyC[0])+(xyA[1]-xyC[1])*(xyA[1]-xyC[1])) +
		Math.sqrt((xyB[0]-xyC[0])*(xyB[0]-xyC[0])+(xyB[1]-xyC[1])*(xyB[1]-xyC[1]));
    	
        ellipse.setEllipseHyperbola(getA2d(), getB2d(), length/2);
    }
    
    /**
     * 
     * @return point A in 2D coords
     */
    abstract protected GeoPoint getA2d();
    
    /**
     * 
     * @return point B in 2D coords
     */
    abstract protected GeoPoint getB2d();
    
    /**
     * 
     * @return point C in 2D coords
     */
    abstract protected GeoPoint getC2d();
    


	

    @Override
    public String toString(StringTemplate tpl) {
    	return loc.getPlain("EllipseWithFociABPassingThroughC",A.getLabel(tpl),
    			B.getLabel(tpl),C.getLabel(tpl));
    }


    
}
