package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;


/**
 * Center[ <GeoConic> ] Center[ <GeoPoint>, <GeoPoint> ]
 */
public class CmdCenter extends CmdMidpoint {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCenter(AbstractKernel kernel) {
		super(kernel);
	}
}
