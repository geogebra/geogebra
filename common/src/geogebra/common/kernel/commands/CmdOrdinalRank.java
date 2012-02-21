package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/*
 * Rank[ <List> ]
 */
public class CmdOrdinalRank extends CmdOneListFunction {

	public CmdOrdinalRank(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.OrdinalRank(a, b);
	}


}
