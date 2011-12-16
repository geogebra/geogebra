package geogebra.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdTwoNumFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;

/*
 * Binomial[ <Number>, <Number> ]
 * Michael Borcherds 2008-04-12
 */
class CmdBinomial extends CmdTwoNumFunction {

	public CmdBinomial(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernelA.Binomial(a, b, c);
	}
}
