package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.statistics.AlgoDoubleListSigmaXX;
import geogebra.common.kernel.statistics.AlgoListSigmaXX;
import geogebra.common.kernel.statistics.AlgoSigmaXX;

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
	final protected GeoElement doCommand(String label, GeoList list)
	{
		GeoNumeric num;
		GeoElement geo = list.get(0);
		if (geo.isNumberValue()) { // list of numbers
			AlgoSigmaXX algo = new AlgoSigmaXX(cons, label, list);
			num = algo.getResult();
		} else { // (probably) list of points
			AlgoListSigmaXX algo = new AlgoListSigmaXX(cons, label, list);
			num = algo.getResult();
		}
		return num;
	}
	
	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList list2) {
		AlgoDoubleListSigmaXX algo = new AlgoDoubleListSigmaXX(cons, a,
				list, list2);
		return algo.getResult();
	}
	
	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq, GeoBoolean isGrouped) {
		AlgoSigmaXX algo = new AlgoSigmaXX(cons, a, list, freq, isGrouped);
		return algo.getResult();
	}
	
}
