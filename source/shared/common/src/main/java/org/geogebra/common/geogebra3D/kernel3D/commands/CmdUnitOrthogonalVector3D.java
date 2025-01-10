package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdUnitOrthogonalVector;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.main.MyError;

/**
 * UnitOrthogonalVector[ &lt;GeoPlane3D&gt; ]
 */
public class CmdUnitOrthogonalVector3D extends CmdUnitOrthogonalVector {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdUnitOrthogonalVector3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof GeoCoordSys2D) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.unitOrthogonalVector3D(c.getLabel(),
								(GeoCoordSys2D) arg[0]) };
				return ret;
			}

		}

		return super.process(c, info);
	}

}
