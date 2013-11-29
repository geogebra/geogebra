package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoTranslateVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Vector w = v starting at A
 * @author mathieu
 *
 */
public class AlgoTranslateVector3D extends AlgoTranslateVector{

	/**
	 * Constructor
	 * @param cons construction
	 * @param label label
	 * @param v input vector
	 * @param A starting point
	 */
	public AlgoTranslateVector3D(Construction cons, String label, GeoVectorND v, GeoPointND A) {
		super(cons, label, v, A);
	}
	

    @Override
	protected GeoVectorND newGeoVector(Construction cons1){
    	return new GeoVector3D(cons1);  
    }
    
    @Override
	public void compute() {
        ((GeoVector3D) w).setCoords(v.getCoordsInD(3));        
    }      

}
