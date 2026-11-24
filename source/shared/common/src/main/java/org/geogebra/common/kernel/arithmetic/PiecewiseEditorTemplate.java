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

package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.editor.share.catalog.Tag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Conversion between conditional expressions and the editor syntax for piecewise functions,
 * <code>$piecewise(expression 1, condition 1, expression 2, condition 2, ...)</code>, which
 * the formula editor shows as cases beneath a brace. The condition of the final case is
 * optional; without it that case covers everything that the others do not.
 */
public final class PiecewiseEditorTemplate {

	private static final String COMMAND_IF = "If";

	private PiecewiseEditorTemplate() {
		// utility class
	}

	/**
	 * @param functionName name of a function or command in the input
	 * @return whether the name stands for a piecewise function in editor syntax
	 */
	public static boolean isPiecewise(@NonNull String functionName) {
		return Tag.PIECEWISE.getKey().equals(functionName);
	}

	/**
	 * Serialize a conditional expression in editor syntax. Conditionals nested as the fallback
	 * of another conditional continue its list of cases, so that both
	 * <code>If(a, b, If(c, d, e))</code> and <code>If(a, b, c, d, e)</code> have three cases.
	 * @param expression expression
	 * @param tpl string template
	 * @param valueForm whether to substitute the values of variables
	 * @return expression in editor syntax, null if the expression is not conditional
	 */
	public static @Nullable String serialize(
			@NonNull ExpressionNode expression, @NonNull StringTemplate tpl, boolean valueForm) {
		List<ExpressionValue> zones = new ArrayList<>();
		if (!collectZones(expression, zones)) {
			return null;
		}
		StringBuilder sb = new StringBuilder(Tag.PIECEWISE.getKey());
		sb.append('(');
		for (int i = 0; i < zones.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(valueForm ? zones.get(i).toValueString(tpl) : zones.get(i).toString(tpl));
		}
		return sb.append(')').toString();
	}

	private static boolean collectZones(ExpressionNode expression, List<ExpressionValue> zones) {
		switch (expression.getOperation()) {
			case IF:
			case IF_SHORT:
				zones.add(expression.getRight());
				zones.add(expression.getLeft());
				return true;
			case IF_ELSE:
				MyNumberPair conditionAndValue = (MyNumberPair) expression.getLeft().unwrap();
				zones.add(conditionAndValue.getY());
				zones.add(conditionAndValue.getX());
				ExpressionNode fallback = expression.getRight().wrap();
				if (!fallback.isConditional() || !collectZones(fallback, zones)) {
					zones.add(fallback);
				}
				return true;
			case IF_LIST:
				MyList conditions = (MyList) expression.getLeft().unwrap();
				MyList values = (MyList) expression.getRight().unwrap();
				for (int i = 0; i < conditions.size(); i++) {
					zones.add(values.get(i));
					zones.add(conditions.get(i));
				}
				if (values.size() > conditions.size()) {
					zones.add(values.get(values.size() - 1));
				}
				return true;
			default:
				return false;
		}
	}

	/**
	 * Build the expression for a piecewise function entered in editor syntax. The result is
	 * the same as for the equivalent plain input: a single case is just its expression, or
	 * "expression, condition" if restricted; multiple cases become an If command.
	 * @param kernel kernel
	 * @param zones expression and condition of each case in turn
	 * @return expression
	 * @throws MyError if there are no zones
	 */
	public static @NonNull ExpressionNode parse(@NonNull Kernel kernel, @NonNull MyList zones) {
		int size = zones.size();
		if (size == 0) {
			throw new MyError(kernel.getLocalization(), MyError.Errors.InvalidInput);
		}
		if (size == 1) {
			return zones.get(0).wrap();
		}
		if (size == 2) {
			return new ExpressionNode(kernel, zones.get(1), Operation.IF_SHORT, zones.get(0));
		}
		Command command = new Command(kernel, COMMAND_IF, false);
		for (int i = 0; i + 1 < size; i += 2) {
			command.addArgument(zones.get(i + 1).wrap());
			command.addArgument(zones.get(i).wrap());
		}
		if (size % 2 != 0) {
			command.addArgument(zones.get(size - 1).wrap());
		}
		return new ExpressionNode(kernel, command);
	}
}
