package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

/*
 * RandomNormal[ <Number>, <Number> ]
 */
public class CmdRandomNormal extends CmdTwoNumFunction {

	public CmdRandomNormal(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernelA.RandomNormal(a, b, c);
	}

}
