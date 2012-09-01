package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.TransformShearOrStretch;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;

/**
 * Stretch[<Object>,<Line>,<Ratio>]
 */
public class CmdStretch extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStretch(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[1] instanceof GeoVector) {

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoList()) {

					ret = Stretch(label, arg[0], (GeoVec3D) arg[1],
							null);
					return ret;

				}
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[1]);
		case 3:
			arg = resArgs(c);

			if ((arg[1] instanceof GeoLine) && arg[2].isGeoNumeric()) {

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoList()) {

					ret = Stretch(label, arg[0], (GeoVec3D) arg[1],
							(GeoNumeric) arg[2]);
					return ret;

				}
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!(arg[1] instanceof GeoVec3D))
				throw argErr(app, c.getName(), arg[1]);
			throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	

	/**
	 * apply matrix Michael Borcherds 2010-05-27
	 */
	final private GeoElement[] Stretch(String label, GeoElement Q, GeoVec3D l,
			GeoNumeric num) {
		Transform t = new TransformShearOrStretch(cons, l, num, false);
		return t.transform(Q, label);
	}
}
