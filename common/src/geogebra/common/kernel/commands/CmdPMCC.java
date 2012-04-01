package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
/**
 * 
 * CorrelationCoefficient[&lt;List of points>]
 * CorrelationCoefficient[&lt;List of numbers>, &lt;List of numbers> ]
 *
 */
public class CmdPMCC extends CmdOneOrTwoListsFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdPMCC(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.PMCC(a, b);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernelA.PMCC(a, b, c);
	}


}
