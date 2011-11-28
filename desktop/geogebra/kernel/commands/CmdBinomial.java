package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;

/*
 * Binomial[ <Number>, <Number> ]
 * Michael Borcherds 2008-04-12
 */
class CmdBinomial extends CmdTwoNumFunction {

	public CmdBinomial(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Binomial(a, b, c);
	}
}
