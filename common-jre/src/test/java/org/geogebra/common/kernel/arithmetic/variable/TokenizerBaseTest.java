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
