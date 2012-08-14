package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.discrete.AlgoVoronoi;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
/**
 * Voronoi[<List of Points>]
 * @author Michael
 */
public class CmdVoronoi extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdVoronoi(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoVoronoi algo = new AlgoVoronoi(cons, a, b);
		return algo.getResult();
	}

}
