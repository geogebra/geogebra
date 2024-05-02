package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdTwoNumFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * RandomNormal[ &lt;Number&gt;, &lt;Number&gt; ]
 */
public class CmdRandomNormal extends CmdTwoNumFunction {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRandomNormal(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String a, GeoNumberValue b,
			GeoNumberValue c) {
		AlgoRandomNormal algo = new AlgoRandomNormal(cons, a, b, c);
		return algo.getResult();
	}

}
