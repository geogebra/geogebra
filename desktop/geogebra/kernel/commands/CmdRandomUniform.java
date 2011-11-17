package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;

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
