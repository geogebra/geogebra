package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;


/**
 * Same as Histogram, just right by default
 *
 */
public class CmdHistogramRight extends CmdHistogram {
	/**
	 * Create new command processor for right histogram
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHistogramRight(Kernel kernel){
		super(kernel,true);
	}
}
