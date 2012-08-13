package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoDoubleListCovariance;
import geogebra.common.kernel.statistics.AlgoListCovariance;

/**
 * Covariance[ &lt;list1>, <&lt;list2> ]
 * Covariance[ &lt;List of Points> ]
 *
 */
public class CmdCovariance extends CmdOneOrTwoListsFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCovariance(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoListCovariance algo = new AlgoListCovariance(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		AlgoDoubleListCovariance algo = new AlgoDoubleListCovariance(cons,
				a, b, c);

		return algo.getResult();
	}


}
