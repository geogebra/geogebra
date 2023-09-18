package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * MatrixRank[Matrix]
 * 
 * @author zbynek
 *
 */
public class CmdMatrixRank extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdMatrixRank(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		GeoElement[] args = resArgs(c);
		if (args.length != 1) {
			throw argNumErr(c);
		}
		if (!args[0].isGeoList()) {
			throw argErr(c, args[0]);
		}

		AlgoMatrixRank algo = new AlgoMatrixRank(cons, c.getLabel(),
				(GeoList) args[0]);

		return new GeoElement[] { algo.getResult() };
	}

}
