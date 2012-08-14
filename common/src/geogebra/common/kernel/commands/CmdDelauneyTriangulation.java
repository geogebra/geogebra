package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.discrete.AlgoDelauneyTriangulation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
/**
 * DelauneyTriangulation[&lt;List ofPoints> ]
 * @author Michael
 *
 */
public class CmdDelauneyTriangulation extends CmdOneListFunction {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDelauneyTriangulation(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		
		AlgoDelauneyTriangulation algo = new AlgoDelauneyTriangulation(cons,
				a, b);
		return algo.getResult();
	}

}
