package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoQ3;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdOneListFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Q3[ list ]
 * @author Michael Borcherds 
 * @version 2008-02-16
 */
public class CmdQ3 extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdQ3(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoQ3 algo = new AlgoQ3(cons, a, b);
		return algo.getQ3();
	}

	@Override
	final protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq)
	{
		AlgoQ3 algo = new AlgoQ3(cons, a, list, freq);
		return algo.getQ3();
	}

}
