package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

public class CmdSpearman extends CmdOneOrTwoListsFunction {

	public CmdSpearman(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Spearman(a, b);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernelA.Spearman(a, b, c);
	}


}
