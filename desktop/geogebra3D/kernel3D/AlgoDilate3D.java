package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoDilate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for dilate at 3D point
 * @author mathieu
 *
 */
public class AlgoDilate3D extends AlgoDilate {

	/**
	 * dilate at point
	 * @param cons construction
	 * @param A point dilated
	 * @param r factor
	 * @param S reference point
	 */
	public AlgoDilate3D(Construction cons, GeoElement A, NumberValue r,
			GeoPointND S) {
		super(cons, A, r, S);
	}
	

	
    
    @Override
	protected GeoElement copy(GeoElement geo){
    	//if (mirror.isGeoElement3D())
    		return ((Kernel3D) kernel).copy3D(geo);
		//return super.copy(geo);
    }
    
    @Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if((geo instanceof GeoFunction || geo instanceof GeoCurveCartesian) /*&& mirror.isGeoElement3D()*/)
			return new GeoCurveCartesian3D(cons);

		return super.getResultTemplate(geo);
	}
    
    @Override
	protected GeoElement copyInternal(Construction cons1, GeoElement geo){
    	//if (mirror.isGeoElement3D())
    		return ((Kernel3D) kernel).copyInternal3D(cons1,geo);
		//return super.copyInternal(cons,geo);
    }
    
	@Override
	protected void setOutGeo(){
    	if(inGeo instanceof GeoFunction /*&& mirror.isGeoElement3D()*/){
    		AlgoTransformation3D.toGeoCurveCartesian(kernel, (GeoFunction)inGeo, (GeoCurveCartesian3D)outGeo);
    	} else{   	
    		super.setOutGeo();
    	}
	}
	
    @Override
	protected Coords getPointCoords(){
    	return S.getInhomCoordsInD(3);
    }

}
