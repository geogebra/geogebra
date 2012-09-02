package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneListFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Shuffle[ <List> ]
 */
public class CmdRandomElement extends CmdOneListFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRandomElement(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.RandomElement(a, b);
	}


}
