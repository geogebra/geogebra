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

package org.geogebra.common.cas;

import org.geogebra.common.cas.error.TimeoutException;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.plugin.Operation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Exact level matching for finite rational cumulative-binomial comparisons with a real count argument. */
public final class BinomialStepSolver {
	// Bound the number of exact CDF evaluations; larger problems keep the existing CAS path.
	private static final int MAX_TRIALS = 100;
	private static final String INDEX = "ggbcdfstepindex";

	private BinomialStepSolver() {}

	/**
	 * Solves a single CDF comparison using the floor boundaries of symbolic binomial sums.
	 * The original definition is needed because function expansion removes the BinomialDist command.
	 * @param kernel owning kernel
	 * @param original original Solve/NSolve definition
	 * @param evaluated expanded command, including any auto-completed solve variable
	 * @return real intervals, ? for an unattainable target, or null if this path does not apply
	 */
	public static @Nullable String trySolve(
			@NonNull Kernel kernel, @NonNull ExpressionValue original, @NonNull Command evaluated) {
		if (!isSolve(evaluated)) {
			return null;
		}
		ExpressionValue definition = definition(original);
		if (!(definition instanceof Command command)
				|| !isSolve(command)
				|| command.getArgumentNumber() < 1
				|| command.getArgumentNumber() > 2) {
			return null;
		}
		Comparison comparison = comparison(kernel, definition(command.getArgument(0)));
		if (comparison == null
				|| !matchesVariable(evaluated, comparison.cdf.variable)
				|| !isNumeric(comparison.target)) {
			return null;
		}
		Cdf cdf = comparison.cdf;
		ExpressionValue target = comparison.target;
		ExpressionValue trials = numericValue(cdf.trials);
		ExpressionValue probability = numericValue(cdf.probability);
		if (!isNumeric(trials) || !isNumeric(probability)) {
			return null;
		}
		double n = trials.evaluateDouble();
		double p = probability.evaluateDouble();
		if (n < 0
				|| n > MAX_TRIALS
				|| n != Math.rint(n)
				|| p < 0
				|| p > 1
				|| !(kernel.getGeoGebraCAS().getCurrentCAS() instanceof CASgiac cas)) {
			return null;
		}
		int count = (int) n;
		String prob = "(" + probability.wrap().getCASstring(StringTemplate.giacTemplate, false) + ")";
		String goal = "(" + target.wrap().getCASstring(StringTemplate.giacTemplate, false) + ")";
		// Giac's raw CDF rejects negative counts and p=0/1; supply those levels explicitly.
		String level = "when(" + prob + "==0,1,when(" + prob + "==1,when(" + INDEX + "<" + count
				+ ",0,1),binomial_cdf(" + count + "," + prob + "," + INDEX + ")))";
		String operator =
				switch (comparison.operation) {
					case LESS -> "<";
					case LESS_EQUAL -> "<=";
					case GREATER -> ">";
					case GREATER_EQUAL -> ">=";
					default -> "==";
				};
		String levels = "[when(normal(0-" + goal + ")" + operator + "0,1,0),op(seq(when(normal(" + level
				+ "-" + goal + ")" + operator + "0,1,0)," + INDEX + ",0," + count + "))]";
		// A numerical approximation of an irrational CDF is not proof of exact equality.
		String rational = "(type(" + prob + ")==DOM_INT||type(" + prob + ")==DOM_RAT)" + "&&(type("
				+ goal + ")==DOM_INT||type(" + goal + ")==DOM_RAT)";
		String query = "when(" + rational + "," + levels + ",undef)";
		CasEvaluation result;
		try {
			result = cas.evaluateCASResult(query);
		} catch (TimeoutException error) {
			return "?";
		}
		String value = result.getValue();
		if (result.getFailure() != CasEvaluation.Failure.NONE || value == null) {
			return null;
		}
		boolean[] matches = matches(value, count + 2);
		boolean equality = comparison.operation == Operation.EQUAL_BOOLEAN;
		if (matches == null || (equality && matches[0] && matches[count + 1])) {
			return null;
		}
		return intervals(cdf.variable, matches, equality);
	}

	private static @Nullable Comparison comparison(Kernel kernel, ExpressionValue input) {
		ExpressionValue left = input;
		ExpressionValue right = new MyDouble(kernel, 0);
		Operation operation = Operation.EQUAL_BOOLEAN;
		if (input instanceof Equation equation) {
			left = equation.getLHS();
			right = equation.getRHS();
		} else if (input instanceof ExpressionNode node
				&& (node.getOperation().isInequality() || node.getOperation() == Operation.EQUAL_BOOLEAN)) {
			left = node.getLeft();
			right = node.getRight();
			operation = node.getOperation();
		}
		if (!isNumeric(numericValue(right))) {
			ExpressionValue swap = left;
			left = right;
			right = swap;
			operation = operation.reverseLeftToRight();
		}
		return isolate(kernel, left, numericValue(right), operation, 0);
	}

	// Only peel numeric affine wrappers around one CDF; do not rewrite general equations.
	private static @Nullable Comparison isolate(
			Kernel kernel,
			ExpressionValue input,
			ExpressionValue target,
			Operation operation,
			int depth) {
		if (depth > 8 || !isNumeric(target)) {
			return null;
		}
		ExpressionValue value = definition(input);
		Cdf cdf = cdf(value, 0);
		if (cdf != null) {
			return new Comparison(cdf, target, operation);
		}
		if (!(value instanceof ExpressionNode node)) {
			return null;
		}
		ExpressionValue scalar = numericValue(node.getRight());
		ExpressionValue body = node.getLeft();
		boolean scalarOnLeft = false;
		if (!isNumeric(scalar)) {
			scalar = numericValue(node.getLeft());
			body = node.getRight();
			scalarOnLeft = true;
		}
		if (!isNumeric(scalar)) {
			return null;
		}
		ExpressionValue next;
		Operation nextOperation = operation;
		switch (node.getOperation()) {
			case PLUS:
				next = new ExpressionNode(kernel, target, Operation.MINUS, scalar);
				break;
			case MINUS:
				next = scalarOnLeft
						? new ExpressionNode(kernel, scalar, Operation.MINUS, target)
						: new ExpressionNode(kernel, target, Operation.PLUS, scalar);
				if (scalarOnLeft) {
					nextOperation = operation.reverseLeftToRight();
				}
				break;
			case MULTIPLY:
			case DIVIDE:
				if (scalar.evaluateDouble() == 0
						|| (scalarOnLeft && node.getOperation() == Operation.DIVIDE)) {
					return null;
				}
				next = new ExpressionNode(
						kernel,
						target,
						node.getOperation() == Operation.MULTIPLY ? Operation.DIVIDE : Operation.MULTIPLY,
						scalar);
				if (scalar.evaluateDouble() < 0) {
					nextOperation = operation.reverseLeftToRight();
				}
				break;
			default:
				return null;
		}
		return isolate(kernel, body, next, nextOperation, depth + 1);
	}

	private static boolean isSolve(Command command) {
		return Commands.Solve.name().equals(command.getName())
				|| Commands.NSolve.name().equals(command.getName());
	}

	private static ExpressionValue definition(ExpressionValue input) {
		// For a:=BinomialDist(...), b:=a, c:=b, definitions retain the alias chain.
		// Follow it to the original command/equation; cap traversal of unresolved chains.
		ExpressionValue value = input.unwrap();
		for (int depth = 0; depth < 8 && value instanceof GeoSymbolic symbolic; depth++) {
			value = symbolic.getDefinition().unwrap();
		}
		return value;
	}

	private static ExpressionValue numericValue(ExpressionValue input) {
		ExpressionValue value = input.unwrap();
		// Unlike definitions, numeric aliases (a=1/2, b=a, c=b) already hold the evaluated 1/2.
		if (value instanceof GeoSymbolic symbolic) {
			ExpressionValue symbolicValue = symbolic.getValue();
			if (symbolicValue != null) {
				return symbolicValue.unwrap();
			}
		}
		return value;
	}

	private static boolean isNumeric(ExpressionValue value) {
		return value.none(v -> variable(v) != null || v instanceof Command || v instanceof BooleanValue)
				&& Double.isFinite(value.evaluateDouble());
	}

	private static @Nullable String variable(ExpressionValue value) {
		ExpressionValue unwrapped = value.unwrap();
		return unwrapped instanceof FunctionVariable
						|| unwrapped instanceof GeoDummyVariable
						|| unwrapped instanceof Variable
				? unwrapped.toString(StringTemplate.defaultTemplate)
				: null;
	}

	private static boolean matchesVariable(Command command, String name) {
		if (command.getArgumentNumber() == 1) {
			return true;
		}
		if (command.getArgumentNumber() != 2) {
			return false;
		}
		ExpressionValue value = command.getArgument(1).unwrap();
		if (value instanceof MyList list && list.size() == 1) {
			value = list.get(0);
		}
		return name.equals(variable(value));
	}

	private static @Nullable Cdf cdf(ExpressionValue input, int depth) {
		if (depth > 8) {
			return null;
		}
		ExpressionValue value = definition(input);
		if (value instanceof Command command
				&& Commands.BinomialDist.name().equals(command.getName())
				&& command.getArgumentNumber() == 4
				&& command.getArgument(3).unwrap() instanceof BooleanValue cumulative
				&& cumulative.getBoolean()) {
			String name = variable(command.getArgument(2));
			return name == null ? null : new Cdf(command.getArgument(0), command.getArgument(1), name);
		}
		if (value instanceof ExpressionNode node
				&& node.getOperation() == Operation.FUNCTION
				&& node.getLeft().unwrap() instanceof GeoSymbolic function
				&& function.getFunctionVariables().length == 1) {
			String argument = variable(node.getRight());
			Cdf body = cdf(function.getDefinition(), depth + 1);
			String formal = variable(function.getFunctionVariables()[0]);
			if (argument != null && body != null && body.variable.equals(formal)) {
				return new Cdf(body.trials, body.probability, argument);
			}
		}
		return null;
	}

	private static boolean @Nullable [] matches(String value, int length) {
		String result = value.trim();
		if (!((result.startsWith("{") && result.endsWith("}"))
				|| (result.startsWith("[") && result.endsWith("]")))) {
			return null;
		}
		String[] values = result.substring(1, result.length() - 1).split(",");
		if (values.length != length) {
			return null;
		}
		boolean[] matches = new boolean[length];
		for (int i = 0; i < length; i++) {
			String entry = values[i].trim();
			if (!"0".equals(entry) && !"1".equals(entry)) {
				return null;
			}
			matches[i] = "1".equals(entry);
		}
		return matches;
	}

	private static String intervals(String variable, boolean[] matches, boolean equality) {
		StringBuilder result = new StringBuilder("{");
		for (int i = 0; i < matches.length; i++) {
			if (!matches[i]) {
				continue;
			}
			int start = i;
			while (i + 1 < matches.length && matches[i + 1]) {
				i++;
			}
			if (start == 0 && i == matches.length - 1) {
				return "{" + variable + " = " + variable + "}";
			}
			if (result.length() > 1) {
				result.append(',');
			}
			if (start > 0) {
				result.append(start - 1).append(" <= ");
			}
			result.append(variable);
			if (i < matches.length - 1) {
				result.append(" < ").append(i);
			}
		}
		// Preserve the existing undefined result when no CDF level matches the target.
		return result.length() == 1 && equality ? "?" : result.append('}').toString();
	}

	private static final class Comparison {
		private final Cdf cdf;
		private final ExpressionValue target;
		private final Operation operation;

		Comparison(Cdf cdf, ExpressionValue target, Operation operation) {
			this.cdf = cdf;
			this.target = target;
			this.operation = operation;
		}
	}

	private static final class Cdf {
		private final ExpressionValue trials;
		private final ExpressionValue probability;
		private final String variable;

		Cdf(ExpressionValue trials, ExpressionValue probability, String variable) {
			this.trials = trials;
			this.probability = probability;
			this.variable = variable;
		}
	}
}
