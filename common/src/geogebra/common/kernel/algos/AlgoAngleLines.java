/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngleLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoLineND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAngleLines extends AlgoAngle  implements DrawInformationAlgo{

    private GeoLineND g, h; // input
    private GeoAngle angle; // output           

    /**
     * Creates new unlabeled angle between lines algo
     * @param cons construction
     * @param g first line
     * @param h second line
     */
    AlgoAngleLines(Construction cons,  GeoLineND g, GeoLineND h) {
        super(cons);
        this.g = g;
        this.h = h;
        angle = newGeoAngle(cons);
        setInputOutput(); // for AlgoElement

        // compute angle
        compute();
        
    }
    
	/**
	 * create a new GeoAngle with interval as default angle
	 * @param cons construction
	 * @return new GeoAngle
	 */
	protected GeoAngle newGeoAngle(Construction cons) {
		return GeoAngle.newAngleWithDefaultInterval(cons);
	}
    
    private AlgoAngleLines(GeoLineND g, GeoLineND h) {  
    	super(((GeoElement) g).getConstruction(), false);
        this.g = g;
        this.h = h;
   }
    
    /**
     * Creates new labeled angle between lines algo
     * @param cons construction
     * @param label angle label
     * @param g first line
     * @param h second line
     */
    
    public AlgoAngleLines(Construction cons, String label, GeoLineND g, GeoLineND h) {
        this(cons,g,h);
        angle.setLabel(label);
    }
    
    public AlgoAngleLines copy(){
    	return new AlgoAngleLines(g.copy(),h.copy());
    }

    @Override
	public Commands getClassName() {
        return Commands.Angle;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGLE;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) g;
        input[1] = (GeoElement) h;

        setOutputLength(1);
        setOutput(0,angle);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the resulting angle
     * @return resulting angle
     */
    public GeoAngle getAngle() {
        return angle;
    }
    
    /**
     * Returns the first line
     * @return first line
     */
    public GeoLineND getg() {
        return g;
    }
    
    /**
     * Returns the second line
     * @return second line
     */
    public GeoLineND geth() {
        return h;
    }

    // calc angle between lines g and h
    // use normalvectors (gx, gy), (hx, hy)
    @Override
	public void compute() {
     	// |v| * |w| * sin(alpha) = det(v, w)
    	// cos(alpha) = v . w / (|v| * |w|)
    	// tan(alpha) = sin(alpha) / cos(alpha)
    	// => tan(alpha) = det(v, w) / v . w    	    	
    	double det = ((GeoLine) g).x * ((GeoLine) h).y - ((GeoLine) g).y * ((GeoLine) h).x;
    	double prod = ((GeoLine) g).x * ((GeoLine) h).x + ((GeoLine) g).y * ((GeoLine) h).y;    	    
    	double value = Math.atan2(det, prod);                  	    	
        
        angle.setValue(value);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return loc.getPlain("AngleBetweenAB",g.getLabel(tpl),h.getLabel(tpl));

    }

	public boolean updateDrawInfo(double[] m, double[] firstVec, DrawAngle drawable) {
		double[] n = GeoVec3D.cross((GeoLine) g, (GeoLine) h).get();
		m[0] = n[0] / n[2];
		m[1] = n[1] / n[2];

		// first vec
		((GeoLine) g).getDirection(firstVec);
		return true;
	}

	// TODO Consider locusequability
}
