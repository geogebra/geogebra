package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/**
 * @author Kamalaruban Parameswaran
 * @version 2012-03-06
 */
public class CmdMeanGrouped extends CmdOneOrTwoListsFunction {

	public CmdMeanGrouped(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Mean(a, b);
	}

	@Override
	protected GeoElement doCommand(String a, GeoList b, GeoList c) {
		// TODO Auto-generated method stub
		return kernelA.Mean_Grouped(a, b, c); 
	}

}
