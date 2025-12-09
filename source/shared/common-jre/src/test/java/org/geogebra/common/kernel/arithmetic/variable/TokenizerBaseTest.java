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

package org.geogebra.common.kernel.arithmetic.variable;

import java.util.ArrayList;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;

public class TokenizerBaseTest extends BaseUnitTest {
	protected void withGeos(String... labels) {
		ArrayList<String> varNames = new ArrayList<>();
		for (String label: labels) {
			GeoElement geo = add(label + "=?");

			if (geo instanceof CasEvaluableFunction) {
				for (FunctionVariable var : ((CasEvaluableFunction) geo).getFunctionVariables()) {
					varNames.add(var.getSetVarString());
				}
			}
		}
		for (String varName : varNames) {
			getKernel().getConstruction().registerFunctionVariable(varName);
		}
	}
}
