package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;

/*
 * RandomNormal[ <Number>, <Number> ]
 */
class CmdRandomNormal extends CmdTwoNumFunction {

	public CmdRandomNormal(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.RandomNormal(a, b, c);
	}

}
