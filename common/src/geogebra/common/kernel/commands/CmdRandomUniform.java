package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.statistics.AlgoRandomUniform;

/**
 * RandomUniform[ <Number>, <Number> ]
 */
public class CmdRandomUniform extends CmdTwoNumFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRandomUniform(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		AlgoRandomUniform algo = new AlgoRandomUniform(cons, a, b, c);
		return algo.getResult();
	}

}
