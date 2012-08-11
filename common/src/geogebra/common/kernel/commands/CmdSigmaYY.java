package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;

/**
 * SigmaYY[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
public class CmdSigmaYY extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSigmaYY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		App.debug("XX");
		return kernelA.SigmaYY(a, b);
	}

}
