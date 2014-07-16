/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoConicPartConicPointsND;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;



/**
 * Arc or sector defined by a conic, start- and end-point.
 */
public class AlgoConicPartConicPoints3D extends AlgoConicPartConicPointsND {
	
	
	// temp parameters
	private PathParameter paramP, paramQ;

    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CONIC_PART_ARC or 
     * GeoConicPart.CONIC_PART_ARC       
     */
    public AlgoConicPartConicPoints3D(Construction cons, String label,
    		GeoConicND circle, GeoPointND startPoint, GeoPointND endPoint,
    		int type) {
        super(cons, label, circle, startPoint, endPoint, type);

    }    	
    
    @Override
	protected void setTempValues(){
		paramP = new PathParameter();
		paramQ = new PathParameter();
    }
    

    @Override
	protected GeoConicND newGeoConicPart(Construction cons, int type){
    	if (conic.isGeoElement3D()){
    		return new GeoConicPart3D(cons, type);
    	}
    	
    	return super.newGeoConicPart(cons, type);
    }
    

    @Override
	protected void computeParemeters(){

    	CoordSys cs = conic.getCoordSys();
    	
		Coords p2d = startPoint.getInhomCoordsInD(3).projectPlane(cs.getMatrixOrthonormal())[1];
		p2d.setZ(1);
		conic.pointChanged(p2d, paramP);

		p2d = endPoint.getInhomCoordsInD(3).projectPlane(cs.getMatrixOrthonormal())[1];
		p2d.setZ(1);
		conic.pointChanged(p2d, paramQ);
    }
    
    @Override
	protected double getStartParameter(){
    	return paramP.t;
    }
    
    @Override
	protected double getEndParameter(){
    	return paramQ.t;
    }
    

    
}
