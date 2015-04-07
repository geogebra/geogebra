package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoConicPartConicParameters;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;

public class AlgoConicPartConicParameters3D extends
		AlgoConicPartConicParameters {

	public AlgoConicPartConicParameters3D(Construction cons, String label,
			GeoConicND circle, NumberValue startParameter,
			NumberValue endParameter, int type) {
		super(cons, label, circle, startParameter, endParameter, type);
	}

	@Override
	protected GeoConicND newGeoConicPart(Construction cons, int type) {
		return new GeoConicPart3D(cons, type);
	}

}
