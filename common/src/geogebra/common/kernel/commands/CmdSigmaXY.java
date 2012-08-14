package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoDoubleListSigmaXY;
import geogebra.common.kernel.statistics.AlgoListSigmaXY;
/**
 * SigmaXY[ list ]
 */
public class CmdSigmaXY extends CmdOneOrTwoListsFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSigmaXY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoListSigmaXY algo = new AlgoListSigmaXY(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		AlgoDoubleListSigmaXY algo = new AlgoDoubleListSigmaXY(cons, a,
				b, c);
		return algo.getResult();
	}


}
