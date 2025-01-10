package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.main.MyError;

/**
 * ToComplex[Vector] ToComplex[List]
 *
 */
public class CmdToComplexPolar extends CommandProcessor {
	private int coordStyle;

	/**
	 * @param kernel
	 *            kernel
	 * @param coordStyle
	 *            Kernel.COORD_*
	 */
	public CmdToComplexPolar(Kernel kernel, int coordStyle) {
		super(kernel);
		this.coordStyle = coordStyle;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		if (c.getArgumentNumber() != 1) {
			throw argNumErr(c);
		}
		GeoElement[] arg = resArgs(c);
		AlgoToComplexPolar algo = null;
		if (arg[0] instanceof GeoPoint) {
			algo = new AlgoToComplexPolar(cons, (GeoPoint) arg[0], coordStyle);
		}
		if (arg[0] instanceof GeoVector) {
			algo = new AlgoToComplexPolar(cons, (GeoVector) arg[0], coordStyle);
		}
		if (arg[0] instanceof GeoList) {
			algo = new AlgoToComplexPolar(cons, (GeoList) arg[0], coordStyle);
		}
		if (arg[0] instanceof GeoNumberValue) {
			algo = new AlgoToComplexPolar(cons,
					(GeoNumberValue) arg[0], coordStyle);
		}
		if (algo == null) {
			throw argErr(arg[0], c);
		}
		algo.getResult().setLabel(c.getLabel());
		return new GeoElement[] { algo.getResult() };
	}

}
