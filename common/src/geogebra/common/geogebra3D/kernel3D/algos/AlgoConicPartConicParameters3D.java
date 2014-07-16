package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoConicPartConicParameters;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.GeoConicND;

public class AlgoConicPartConicParameters3D extends AlgoConicPartConicParameters{

	public AlgoConicPartConicParameters3D(Construction cons, String label,
			GeoConicND circle, NumberValue startParameter,
			NumberValue endParameter, int type) {
		super(cons, label, circle, startParameter, endParameter, type);
	}
	
    @Override
	protected GeoConicND newGeoConicPart(Construction cons, int type){
    	return  new GeoConicPart3D(cons, type);
    }

}
