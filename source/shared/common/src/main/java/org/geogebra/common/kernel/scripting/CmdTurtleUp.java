package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Lifts up the pen.
 * 
 * TurtleUp[ &lt;Turtle&gt;]
 * 
 * @author judit
 */
public class CmdTurtleUp extends CmdTurtleCommand {

	/**
	 * @param kernel
	 *            the kernel
	 */
	public CmdTurtleUp(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(Command c, GeoElement[] args)
			throws MyError {
		getTurtle(args).setPenDown(false);
	}

}
