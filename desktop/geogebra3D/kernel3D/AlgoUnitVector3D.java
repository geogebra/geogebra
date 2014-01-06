package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoUnitVector;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

public class AlgoUnitVector3D extends AlgoUnitVector {

	public AlgoUnitVector3D(Construction cons, String label, GeoDirectionND line) {
		super(cons, label, (GeoElement) line);
	}
	
	public AlgoUnitVector3D(Construction cons, GeoDirectionND line) {
		super(cons, (GeoElement) line);
	}
	
	
	
	@Override
	protected GeoVectorND createVector(Construction cons){
    	GeoVector3D ret = new GeoVector3D(cons); 
    	return ret;
    }
	
    @Override
	public final void compute() { 

    	Coords coords = ((GeoDirectionND) inputGeo).getDirectionInD3();
        length = coords.norm();
        if (Kernel.isZero(length)){
        	u.setUndefined();
        }else{
        	((GeoVector3D) u).setCoords(coords.mul(1/length));  
        }
    } 
    
	@Override
	protected GeoPointND getInputStartPoint() {
		
		if (inputGeo.isGeoLine()){
			return ((GeoLineND) inputGeo).getStartPoint();
		}
		
		if (inputGeo.isGeoVector()){
			return ((GeoVectorND) inputGeo).getStartPoint();
		}
		
		return null; //TODO start point for GeoDirectionND
	}
	
	

}
