package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.geos.Bounds;

public class ConditionalSerializer {

	private final Kernel kernel;
	private final VarString function;

	/**
	 * @param kernel kernel
	 * @param function function to be serialized
	 */
	public ConditionalSerializer(Kernel kernel, VarString function) {
		this.kernel = kernel;
		this.function = function;
	}

	/**
	 * @param cases function branches
	 * @param conditions conditions
	 * @param complete whether conditions cover all reals
	 * @param substituteNumbers whether to substitute numbers
	 * @param tpl string template
	 * @return latex representation of piecewise function
	 */
	public String appendConditionalLaTeX(ArrayList<ExpressionNode> cases,
		ArrayList<Bounds> conditions, boolean complete,
			boolean substituteNumbers, StringTemplate tpl) {
		StringBuilder sbLaTeX = new StringBuilder();
		FunctionVariable fv = function.getFunctionVariables()[0];
		int lastValid = conditions.size() - 1;
		while (lastValid >= 0 && !conditions.get(lastValid).isValid()) {
			lastValid--;
		}
		int firstValid = 0;
		while (firstValid < conditions.size()
				&& !conditions.get(firstValid).isValid()) {
			firstValid++;
		}
		if (firstValid > lastValid) {
			sbLaTeX.append('?');
			return sbLaTeX.toString();

		}
		if (firstValid == lastValid) {
			sbLaTeX.append(cases.get(firstValid)
					.toLaTeXString(!substituteNumbers, tpl));
			if (!complete) {

				sbLaTeX.append(", \\;\\;\\;\\; \\left(");
				sbLaTeX.append(conditions.get(firstValid).toLaTeXString(
						!substituteNumbers, fv.toString(tpl), tpl));
				sbLaTeX.append(" \\right)");

			}
			return sbLaTeX.toString();
		}
		sbLaTeX.append("\\left\\{\\begin{array}{ll} ");
		for (int i = firstValid; i <= lastValid; i++) {
			if (conditions.get(i).isValid()) {
				sbLaTeX.append(cases.get(i)
						.toLaTeXString(!substituteNumbers, tpl));
				sbLaTeX.append("& : ");
				if (i == cases.size() - 1 && complete) {
					sbLaTeX.append("\\text{");
					sbLaTeX.append(kernel.getLocalization().getMenu("otherwise"));
					sbLaTeX.append("}");
				} else {

					sbLaTeX.append(conditions.get(i).toLaTeXString(
							!substituteNumbers, fv.toString(tpl),
							tpl));
					if (i != lastValid) {
						sbLaTeX.append("\\\\ ");
					}
				}
			}
		}
		sbLaTeX.append(" \\end{array}\\right. ");
		return sbLaTeX.toString();
	}

	/**
	 * @param condition condition
	 * @param expression function definition in given interval/domain
	 * @param substituteNumbers whether to print child elements as values
	 * @return conditional LaTeX string
	 */
	public StringBuilder getSingleCondition(ExpressionValue condition,
			ExpressionValue expression, boolean substituteNumbers) {
		StringBuilder sbLaTeX = new StringBuilder();
		if (substituteNumbers) {
			sbLaTeX.append(expression
					.toValueString(StringTemplate.latexTemplate));
			sbLaTeX.append(", \\;\\;\\;\\; \\left(");
			sbLaTeX.append(condition
					.toValueString(StringTemplate.latexTemplate));
		} else {
			sbLaTeX.append(
					expression.toString(StringTemplate.latexTemplate));
			sbLaTeX.append(", \\;\\;\\;\\; \\left(");
			sbLaTeX.append(
					condition.toString(StringTemplate.latexTemplate));
		}

		sbLaTeX.append(" \\right)");
		return sbLaTeX;
	}
}
