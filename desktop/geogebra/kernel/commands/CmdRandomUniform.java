package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;

/*
 * RandomUniform[ <Number>, <Number> ]
 */
class CmdRandomUniform extends CmdTwoNumFunction {

	public CmdRandomUniform(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.RandomUniform(a, b, c);
	}

}
