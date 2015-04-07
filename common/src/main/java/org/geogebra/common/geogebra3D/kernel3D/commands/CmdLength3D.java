package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoLengthPoint3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdLength;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

public class CmdLength3D extends CmdLength {

	public CmdLength3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement length(String label, GeoVectorND v) {
		if (v.isGeoElement3D()) {
			return kernelA.getManager3D().Length(label, v);
		}

		return super.length(label, v);
	}

	@Override
	protected GeoElement length(String label, GeoPointND p) {
		AlgoLengthPoint3D algo = new AlgoLengthPoint3D(cons, label, p);

		return algo.getLength();
	}
}
