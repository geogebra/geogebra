/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngleVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

public class AlgoAngleVectors extends AlgoAngle {

	private GeoVectorND v, w; // input
    private GeoAngle angle; // output           

    public AlgoAngleVectors(
        Construction cons,
        String label,
        GeoVectorND v,
        GeoVectorND w) {
        super(cons);
        this.v = v;
        this.w = w;
        angle = newGeoAngle(cons);
        setInputOutput(); // for AlgoElement

        // compute angle
        compute();
        angle.setLabel(label);
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
        input[0] = (GeoElement) v;
        input[1] = (GeoElement) w;

        setOutputLength(1);
        setOutput(0,angle);
        setDependencies(); // done by AlgoElement
    }

    public GeoAngle getAngle() {
        return angle;
    }
    public GeoVectorND getv() {
        return v;
    }
    public GeoVectorND getw() {
        return w;
    }

    // calc angle between vectors v and w
    // angle in range [0, 2pi) 
    // use normalvector to 
    @Override
	public void compute() {    	    	    	
    	// |v| * |w| * sin(alpha) = det(v, w)
    	// cos(alpha) = v . w / (|v| * |w|)
    	// tan(alpha) = sin(alpha) / cos(alpha)
    	// => tan(alpha) = det(v, w) / v . w    	    	
    	double det = ((GeoVector) v).x * ((GeoVector) w).y - ((GeoVector) v).y * ((GeoVector) w).x;
    	double prod = ((GeoVector) v).x * ((GeoVector) w).x + ((GeoVector) v).y * ((GeoVector) w).y;    	    
    	double value = Math.atan2(det, prod);                  	    	
        angle.setValue(value);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return loc.getPlain("AngleBetweenAB",v.getLabel(tpl),w.getLabel(tpl));

    }
    

    

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec, DrawAngle drawable) {
		GeoPointND vertex = v.getStartPoint();
		if (centerIsNotDrawable(vertex)){
			return false;
		}

		
		Coords origin = drawable.getCoordsInView(vertex);
		if (!drawable.inView(origin)) {
			return false;
		}
		
		
		Coords direction = drawable.getCoordsInView(v.getCoordsInD(3));
		if (!drawable.inView(direction)) {
			return false;
		}

		// origin
		m[0] = origin.get()[0];
		m[1] = origin.get()[1];		

		// first vec
		firstVec[0] = direction.getX();
		firstVec[1] = direction.getY();
		
		return true;

	}

	
	@Override
	public boolean getCoordsInD3(Coords[] drawCoords){
		GeoPointND vertex = v.getStartPoint();
		if (centerIsNotDrawable(vertex)){
			return false;
		}			
			
		drawCoords[0] = vertex.getInhomCoordsInD(3);
		drawCoords[1] = v.getCoordsInD(3);
		drawCoords[2] = w.getCoordsInD(3);
		
		return true;
	}
	
	// TODO Consider locusequability
}
