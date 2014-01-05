package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoUnitVectorLine3D extends AlgoUnitVector3D {

	public AlgoUnitVectorLine3D(Construction cons, String label, GeoLineND line) {
		super(cons, label, (GeoElement) line);
	}
	
	
    @Override
    protected Coords getCoords() { 

    	return ((GeoLineND) inputGeo).getDirectionInD3();
    } 
    
	@Override
	protected GeoPointND getInputStartPoint() {
		return ((GeoLineND) inputGeo).getStartPoint();
	}

	
	

}
