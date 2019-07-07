package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.variable.power.Base;
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

	ExpressionNode getXyzPiDegPower(Exponents exponents,
	                                     int degPower) {
		if (exponents.get(Base.pi) == 0 && degPower == 0) {
			return getXyzPowers(exponents);
		}
		return getXyzPowers(exponents)
				.multiply(piDegPowers(exponents.get(Base.pi), degPower));
	}

	ExpressionNode getXyzPowers(Exponents exponents) {
		return new ExpressionNode(kernel, new FunctionVariable(kernel, "x"))
				.power(new MyDouble(kernel, exponents.get(Base.x)))
				.multiplyR(new ExpressionNode(kernel,
						new FunctionVariable(kernel, "y"))
								.power(new MyDouble(kernel,
										exponents.get(Base.y))))
				.multiplyR(new ExpressionNode(kernel,
						new FunctionVariable(kernel, "z"))
								.power(new MyDouble(kernel,
										exponents.get(Base.z))))
				.multiplyR(new ExpressionNode(kernel,
						new FunctionVariable(kernel, "t"))
								.power(new MyDouble(kernel, exponents.get(Base.t))))
				.multiplyR(new ExpressionNode(kernel,
						new FunctionVariable(kernel, Unicode.theta_STRING))
								.power(new MyDouble(kernel,
										exponents.get(Base.theta))));
	}

	ExpressionNode piDegPowers(int piPower, int degPower) {
		ExpressionNode piExp = piPower > 0
				? new MySpecialDouble(kernel, Math.PI, Unicode.PI_STRING)
				.wrap().power(piPower)
				: null;
		ExpressionNode degExp = degPower > 0 ? new MyDouble(kernel, MyMath.DEG)
				.setAngle().wrap().power(degPower) : null;
		return degExp == null ? piExp
				: (piExp == null ? degExp : piExp.multiply(degExp));
	}
}
