package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Presses the pen down.
 * @author judit
 * TurtleUp[ <Turtle>]
 */
public class CmdTurtleDown extends CmdTurtleCommand {

	/**
	 * @param kernel the kernel
	 */
	public CmdTurtleDown(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(String cname, GeoElement[] args)
			throws MyError {
		getTurtle(args).setPenDown(true);
	}

}
