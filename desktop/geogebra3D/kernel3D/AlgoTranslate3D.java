package geogebra3D.kernel3D;

import geogebra.kernel.AlgoTranslate;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoVectorND;


public class AlgoTranslate3D extends AlgoTranslate {

	public AlgoTranslate3D(Construction cons, GeoElement in, GeoElement v) {
		super(cons, in, v);
	}
	
    protected Coords getVectorCoords(){
    	GeoVectorND vec = (GeoVectorND) v;
    	return vec.getCoordsInD(3);
    }
    
    protected GeoElement copy(GeoElement geo){
    	if (v.isGeoElement3D())
    		return ((Kernel3D) kernel).copy3D(geo);
    	else
    		return super.copy(geo);
    }
    
    protected GeoElement copyInternal(Construction cons, GeoElement geo){
    	if (v.isGeoElement3D())
    		return ((Kernel3D) kernel).copyInternal3D(cons,geo);
    	else
    		return super.copyInternal(cons,geo);
    }

}
