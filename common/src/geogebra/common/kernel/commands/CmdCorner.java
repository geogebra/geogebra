package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;

/**
 * Corner[ <Image>, <number> ]
 */
public class CmdCorner extends CmdVertex {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCorner(Kernel kernel) {
		super(kernel);
	}

}
