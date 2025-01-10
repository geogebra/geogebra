package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * NormalQuantilePlot[ &lt;List of Numeric&gt; ]
 * @author G. Sturr
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
		AlgoNormalQuantilePlot algo = new AlgoNormalQuantilePlot(cons, a, b);
		return algo.getResult();
	}

}
