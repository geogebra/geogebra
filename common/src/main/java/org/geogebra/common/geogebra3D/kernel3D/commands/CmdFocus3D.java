package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoFocus3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFocus;
import org.geogebra.common.kernel.commands.CmdFocus;
import org.geogebra.common.kernel.kernelND.GeoConicND;

public class CmdFocus3D extends CmdFocus {

	public CmdFocus3D(Kernel kernel) {
		super(kernel);
	}

	protected AlgoFocus newAlgoFocus(Construction cons, String[] labels,
			GeoConicND c) {

		if (c.isGeoElement3D()) {
			return new AlgoFocus3D(cons, labels, c);
		}

		return super.newAlgoFocus(cons, labels, c);
	}

}
