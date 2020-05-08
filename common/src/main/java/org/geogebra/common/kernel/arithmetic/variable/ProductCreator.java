package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.variable.power.Exponents;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.MyMath;

import com.himamis.retex.editor.share.util.Unicode;

class ProductCreator {

	private Kernel kernel;

	ProductCreator(Kernel kernel) {
		this.kernel = kernel;
	}

	ExpressionNode getProduct(String label) {
		int length = label.length();
		if (length == 3 && label.charAt(2) == '\'') {
			return product(label.charAt(0) + "",
					label.charAt(1) + "'");

		} else if (length == 3 && label.charAt(1) == '\'') {
			return product(label.charAt(0) + "'",
					label.charAt(2) + "");

		} else if (length == 4 && label.charAt(1) == '\''
				&& label.charAt(3) == '\'') {
			return product(label.charAt(0) + "'",
					label.charAt(2) + "'");

		} else if (length == 2) {
			return product(label.charAt(0) + "",
					label.charAt(1) + "");

		}
		return null;
	}

	private ExpressionNode product(String string, String string2) {
		GeoElement el1 = kernel.lookupLabel(string);
		GeoElement el2 = kernel.lookupLabel(string2);
		if (el1 != null && el2 != null && el1.isNumberValue()
				&& el2.isNumberValue()) {
			return el1.wrap().multiplyR(el2);
		}
		return null;
	}

	ValidExpression getFunctionVariablePowers(Exponents exponents) {
		return variablePower("x", exponents)
				.multiplyR(variablePower("y", exponents))
				.multiplyR(variablePower("z", exponents))
				.multiplyR(variablePower("t", exponents))
				.multiplyR(variablePower(Unicode.theta_STRING, exponents));
	}

	private ExpressionNode variablePower(String name, Exponents exponents) {
		return new FunctionVariable(kernel, name)
				.wrap().power(exponents.get(name));
	}

	ExpressionNode piDegPowers(ExpressionNode base, int piPower, int degPower) {
		ExpressionNode piExpTimesBase = piPower > 0
				? new MySpecialDouble(kernel, Math.PI, Unicode.PI_STRING)
				.wrap().power(piPower).multiplyR(base)
				: base;
		if (degPower == 0) {
			return piExpTimesBase;
		}
		MyDouble degree = new MySpecialDouble(kernel, MyMath.DEG, Unicode.DEGREE_STRING)
				.setAngle();
		ValidExpression degExp = degree.wrap().power(degPower);
		return piExpTimesBase.multiplyR(degExp);
	}
}
