/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVec3D;



public class AlgoAngleVector extends AlgoElement {

    private GeoVec3D vec; // input
    private GeoAngle angle; // output          
    
    private double [] coords = new double[2];

    public AlgoAngleVector(Construction cons, String label, GeoVec3D vec) {
        super(cons);
        this.vec = vec;
        
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement                
        compute();
        angle.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoAngleVector;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGLE;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = vec;

        setOutputLength(1);
        setOutput(0,angle);
        setDependencies(); // done by AlgoElement
    }

    public GeoAngle getAngle() {
        return angle;
    }
    
    public GeoVec3D getVec3D() {
    	return vec;
    }
        
    @Override
	public final void compute() {  
    	vec.getInhomCoords(coords);
        angle.setValue(
        		Math.atan2(coords[1], coords[0])
			);
    }

    @Override
	public final String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("AngleOfA",vec.getLabel(tpl));

    }

	// TODO Consider locusequability
}
