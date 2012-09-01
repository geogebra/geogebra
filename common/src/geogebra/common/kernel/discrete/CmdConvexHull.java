package geogebra.common.kernel.discrete;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneListFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
/**
 * ConvexHull[ &lt; List of Points> ]
 * @author Michael
 *
 */
public class CmdConvexHull extends CmdOneListFunction {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdConvexHull(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoConvexHull algo = new AlgoConvexHull(cons, a, b);
		return algo.getResult();
	}

}
