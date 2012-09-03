package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Make turtle move back
 * @author arno
 * TurtleBack[ <Turtle>, <distance> ]
 */
public class CmdTurtleBack extends CmdTurtleCommand {

	/**
	 * @param kernel the kernel
	 */
	public CmdTurtleBack(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(String cname, GeoElement[] args)
			throws MyError {
		getTurtle(args).forward(-getNumArg(cname, args));
	}

}
