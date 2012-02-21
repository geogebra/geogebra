package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

public class CmdPMCC extends CmdOneOrTwoListsFunction {

	public CmdPMCC(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.PMCC(a, b);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernelA.PMCC(a, b, c);
	}


}
