package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgorithmSet.AlgorithmSetIterator;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry.AlgoDependentArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;

/**
 * Replaces references to removed algos in arbitrary constant after XML reload
 */
public class ArbconstAlgoFixer implements Inspecting {

	@Override
	public boolean check(ExpressionValue ev) {
		if (ev instanceof GeoNumeric) {
			GeoNumeric num = (GeoNumeric) ev;
			AlgorithmSetIterator it = num.getAlgoUpdateSet().getIterator();
			while (it.hasNext()) {
				AlgoElement el = it.next();
				if (el instanceof AlgoDependentArbitraryConstant) {
					((AlgoDependentArbitraryConstant) el).replaceOutCE();
				}
			}
		}
		return false;
	}

}
