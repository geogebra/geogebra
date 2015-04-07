package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Triangular[min,max,mode,value] Triangular[min,max,mode,value,cumulative]
 * Triangular[min,max,mode,x]
 */
public class CmdTriangular extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTriangular(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c2) throws MyError {
		int n = c2.getArgumentNumber();
		boolean ok, ok2 = true;
		GeoElement[] arg;

		BooleanValue cumulative = null; // default for n=3
		arg = resArgs(c2);

		switch (n) {
		case 5:
			if (arg[4].isGeoBoolean()) {
				cumulative = (BooleanValue) arg[4];
			} else
				throw argErr(app, c2.getName(), arg[4]);

			// fall through
		case 4:
			if ((ok = arg[0] instanceof GeoNumberValue)
					&& (ok2 = arg[1] instanceof GeoNumberValue)
					&& (arg[2] instanceof GeoNumberValue)) {
				if (arg[3].isGeoFunction()
						&& ((GeoFunction) arg[3]).toString(
								StringTemplate.defaultTemplate).equals("x")) {

					AlgoTriangularDF algo = new AlgoTriangularDF(cons,
							c2.getLabel(), (GeoNumberValue) arg[0],
							(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
							cumulative);
					return algo.getGeoElements();

				} else if (arg[3] instanceof GeoNumberValue) {
					AlgoTriangular algo = new AlgoTriangular(cons,
							c2.getLabel(), (GeoNumberValue) arg[0],
							(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
							(GeoNumberValue) arg[3]);
					return algo.getGeoElements();

				} else
					throw argErr(app, c2.getName(), arg[2]);
			}
			throw argErr(app, c2.getName(), !ok ? arg[0] : (ok2 ? arg[2]
					: arg[0]));

		default:
			throw argNumErr(app, c2.getName(), n);
		}
	}

}
