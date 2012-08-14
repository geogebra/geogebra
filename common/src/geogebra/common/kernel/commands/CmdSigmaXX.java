package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoDoubleListSigmaXX;

/**
 * SigmaXX[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
public class CmdSigmaXX extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSigmaXX(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.SigmaXX(a, b);
	}
	
	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList list2) {
		AlgoDoubleListSigmaXX algo = new AlgoDoubleListSigmaXX(cons, a,
				list, list2);
		return algo.getResult();
	}
	
	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq, GeoBoolean isGrouped) {
		return kernelA.SigmaXX(a, list, freq, isGrouped);
	}
	
}
