package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;
/**
 * SpanningTree[ &lt;List of Points> ]
 *
 */
public class CmdMinimumSpanningTree extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMinimumSpanningTree(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.MinimumSpanningTree(a, b);
	}

}
