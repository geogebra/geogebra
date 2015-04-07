package org.geogebra.common.kernel.discrete;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
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
		
		AlgoMinimumSpanningTree algo = new AlgoMinimumSpanningTree(cons, a,
				b);
		return algo.getResult();

	}

}
