package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoUnitVector;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoVectorND;

public abstract class AlgoUnitVector3D extends AlgoUnitVector {

	public AlgoUnitVector3D(Construction cons, String label, GeoElement line) {
		super(cons, label, line);
	}
	
	@Override
	protected GeoVectorND createVector(Construction cons){
    	GeoVector3D ret = new GeoVector3D(cons); 
    	return ret;
    }
	
    @Override
	public final void compute() { 

    	Coords coords = getCoords();
        length = coords.norm();
        if (Kernel.isZero(length)){
        	u.setUndefined();
        }else{
        	((GeoVector3D) u).setCoords(coords.mul(1/length));  
        }
    } 
    
    /**
     * 
     * @return coords to compute v
     */
    protected abstract Coords getCoords();
    
	
	

}
