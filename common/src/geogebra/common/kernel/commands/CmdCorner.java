package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;


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
	public CmdCorner(AbstractKernel kernel) {
		super(kernel);
	}

}
