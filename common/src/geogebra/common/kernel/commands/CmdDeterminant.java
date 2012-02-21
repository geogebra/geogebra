package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/*
 * Determinant[ <List> ]
 * Michael Borcherds 
 */
public class CmdDeterminant extends CmdOneListFunction {

	public CmdDeterminant(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Determinant(a, b);
	}

}
