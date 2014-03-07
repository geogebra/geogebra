/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;



public class AlgoAngleVector extends AlgoAngleVectorND {

    
    private double [] coords;

    public AlgoAngleVector(Construction cons, String label, GeoVec3D vec) {
        super(cons, label, vec);
    }

    
    public GeoVec3D getVec3D() {
    	return (GeoVec3D) vec;
    }
        
    @Override
	public final void compute() {  
    	if (coords == null){
    		coords = new double[2];
    	}
    	((GeoVec3D) vec).getInhomCoords(coords);
        angle.setValue(
        		Math.atan2(coords[1], coords[0])
			);
    }


	public boolean updateDrawInfo(double[] m, double[] firstVec, DrawAngle drawable) {
		if(vec.isGeoVector()){
			GeoPointND vertex = ((GeoVector)vec).getStartPoint();
			if (vertex != null)			
				vertex.getInhomCoords(m);
			return vertex!=null && vertex.isDefined() && !vertex.isInfinite();
		}
		m[0]=0;
		m[1]=0;	
		return vec.isDefined();		
	}
	
	
	@Override
	public boolean getCoordsInD3(Coords[] drawCoords){
		
		if(vec.isGeoVector()){
			GeoPointND vertex = ((GeoVector)vec).getStartPoint();
			if (centerIsNotDrawable(vertex)){
				return false;
			}
			drawCoords[0] = vertex.getInhomCoordsInD(3);
			drawCoords[2] = ((GeoVector)vec).getCoordsInD(3);
		}else{
			drawCoords[0] = Coords.O;
			drawCoords[2] = ((GeoPoint)vec).getCoordsInD(3);
			drawCoords[2].setW(0);
		}		
			
		drawCoords[1] = Coords.VX;
		
		return true;
	}

	// TODO Consider locusequability
}
