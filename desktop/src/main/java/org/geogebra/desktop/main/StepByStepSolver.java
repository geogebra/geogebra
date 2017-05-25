package org.geogebra.desktop.main;

import java.util.ArrayList;

import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class StepByStepSolver {

	private AppDNoGui app;
	private Kernel kernel;
	private AlgebraProcessor ap;
	private GeoGebraCasInterface cas;
	private ArrayList<String> output;
	private StringTemplate tpl;
	private Parser parser;

	public StepByStepSolver(String stringValue) {
		this.app = new AppDNoGui(new LocalizationD(3), false);

		this.kernel = app.getKernel();
		this.ap = kernel.getAlgebraProcessor();
		this.cas = app.getKernel().getGeoGebraCAS();
		this.parser = kernel.getParser();

		this.tpl = StringTemplate.defaultTemplate;

		String LHS = "4x-3+x";
		String RHS = "1-x";

		LHS = "(x+1)/x";

		LHS = "(x+1)(x+2)";

		LHS = "x/2";

		LHS = stripSpaces(LHS);
		RHS = stripSpaces(RHS);

		output = new ArrayList<String>();

		String[] ret = solve(LHS, RHS);
		output("finished, LHS = " + ret[0] + ", RHS = " + ret[1]);

		for (int i = 0; i < output.size(); i++) {
			Log.error(output.get(i));
		}

	}

	private String[] solve(String LHS, String RHS) {
		output("solving: " + LHS + " = " + RHS);

		boolean stepDone = false;

		String solutionsStr = cas.evaluateGeoGebraCAS(
				"Solutions[" + LHS + "=" + RHS + "]", null,
				StringTemplate.defaultTemplate, app.getKernel());
		String NsolutionsStr = cas.evaluateGeoGebraCAS(
				"NSolutions[" + LHS + "=" + RHS + "]", null,
				StringTemplate.defaultTemplate, app.getKernel());

		int degeeLHS = degree(LHS);
		int degeeRHS = degree(RHS);

		GeoList solutionsList = ap.evaluateToList(solutionsStr);

		GeoList NsolutionsList = ap.evaluateToList(solutionsStr);

		Log.error("number of solutions = " + solutionsList.size());
		Log.error("number of numeric solutions = " + NsolutionsList.size());

		// PartialFractions[1/2 x] gives x/2
		// maybe also "Polynomial" is useful

		String[] commands = { "Evaluate", "Simplify", "Expand",
				"PartialFractions", "Factor" };

		for (int i = 0; i < commands.length; i++) {

			String simpleLHS = callCAS(LHS, commands[i]);
			String simpleRHS = callCAS(RHS, commands[i]);
			Log.error("simpleLHS = " + simpleLHS);
			Log.error("simpleRHS = " + simpleRHS);

			if (count(simpleLHS, "x") < count(LHS, "x")
					|| simpleLHS.length() < LHS.length()) {
				output("simplifying " + LHS + " to " + simpleLHS);

				Log.error("" + count(simpleLHS, "x"));
				Log.error("" + count(LHS, "x"));
				Log.error("" + simpleLHS.length());
				Log.error("" + LHS.length());

				LHS = simpleLHS;
				stepDone = true;
			}
			if (count(simpleRHS, "x") < count(RHS, "x")
					|| simpleRHS.length() < RHS.length()) {
				output("simplifying " + RHS + " to " + simpleRHS);
				RHS = simpleRHS;
				stepDone = true;
			}
		}

		if (stepDone) {
			return solve(LHS, RHS);
		}

		try {
			ExpressionNode enLHS = (ExpressionNode) parser
					.parseGeoGebraExpression(LHS);
			ExpressionNode enRHS = (ExpressionNode) parser
					.parseGeoGebraExpression(RHS);

			if (enLHS.getOperation().equals(Operation.DIVIDE)) {
				output("multiply both sides by " + enLHS.getRight());
				return solve(enLHS.getLeft().toString(tpl), "(" + RHS + ")("
						+ enLHS.getRight().toString(tpl) + ")");
			}
			if (enRHS.getOperation().equals(Operation.DIVIDE)) {
				output("multiply both sides by " + enRHS.getRight());
				return solve(
						"(" + LHS + ")(" + enRHS.getRight().toString(tpl) + ")",
						enRHS.getLeft().toString(tpl));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		String[] ret = { LHS, RHS };
		return ret;

	}

	/**
	 * 
	 * @param s
	 *            expression
	 * @return degree (or -1 for non-polynomials)
	 */
	private int degree(String s) {
		s = callCAS(s, "Degree");
		if ("?".equals(s)) {
			return -1;
		}
		return Integer.parseInt(s);
	}

	private void output(String message) {
		output.add(message);
		Log.error(message);

	}

	private String stripSpaces(String s) {
		return s.replaceAll(" ", "");
	}

	private int count(String input, String match) {
		int index = input.indexOf(match);
		int count = 0;
		while (index != -1) {
			count++;
			input = input.substring(index + 1);
			index = input.indexOf(match);
		}
		return count;
	}

	private String simplify(String s) {
		return callCAS(s, "Simplify");
	}

	private String simplifyFull(String s) {
		return callCAS(s, "Simplify");
	}

	private String expand(String s) {
		return callCAS(s, "Expand");
	}

	private String partialFractions(String s) {
		return callCAS(s, "PartialFractions");
	}

	private String callCAS(String s, String cmd) {
		String ret = cas.evaluateGeoGebraCAS(cmd + "[" + s + "]", null,
				StringTemplate.defaultTemplate, kernel);

		return stripSpaces(ret);
	}

}
