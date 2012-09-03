package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Turn turtle anticlockwise
 * @author arno
 * TurtleLeft[ <Turtle>, <Angle in radians>]
 */
public class CmdTurtleLeft extends CmdTurtleCommand {

	/**
	 * @param kernel the kernel
	 */
	public CmdTurtleLeft(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(String cname, GeoElement[] args)
			throws MyError {
		getTurtle(args).turn(getNumArg(cname, args)*180/Math.PI);
	}

}
