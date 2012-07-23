package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Turtle Creation command
 * Syntax:
 *     Turtle[]
 * @author arno
 *
 */
public class CmdTurtle extends CommandProcessor {

	/**
	 * Constructor
	 * @param kernel the kernel
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
		GeoElement[] ret = { kernelA.Turtle(c.getLabel()) };
		return ret;
	}

}
