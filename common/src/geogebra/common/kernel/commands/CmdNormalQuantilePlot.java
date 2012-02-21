package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * NormalQuantilePlot[ <List of Numeric> ] G.Sturr 2011-6-29
 */
public class CmdNormalQuantilePlot extends CmdOneListFunction {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNormalQuantilePlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b) {
		return kernelA.NormalQuantilePlot(a, b);
	}

}
