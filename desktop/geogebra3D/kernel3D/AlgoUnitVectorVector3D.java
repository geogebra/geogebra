package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

public class AlgoUnitVectorVector3D extends AlgoUnitVector3D {

	public AlgoUnitVectorVector3D(Construction cons, String label, GeoVectorND v) {
		super(cons, label, (GeoElement) v);
	}
	
	
    @Override
    protected Coords getCoords() { 

    	return ((GeoVectorND) inputGeo).getCoordsInD(3);
    } 
    
	@Override
	protected GeoPointND getInputStartPoint() {
		return ((GeoVectorND) inputGeo).getStartPoint();
	}

	
	

}
