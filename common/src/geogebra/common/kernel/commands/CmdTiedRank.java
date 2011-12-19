package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/*
 * Rank[ <List> ]
 */
public class CmdTiedRank extends CmdOneListFunction {

	public CmdTiedRank(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.TiedRank(a, b);
	}


}
