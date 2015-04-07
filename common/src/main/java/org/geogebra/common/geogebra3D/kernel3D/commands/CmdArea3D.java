package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAreaPoints3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAreaPoints;
import org.geogebra.common.kernel.commands.CmdArea;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class CmdArea3D extends CmdArea {

	public CmdArea3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected AlgoAreaPoints getAlgoAreaPoints(Construction cons, String label,
			GeoPointND[] points, boolean is3D) {
		if (is3D) {
			return new AlgoAreaPoints3D(cons, label, points);
		}
		return new AlgoAreaPoints(cons, label, points);
	}

}
