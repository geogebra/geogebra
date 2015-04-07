package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.main.MyError;

/**
 * Turtle Creation command Syntax: Turtle[]
 * 
 * @author arno
 *
 */
public class CmdTurtle extends CommandProcessor {

	/**
	 * Constructor
	 * 
	 * @param kernel
	 *            the kernel
	 */
	public CmdTurtle(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		if (n > 0) {
			throw argNumErr(app, c.getName(), n);
		}
		AlgoTurtle algo = new AlgoTurtle(cons, c.getLabel());
		GeoTurtle turtle = algo.getTurtle();

		turtle.setAnimating(true);
		GeoElement[] ret = { turtle };
		return ret;
	}

}
