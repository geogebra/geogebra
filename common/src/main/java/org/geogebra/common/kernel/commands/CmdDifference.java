package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.MyError;

/**
 * Difference[<polygon>,<polygon>]
 * 
 * @author thilina
 *
 */
public class CmdDifference extends CommandProcessor {

	/**
	 * 
	 * @param kernel
	 */
	public CmdDifference(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int argumentNo = c.getArgumentNumber();
		boolean[] ok = { false, false };
		GeoElement[] arg = resArgs(c);

		switch (argumentNo) {
		case 2:
			if ((ok[0] = arg[0] instanceof GeoPolygon)
					&& (ok[1] = arg[1] instanceof GeoPolygon)) {
				GeoElement[] result = getAlgoDispatcher()
						.Difference(c.getLabels(), (GeoPolygon) arg[0],
								(GeoPolygon) arg[1]);
				return result;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		default:
			throw argNumErr(app, c.getName(), argumentNo);
		}
	}

}
