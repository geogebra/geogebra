package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;

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
