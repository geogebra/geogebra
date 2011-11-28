package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;

/*
 * Random[ <Number>, <Number> ]
 */
class CmdRandom extends CmdTwoNumFunction {

	public CmdRandom(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Random(a, b, c);
	}

}
