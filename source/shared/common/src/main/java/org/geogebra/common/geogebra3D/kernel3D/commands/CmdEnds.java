package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricEnds;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Command to compute ends of a limited quadric (cone, cylinder, ...)
 * 
 * @author mathieu
 *
 */
public class CmdEnds extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdEnds(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof GeoQuadric3DLimited) {

				AlgoQuadricEnds algo = new AlgoQuadricEnds(cons, c.getLabels(),
						(GeoQuadric3DLimited) arg[0]);
				return algo.getSections();
			}

			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}

	}

}
