package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

/*
 * RandomNormal[ <Number>, <Number> ]
 */
public class CmdRandomNormal extends CmdTwoNumFunction {

	public CmdRandomNormal(AbstractKernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernelA.RandomNormal(a, b, c);
	}

}
