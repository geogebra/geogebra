package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;
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
		return kernelA.DelauneyTriangulation(a, b);
	}

}
