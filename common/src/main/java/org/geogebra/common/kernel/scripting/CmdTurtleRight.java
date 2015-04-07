package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Turn a turtle clockwise
 * 
 * @author arno TurtleRight[ <Turtle>, <Angle in radians> ]
 */
public class CmdTurtleRight extends CmdTurtleCommand {

	/**
	 * @param kernel
	 *            the kernel
	 */
	public CmdTurtleRight(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(String cname, GeoElement[] args)
			throws MyError {
		getTurtle(args).turn(-getNumArg(cname, args) * 180 / Math.PI);
	}

}
