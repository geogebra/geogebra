package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdTwoNumFunction;
import geogebra.common.kernel.geos.GeoElement;

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


	@Override
	protected GeoElement doCommand2(String a, NumberValue b, NumberValue c, NumberValue d)
	{
		AlgoRandomUniformList algo = new AlgoRandomUniformList(cons, a, b, c,d);
		return algo.getResult();
	}
}
