package geogebra.kernel.commands;

import geogebra.kernel.Kernel;

/**
 * Center[ <GeoConic> ] Center[ <GeoPoint>, <GeoPoint> ]
 */
class CmdCenter extends CmdMidpoint {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCenter(Kernel kernel) {
		super(kernel);
	}
}
