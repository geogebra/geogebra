package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.discrete.AlgoMinimumSpanningTree;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
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
