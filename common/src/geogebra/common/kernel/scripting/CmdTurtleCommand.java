package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTurtle;
import geogebra.common.main.MyError;

/**
 * @author arno
 * Base class for turtle commands
 */
public abstract class CmdTurtleCommand extends CmdScripting {

	/**
	 * @param kernel the kernel
	 */
	public CmdTurtleCommand(Kernel kernel) {
		super(kernel);
	}
	
	/**
	 * Return the turtle in the command
	 * @param args the resolved command args
	 * @return the first arg as a GeoTurtle
	 */
	@SuppressWarnings("static-method")
	protected final GeoTurtle getTurtle(GeoElement[] args) {
		return (GeoTurtle) args[0];
	}
	
	/**
	 * Return the second arg of the command as a double
	 * @param cname the command name
	 * @param args the resolved args
	 * @return the second arg as a double
	 * @throws MyError thrown can't be done
	 */
	protected final double getNumArg(String cname, GeoElement[] args) throws MyError {
		if (args.length != 2) {
			throw argNumErr(app, cname, args.length);
		}
		if (!args[1].isNumberValue()) {
			throw argErr(app, cname, args[1]);
		}
		return ((NumberValue) args[1]).getDouble();
	}
	
	/**
	 * Actually perform the command on the turtle
	 * @param cname the command name
	 * @param args all the arguments (including the turtle) 
	 * @throws MyError possible error
	 */
	protected abstract void performTurtleCommand(String cname, GeoElement[] args) throws MyError;
	
	@Override
	public final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		if (n < 1) {
			throw argNumErr(app, c.getName(), n);
		}
		GeoElement[] args = resArgs(c);
		if (!args[0].isGeoTurtle()) {
			throw argErr(app, c.getName(), args[0]);
		}
		performTurtleCommand(c.getName(), args);
	}
}
