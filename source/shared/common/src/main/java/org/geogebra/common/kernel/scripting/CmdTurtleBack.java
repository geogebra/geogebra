package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Make turtle move back
 * 
 * TurtleBack[ &lt;Turtle&gt;, &lt;distance&gt; ]
 * 
 * @author arno
 */
public class CmdTurtleBack extends CmdTurtleCommand {

	/**
	 * @param kernel
	 *            the kernel
	 */
	public CmdTurtleBack(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(Command c, GeoElement[] args)
			throws MyError {
		getTurtle(args).forward(-getNumArg(c, args));
	}

}
