package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdOrthogonalVector;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.main.MyError;

/**
 * OrthogonalVector[ &lt;GeoPlane3D&gt; ]
 */
public class CmdOrthogonalVector3D extends CmdOrthogonalVector {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdOrthogonalVector3D(Kernel kernel) {
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
				GeoElement[] ret = {
						(GeoElement) kernel.getManager3D().orthogonalVector3D(
								c.getLabel(), (GeoCoordSys2D) arg[0]) };
				return ret;
			}
			break;

		case 2:
			arg = resArgs(c);
			if (arg[0] instanceof GeoLineND
					&& arg[1] instanceof GeoDirectionND) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.orthogonalVector3D(c.getLabel(), (GeoLineND) arg[0],
								(GeoDirectionND) arg[1]) };
				return ret;
			}
			break;

		}

		return super.process(c, info);
	}

}
