package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Lifts up the pen.
 * @author judit
 * TurtleUp[ <Turtle>]
 */
public class CmdTurtleUp extends CmdTurtleCommand {

	/**
	 * @param kernel the kernel
	 */
	public CmdTurtleUp(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(String cname, GeoElement[] args)
			throws MyError {
		getTurtle(args).setPenDown(false);
	}

}
