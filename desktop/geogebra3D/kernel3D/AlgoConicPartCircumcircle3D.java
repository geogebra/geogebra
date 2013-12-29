/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoCircleThreePoints;
import geogebra.common.kernel.algos.AlgoConicPartCircumcircleND;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoConicPartND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Circle arc or sector defined by three points.
 */
public class AlgoConicPartCircumcircle3D extends AlgoConicPartCircumcircleND {

	private AlgoCircle3DThreePoints algo;

    public AlgoConicPartCircumcircle3D(Construction cons, String label,
    		GeoPointND A, GeoPointND B, GeoPointND C,
    		int type) 
    {
    	super(cons, label, A, B, C, type);
    	 
    }
    
    @Override
	protected AlgoCircleThreePoints getAlgo(){
    	algo = new AlgoCircle3DThreePoints(cons, A, B, C);
    	return algo;
    }
    
    @Override
	protected GeoConicPart3D createConicPart(Construction cons, int type){
    	return new GeoConicPart3D(cons, type);
    }

	
	


	
	@Override
	final public GeoPoint getA() {
		return algo.getPoint2D(0);
	}

	/**
	 * Method for LocusEqu.
	 * @return second point.
	 */
	@Override
	final public GeoPoint getB() {
		return algo.getPoint2D(1);
	}

	/**
	 * Method for LocusEqu.
	 * @return third point.
	 */
	@Override
	final public GeoPoint getC() {
		return algo.getPoint2D(2);
	}

	@Override
	public GeoConicPart3D getConicPart() {
        return (GeoConicPart3D) super.getConicPart();
    }

	
	@Override
	protected void setFromUndefinedCircle(){
		
		if (!A.isDefined() || !B.isDefined() || !C.isDefined()){
			conicPart.setUndefined();
			return;
		}
		
		Coords cA = A.getInhomCoordsInD(3);
		Coords cB = B.getInhomCoordsInD(3);
		Coords cC = C.getInhomCoordsInD(3);
		
		((GeoConicPart3D) conicPart).setFirstLine(cA, cC);
		
		if (cB.sub(cA).dotproduct(cB.sub(cC)) > 0) {
			//tell conicPart about this case: two rays
			((GeoConicPartND) conicPart).setParameters(0, 1, false);
			((GeoConicPart3D) conicPart).setSecondLineOrigin(cC);
		} else {
			// segment
			// tell conicPart about this case: one segment
			((GeoConicPartND) conicPart).setParameters(0, 1, true);
		}
		
		conicPart.setType(GeoConicNDConstants.CONIC_PARALLEL_LINES);
		
	}
}
