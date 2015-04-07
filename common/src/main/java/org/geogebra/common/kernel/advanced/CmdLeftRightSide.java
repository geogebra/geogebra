package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.MyError;

/**
 * LeftSide[equation] RightSide[equation]
 * 
 * @author Zbynek Konecny
 *
 */
public class CmdLeftRightSide extends CommandProcessor {
	private boolean left;

	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param left
	 *            true for LeftSide, false for RightSide
	 */
	public CmdLeftRightSide(Kernel kernel, boolean left) {
		super(kernel);
		this.left = left;
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		GeoElement[] args = resArgs(c);
		if (args.length != 1)
			throw argNumErr(app, c.getName(), args.length);
		if (!args[0].isGeoImplicitPoly() && !(args[0] instanceof GeoConic)
				&& !(args[0] instanceof GeoLine))
			throw argErr(app, c.getName(), args[0]);

		AlgoLeftRightSide algo = new AlgoLeftRightSide(cons, c.getLabel(),
				args[0], left);

		return new GeoElement[] { algo.getResult() };
	}

}
