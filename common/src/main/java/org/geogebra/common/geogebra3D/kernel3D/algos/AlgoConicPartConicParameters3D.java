package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoConicPartConicParameters;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;

public class AlgoConicPartConicParameters3D
		extends AlgoConicPartConicParameters {

	public AlgoConicPartConicParameters3D(Construction cons, String label,
			GeoConicND circle, GeoNumberValue startParameter,
			GeoNumberValue endParameter, int type) {
		super(cons, label, circle, startParameter, endParameter, type);
	}

	@Override
	protected GeoConicND newGeoConicPart(Construction cons1, int type1) {
		return new GeoConicPart3D(cons1, type1);
	}

}
