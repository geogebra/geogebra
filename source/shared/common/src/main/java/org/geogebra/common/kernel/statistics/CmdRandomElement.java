package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.HasSymbolicMode;

/**
 * RandomElement[ &lt;List&gt; ]
 */
public class CmdRandomElement extends CmdOneListFunction {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRandomElement(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String label, GeoList list) {
		return doCommand(label, list, null);
	}

	@Override
	final protected GeoElement doCommand(String label, GeoList b, EvalInfo info) {
		AlgoRandomElement algo = new AlgoRandomElement(cons, b);
		GeoElement element = algo.getElement();
		initSymbolicMode(element, info);
		element.setLabel(label);
		return element;
	}

	private void initSymbolicMode(GeoElement element, EvalInfo info) {
		if (info != null && info.isSymbolic() && element instanceof HasSymbolicMode) {
			if (element.getDefinition() != null
					&& element.getDefinition().unwrap() instanceof MySpecialDouble) {
				return;
			}
			((HasSymbolicMode) element).setSymbolicMode(true, false);
		}
	}
}
