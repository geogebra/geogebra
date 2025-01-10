package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.main.MyError;

/**
 * @author arno Base class for turtle commands
 */
public abstract class CmdTurtleCommand extends CmdScripting {

	/**
	 * @param kernel
	 *            the kernel
	 */
	public CmdTurtleCommand(Kernel kernel) {
		super(kernel);
	}

	/**
	 * Return the turtle in the command
	 * 
	 * @param args
	 *            the resolved command args
	 * @return the first arg as a GeoTurtle
	 */
	@SuppressWarnings("static-method")
	protected final GeoTurtle getTurtle(GeoElement[] args) {
		return (GeoTurtle) args[0];
	}

	/**
	 * Return the second arg of the command as a double
	 * 
	 * @param c
	 *            the command
	 * @param args
	 *            the resolved args
	 * @return the second arg as a double
	 * @throws MyError
	 *             thrown can't be done
	 */
	protected final double getNumArg(Command c, GeoElement[] args)
			throws MyError {
		if (args.length != 2) {
			throw argNumErr(c);
		}
		if (!(args[1] instanceof NumberValue)) {
			throw argErr(c, args[1]);
		}
		return args[1].evaluateDouble();
	}

	/**
	 * Actually perform the command on the turtle
	 * 
	 * @param c
	 *            the command
	 * @param args
	 *            all the arguments (including the turtle)
	 * @throws MyError
	 *             possible error
	 */
	protected abstract void performTurtleCommand(Command c,
			GeoElement[] args) throws MyError;

	@Override
	public final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		if (n < 1) {
			throw argNumErr(c);
		}
		GeoElement[] args = resArgs(c);
		if (!args[0].isGeoTurtle()) {
			throw argErr(c, args[0]);
		}
		performTurtleCommand(c, args);
		return args;
	}
}
