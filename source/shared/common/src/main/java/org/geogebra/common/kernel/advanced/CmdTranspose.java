package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Transpose[ &lt;List&gt; ]
 * 
 * @author Michael Borcherds
 */
public class CmdTranspose extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTranspose(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String label, GeoList b) {
		AlgoTranspose algo = new AlgoTranspose(cons, b);
		algo.getResult().setLabel(label);
		return algo.getResult();
	}

}
