package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.TransformShearOrStretch;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.main.MyError;

/**
 * Shear[&lt;Object&gt;, &lt;Line&gt;, &lt;Ratio&gt;]
 */
public class CmdShear extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShear(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 3:
			arg = resArgs(c, info);

			if ((arg[1] instanceof GeoVec3D) && arg[2].isGeoNumeric()) {

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoList()) {

					ret = shear(label, arg[0], (GeoVec3D) arg[1],
							(GeoNumeric) arg[2]);
					return ret;

				}
				throw argErr(c, arg[0]);
			}
			if (!(arg[1] instanceof GeoVec3D)) {
				throw argErr(c, arg[1]);
			}
			throw argErr(c, arg[2]);

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * shear
	 */
	final private GeoElement[] shear(String label, GeoElement Q, GeoVec3D l,
			GeoNumeric num) {
		Transform t = new TransformShearOrStretch(cons, l, num, true);
		return t.transform(Q, label);
	}
}
