package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

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
		return kernelA.Covariance(a, b);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernelA.Covariance(a, b, c);
	}


}
