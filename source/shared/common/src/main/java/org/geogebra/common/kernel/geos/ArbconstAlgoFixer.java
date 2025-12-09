/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
