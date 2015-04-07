package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
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
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		if (c.getArgumentNumber() != 1)
			throw argNumErr(app, c.getName(), c.getArgumentNumber());
		GeoElement[] arg = resArgs(c);
		AlgoToComplexPolar algo = null;
		if (arg[0] instanceof GeoPoint)
			algo = new AlgoToComplexPolar(cons, c.getLabel(),
					(GeoPoint) arg[0], coordStyle);
		if (arg[0] instanceof GeoVector)
			algo = new AlgoToComplexPolar(cons, c.getLabel(),
					(GeoVector) arg[0], coordStyle);
		if (arg[0] instanceof GeoList)
			algo = new AlgoToComplexPolar(cons, c.getLabel(), (GeoList) arg[0],
					coordStyle);
		return new GeoElement[] { algo.getResult() };
	}

}
