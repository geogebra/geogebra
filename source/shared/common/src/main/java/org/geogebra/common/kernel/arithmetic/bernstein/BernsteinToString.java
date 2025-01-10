package org.geogebra.common.kernel.arithmetic.bernstein;

import org.geogebra.common.util.StringUtil;

public class BernsteinToString {

	/**
	 * Serialize a one variable Bernstein polynomial.
	 *
	 * @param polynomial to serialize
	 * @return String representation of the polynomial
	 */
	public static String toString1Var(BernsteinPolynomial1Var polynomial) {
		if (polynomial.isConstant()) {
			return "" + (int) polynomial.bernsteinCoeffs[0];
		}
		StringBuilder sb = new StringBuilder();
		for (int i = polynomial.degree; i >= 0; i--) {
			double c = polynomial.bernsteinCoeffs[i];
			if (c == 0) {
				continue;
			}
			String fs = sb.length() == 0 && c > 0 ? ""
					: c > 0 ? "+ " : "- ";
			sb.append(fs);
			if (c != 1 && c != -1) {
				double abs = Math.abs(c);
				sb.append((abs - (int) abs) > 1E-6 ? abs : String.valueOf((int) abs));
			}
			String powerX = powerString(polynomial.variableName + "", i);
			String powerOneMinusX = powerString("(1 - " + polynomial.variableName + ")",
					polynomial.degree - i);
			sb.append(powerX);
			if (!powerX.isEmpty()) {
				sb.append(" ");
			}
			sb.append(powerOneMinusX);
			if (!powerOneMinusX.isEmpty()) {
				sb.append(" ");
			}
		}
		String trimmed = sb.toString().trim();
		return trimmed.isEmpty() ? "0" : trimmed;
	}

	static String powerString(String base, int i) {
		if (i == 0) {
			return "";
		}
		if (i == 1) {
			return base;
		}
		return base + StringUtil.numberToIndex(i);
	}

	/**
	 * Serialize a two variable Bernstein polynomial.
	 *
	 * @param polynomial to serialize
	 * @return String representation of the polynomial
	 */
	public static String toString2Var(BernsteinPolynomial2Var polynomial) {
		StringBuilder sb = new StringBuilder();
		for (int i = polynomial.degreeX; i >= 0; i--) {
			BernsteinPolynomial c = i < polynomial.bernsteinCoeffs.length
					? polynomial.bernsteinCoeffs[i]
					: null;
			if (c == null || "0".equals(c.toString())) {
				continue;
			}
			String fs = sb.length() == 0 ? "" : "+";
			sb.append(fs);
			if (polynomial.degreeX > 0 && !c.isConstant()) {
				sb.append(" (");
				sb.append(c);
				sb.append(") ");
			} else if (!"1".equals(c.toString())) {
				sb.append(c);
			} else {
				sb.append(" ");
			}

			String powerX = powerString("x", i);
			String powerOneMinusX = powerString("(1 - x)", polynomial.degreeX - i);
			sb.append(powerX);
			if (!powerX.isEmpty()) {
				sb.append(" ");
			}
			sb.append(powerOneMinusX);
			if (!powerOneMinusX.isEmpty()) {
				sb.append(" ");
			}
		}
		String trimmed = sb.toString().trim();
		return trimmed.isEmpty() ? "0" : trimmed;

	}
}
