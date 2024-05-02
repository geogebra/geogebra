package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Turn turtle anticlockwise
 * 
 * TurtleLeft[ &lt;Turtle&gt;, &lt;Angle in radians&gt;]
 * 
 * @author arno
 */
public class CmdTurtleLeft extends CmdTurtleCommand {

	/**
	 * @param kernel
	 *            the kernel
	 */
	public CmdTurtleLeft(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(Command c, GeoElement[] args)
			throws MyError {
		getTurtle(args).turn(getNumArg(c, args) * 180 / Math.PI);
	}

}
