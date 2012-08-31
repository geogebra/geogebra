package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneOrTwoListsFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
/**
 * Spearman[list of points]
 * Spearman[list of numbers,list of numbers]
 *
 */

public class CmdSpearman extends CmdOneOrTwoListsFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSpearman(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoSpearman algo = new AlgoSpearman(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		AlgoSpearman algo = new AlgoSpearman(cons, a, b, c);
		return algo.getResult();
	}


}
