package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;

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
