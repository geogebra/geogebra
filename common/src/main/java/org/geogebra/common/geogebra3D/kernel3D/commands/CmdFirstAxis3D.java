package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAxisFirst3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoAxisFirst;
import org.geogebra.common.kernel.advanced.CmdFirstAxis;
import org.geogebra.common.kernel.kernelND.GeoConicND;

public class CmdFirstAxis3D extends CmdFirstAxis {

	public CmdFirstAxis3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected AlgoAxisFirst getAlgoAxisFirst(Construction cons, String label,
			GeoConicND geoConicND) {
		if (geoConicND instanceof GeoConic3D) {
			return new AlgoAxisFirst3D(cons, label, geoConicND);
		}
		return new AlgoAxisFirst(cons, label, geoConicND);

	}

}
