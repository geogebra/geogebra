package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdTwoNumFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * RandomUniform[ &lt;Number&gt;, &lt;Number&gt; ]
 */
public class CmdRandomUniform extends CmdTwoNumFunction {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRandomUniform(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String a, GeoNumberValue b,
			GeoNumberValue c) {
		AlgoRandomUniform algo = new AlgoRandomUniform(cons, a, b, c);
		return algo.getResult();
	}

	@Override
	protected GeoElement doCommand2(Command a, GeoNumberValue b,
			GeoNumberValue c, GeoNumberValue d) {
		AlgoRandomUniformList algo = new AlgoRandomUniformList(cons,
				a.getLabel(), b, c, d);
		return algo.getResult();
	}
}
