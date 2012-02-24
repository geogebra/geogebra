package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/**
 * PointList[ <List> ]
 */
public class CmdPointList extends CmdOneListFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdPointList(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.PointList(a, b);
	}


}
