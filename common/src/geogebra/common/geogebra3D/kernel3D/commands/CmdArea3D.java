package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoAreaPoints3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoAreaPoints;
import geogebra.common.kernel.commands.CmdArea;
import geogebra.common.kernel.kernelND.GeoPointND;

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
