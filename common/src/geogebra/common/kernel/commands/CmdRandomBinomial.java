package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.statistics.AlgoRandomBinomial;

/**
 * RandomBinomial[ <Number>, <Number> ]
 */
public class CmdRandomBinomial extends CmdTwoNumFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRandomBinomial(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		AlgoRandomBinomial algo = new AlgoRandomBinomial(cons, a, b, c);
		return algo.getResult();
	}

}
