package geogebra.common.kernel.discrete;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneListFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
/**
 * TravelingSalesman[list of points]
 * @author Michael
 *
 */
public class CmdTravelingSalesman extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdTravelingSalesman(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoTravelingSalesman algo = new AlgoTravelingSalesman(cons, a,
				b);
		return algo.getResult();
	}

}
