package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

/*
 * RandomBinomial[ <Number>, <Number> ]
 */
public class CmdRandomBinomial extends CmdTwoNumFunction {

	public CmdRandomBinomial(AbstractKernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernelA.RandomBinomial(a, b, c);
	}

}
