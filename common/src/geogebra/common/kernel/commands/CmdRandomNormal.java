package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.statistics.AlgoRandomNormal;

/**
 * RandomNormal[ <Number>, <Number> ]
 */
public class CmdRandomNormal extends CmdTwoNumFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRandomNormal(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		AlgoRandomNormal algo = new AlgoRandomNormal(cons, a, b, c);
		return algo.getResult();
	}

}
