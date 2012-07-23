package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Move turtle forward
 * @author arno
 * TurtleForward[ <Turtle>, <distance> ]
 */
public class CmdTurtleForward extends CmdTurtleCommand {

	/**
	 * @param kernel the kernel
	 */
	public CmdTurtleForward(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(String cname, GeoElement[] args) throws MyError {
		getTurtle(args).forward(getNumArg(cname, args));
	}

}
