package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Orthogonal[ &lt;GeoPoint3D&gt;, &lt;GeoCoordSys&gt; ]
 */
public class CmdOrthogonalPlane extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdOrthogonalPlane(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if (arg[0].isGeoPoint()) {
				if (arg[1] instanceof GeoLineND) {
					return new GeoElement[] { (GeoElement) kernel
							.getManager3D().orthogonalPlane3D(c.getLabel(),
									(GeoPointND) arg[0], (GeoLineND) arg[1]) };
				} else if (arg[1] instanceof GeoVectorND) {
					return new GeoElement[] { (GeoElement) kernel
							.getManager3D().orthogonalPlane3D(c.getLabel(),
									(GeoPointND) arg[0],
									(GeoVectorND) arg[1]) };
				} else {
					throw argErr(c, arg[1]);
				}
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}

	}

}
