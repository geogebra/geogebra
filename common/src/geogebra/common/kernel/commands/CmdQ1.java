package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoMedian;
import geogebra.common.kernel.statistics.AlgoQ1;

/**
 * Q1[ list ]
 * @author Michael Borcherds 
 * @version 2008-02-16
 */
public class CmdQ1 extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdQ1(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoQ1 algo = new AlgoQ1(cons, a, b);
		return algo.getQ1();
	}
	
	@Override
	final protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq)
	{
		AlgoQ1 algo = new AlgoQ1(cons, a, list, freq);
		return algo.getQ1();
	}

}
