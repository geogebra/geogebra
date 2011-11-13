package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;

/*
 * RandomBinomial[ <Number>, <Number> ]
 */
class CmdRandomBinomial extends CmdTwoNumFunction {

	public CmdRandomBinomial(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.RandomBinomial(a, b, c);
	}

}
