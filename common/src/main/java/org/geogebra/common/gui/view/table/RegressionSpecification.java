package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.statistics.Regression;

public final class RegressionSpecification {

	private final Regression regression;
	private final int degree;
	private final String label;
	private static ArrayList<RegressionSpecification> specs = new ArrayList<>();
	private final String formula;
	private final String coeffOrdering;

	private RegressionSpecification(Regression regression, int deg, String formula,
			String coeffOrdering) {
		this.regression = regression;
		this.degree = deg;
		this.label = deg > 1 ? getPolynomialLabel(degree) : regression.getLabel();
		this.formula = deg > 0 ? getFormula(degree) : formula;
		this.coeffOrdering = coeffOrdering;
	}

	private static String getPolynomialLabel(int degree) {
		switch (degree) {
		case 2: return "Quadratic";
		case 3: return "Cubic";
		default: return "Quartic";
		}
	}

	/**
	 * @param listSize size of selected list
	 * @return all available regression types
	 */
	public static List<RegressionSpecification> getForListSize(int listSize) {
		if (specs.isEmpty()) {
			addSpec(Regression.LINEAR, 1, null, "ba");
			addSpec(Regression.LOG, 0, "a + b\\cdot \\log(x)", "ab");
			addSpec(Regression.POW, 0, "a \\cdot x^b", "ab");
			addSpec(Regression.POLY, 2, null, "cba");
			addSpec(Regression.POLY, 3, null, "dcba");
			addSpec(Regression.POLY, 4, null, "edcba");
			addSpec(Regression.EXP, 0, "a \\cdot e^{b\\ x}", "ab");
			addSpec(Regression.GROWTH, 0, "a \\cdot b^x", "ab");
			addSpec(Regression.SIN, 0, "a \\cdot \\sin(b\\ x + c) + d", "dabc");
			addSpec(Regression.LOGISTIC, 0, "\\frac{a}{1 + b\\cdot e^{-c\\ x}}", "bac");
		}
		return specs.stream().filter(spec -> spec.coeffOrdering.length() <= listSize)
				.collect(Collectors.toList());
	}

	private static void addSpec(Regression linear, int i, String o, String ba) {
		specs.add(new RegressionSpecification(linear, i, o, ba));
	}

	public String getLabel() {
		return label;
	}

	/**
	 * @param kernel kernel
	 * @param points input data as a tuple (x-coordinates, y-coordinates)
	 * @return regression command
	 */
	public Command buildCommand(Kernel kernel, MyVecNode points) {
		Command cleanData = new Command(kernel, Commands.RemoveUndefined.getCommand(),
				false);
		cleanData.addArgument(points.wrap());
		return regression.buildCommand(kernel, degree, cleanData);
	}

	public String getFormula() {
		return formula;
	}

	/**
	 * @param degree polynomial degree (for polynomial regression)
	 * @return formula
	 */
	private static String getFormula(int degree) {
		StringBuilder sb = new StringBuilder();
		char coeffName = 'a';
		for (int i = degree; i >= 0; i--, coeffName++) {
			sb.append(coeffName);
			if (i == 1) {
				sb.append("\\ x+");
			} else if (i > 0) {
				sb.append("\\ x^{");
				sb.append(i);
				sb.append("}+");
			}
		}
		return sb.toString();
	}

	public String getCoeffOrdering() {
		return coeffOrdering;
	}
}
