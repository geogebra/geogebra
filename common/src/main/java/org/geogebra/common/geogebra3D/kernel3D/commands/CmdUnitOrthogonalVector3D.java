package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.CmdUnitOrthogonalVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.main.MyError;

/**
 * UnitOrthogonalVector[ <GeoPlane3D> ]
 */
public class CmdUnitOrthogonalVector3D extends CmdUnitOrthogonalVector {

	public CmdUnitOrthogonalVector3D(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0] instanceof GeoCoordSys2D)) {
				GeoElement[] ret = { (GeoElement) ((Kernel) kernelA)
						.getManager3D().UnitOrthogonalVector3D(c.getLabel(),
								(GeoCoordSys2D) arg[0]) };
				return ret;
			}

		}

		return super.process(c);
	}

}
