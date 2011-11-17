package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;

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
