package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Order determines order in Two Variable Regression Analysis menu For each
 * String, getMenu(s) must be defined
 *
 * @author Michael Borcherds
 */
public enum Regression {
	NONE("None", Commands.FitPoly, ""),
	LINEAR("Linear", Commands.FitPoly, "a\\ x + b"),
	LOG("Log", Commands.FitLog, "a + b\\cdot \\log(x)"),
	POLY("Polynomial", Commands.FitPoly, ""),
	POW("Power", Commands.FitPow, "a \\cdot x^b"),
	EXP("Exponential", Commands.FitExp, "a \\cdot e^{b\\ x}"),
	GROWTH("Growth", Commands.FitGrowth, "a \\cdot b^x"),
	SIN("Sin", Commands.FitSin, "a \\cdot \\sin(b\\ x + c) + d"),
	LOGISTIC("Logistic", Commands.FitLogistic, "\\frac{a}{1 + b\\cdot e^{c\\ x}}");

	private final Commands command;
	// getMenu(label) must be defined
	private final String label;
	private final String formula;

	/**
	 * @param label label
	 * @param command command
	 * @param formula generic formula
	 */
	Regression(String label, Commands command, String formula) {
		this.label = label;
		this.command = command;
		this.formula = formula;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * @param kernel kernel
	 * @param order degree of polynomial for polynomial regression
	 * @param list list of points
	 * @return command
	 */
	public Command buildCommand(Kernel kernel, int order, ExpressionValue list) {
		Command cmd = new Command(kernel, command.name(), false);
		cmd.addArgument(list.wrap());
		if (command == Commands.FitPoly) {
			int degree = this == POLY ? order : 1;
			cmd.addArgument(new GeoNumeric(kernel.getConstruction(), degree).wrap());
		}
		return cmd;
	}

	/**
	 * @param degree polynomial degree (for polynomial regression)
	 * @return formula
	 */
	public String getFormula(int degree) {
		if (this == POLY) {
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
		return formula;
	}
}
