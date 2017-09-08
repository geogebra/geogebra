package org.geogebra.common.kernel.stepbystep.solution;

import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public enum SolutionStepType {
	WRAPPER("", ""),
	
	SUBSTEP_WRAPPER("", ""),

	EQUATION("", ""),

	SOLVE("Solve", "Solve: %0"),

	NEW_CASE("CaseA", "Case ?: %0"),
	
	SOLVING_IN_INTERVAL("SolvingInInterval", "Case ?: %0 when %1"),

	CANT_SOLVE("CantSolve", "Cannot Solve"),

	TRUE_FOR_ALL("TrueForAllAInB", "The equation is true for all %0"),

	NO_REAL_SOLUTION("NoRealSolutions", "No Real Solutions"),

	SOLUTION("SolutionA", "Solution: %0"),

	SOLUTIONS("SolutionsA", "Solutions: %0") {
		@Override
		public String getDefaultText(Localization loc, StepNode[] parameters) {
			StringBuilder serializedDefault = new StringBuilder();
			for (int i = 0; i < parameters.length; i++) {
				if (i != 0) {
					serializedDefault.append(",\\;");
				}
				serializedDefault.append(parameters[i].toLaTeXString(loc, false));
			}
			return loc.getMenuLaTeX(getKey(), getDefault(), serializedDefault.toString());
		}

		@Override
		public String getDetailedText(Localization loc, int color, StepNode[] parameters) {
			StringBuilder serializedColored = new StringBuilder();
			for (int i = 0; i < parameters.length; i++) {
				if (i != 0) {
					serializedColored.append(",\\;");
				}
				serializedColored.append(parameters[i].toLaTeXString(loc, true));
			}
			return loc.getMenuLaTeX(getKey(), getDefault(), serializedColored.toString()) + colorText(color);
		}
	},

	CHECK_VALIDITY("CheckingValidityOfSolutions", "Checking validity of solutions"),

	VALID_SOLUTION("ValidSolution", "Valid Solution: %0"),

	INVALID_SOLUTION("InvalidSolution", "Invalid Solution: %0"),

	VALID_SOLUTION_ABS("ValidSolutionAbs", "%0 \\in %1"),

	INVALID_SOLUTION_ABS("ValidSolutionAbs", "%0 \\notin %1"),

	RESOLVE_ABSOLUTE_VALUES("ResolveAbsoluteValues", "Resolve Absolute Values"),

	SQUARE_ROOT("TakeSquareRoot", "Take square root of both sides"),

	CUBE_ROOT("TakeCubeRoot", "Take cube root of both sides"),

	NTH_ROOT("TakeNthRoot", "Take %0 root of both sides") {
		@Override
		public String getDefaultText(Localization loc, StepNode[] parameters) {
			return loc.getMenuLaTeX(getKey(), getDefault(), loc.getOrdinalNumber((int) parameters[0].getValue()));
		}

		@Override
		public String getDetailedText(Localization loc, int color, StepNode[] parameters) {
			return loc.getMenuLaTeX(getKey(), getDefault(), loc.getOrdinalNumber((int) parameters[0].getValue()));
		}
	},

	SQUARE_BOTH_SIDES("SquareBothSides", "Square both sides"),

	ADD_TO_BOTH_SIDES("AddAToBothSides", "Add %0 to both sides"),

	SUBTRACT_FROM_BOTH_SIDES("SubtractAFromBothSides", "Subtract %0 from both sides"),

	MULTIPLY_BOTH_SIDES("MultiplyBothSidesByA", "Multiply both sides by %0"),

	DIVIDE_BOTH_SIDES("DivideBothSidesByA", "Divide both sides by %0"),

	FACTOR_EQUATION("FactorEquation", "Factor equation"),
	
	INVERT_BOTH_SIDES("InvertBothSides", "Invert both sides"),

	USE_QUADRATIC_FORMULA("UseQuadraticFormulaWithABC", "Use quadratic formula with a = %0, b = %1, c = %2"),

	QUADRATIC_FORMULA("QuadraticFormula", "%0 = \\frac{-b \\pm \\sqrt{b^2-4ac}}{2a}"),

	COMPLETE_THE_CUBE("CompleteCube", "Complete the cube"),

	COMPLETE_THE_SQUARE("CompleteSquare", "Complete the square"),

	NO_SOLUTION_TRIGONOMETRIC("NoSolutionTrigonometricSin", "%0 \\in [-1, 1] for all %1 \\in \\mathbb{R}"),

	REPLACE_WITH("ReplaceAWithB", "Replace %0 with %1"),

	RATIONAL_ROOT_THEOREM("RationalRootTheorem",
			"A polynomial equation with integer coefficients has all of its rational roots in the form p/q, where p divides the constant term and q divides the coefficient of the highest order term"),

	EXPAND_FRACTIONS("ExpandFractions", "Expand Fractions, the common denominator is: %0"),

	FACTOR_DENOMINATORS("FatorDenominators", "Factor Denominators"),

	PRODUCT_IS_ZERO("ProductIsZero", "Product is zero"),

	TRIAL_AND_ERROR("TrialAndError", "Find the roots by trial and error, and factor them out"),

	SOLVE_NUMERICALLY("SolveNumerically", "Solve numerically: "),

	REGROUP_WRAPPER("RegroupExpression", "Regroup Expression"),

	SIMPLIFICATION_WRAPPER("SimplifyExpression", "Simplify Expression"),

	DOUBLE_MINUS("DoubleMinus", "A double negative is a positive"),

	RATIONALIZE_DENOMINATOR("RationalizeDenominator", "Rationalize the denominator."),

	MULTIPLY_NUM_DENOM("MultiplyNumeratorAndDenominator", "Mutiply the numerator and denominator by %0"),

	DISTRIBUTE_ROOT_FRAC("DistributeRootOverFraction", "Distribute the root over the fraction"),

	DISTRIBUTE_MINUS("DistributeMinus", "Distribute minus"),

	ADD_CONSTANTS("AddConstants", "Add constants"),

	COLLECT_LIKE_TERMS("CollectLikeTerms", "Collect like terms: %0"),

	ADD_FRACTIONS("AddFractions", "Add fractions"),

	ADD_NUMERATORS("AddNumerators", "Add numerators"),

	MULTIPLY_CONSTANTS("MultiplyConstants", "Multiply constants"),

	COMMON_FRACTION("CommonFraction", "Write the product as a single fraction"),

	CANCEL_FRACTION("CancelFraction", "Cancel %0 in the fraction"),

	MULTIPLIED_BY_ZERO("MultipliedByZero", "Anything multiplied by zero is zero"),

	REGROUP_PRODUCTS("RegroupProducts", "Regroup products: "),

	SQUARE_MINUS("SquareMinus", "Squaring a minus makes it go awaaayy"),

	REDUCE_ROOT_AND_POWER("ReduceRootAndPower", "Reduce the root and power by: "),

	REDUCE_ROOT_AND_POWER_EVEN("ReduceRootAndPowerEven", "Reduce the root and power by: "),

	EVALUATE_POWER("EvaluatePower", "Evaluate power"),

	ZEROTH_POWER("ZerothPower", "The zeroth power of anything is one"),

	FIRST_POWER("FirstPower", "The first power of anything is itself"),

	FIRST_ROOT("FirstRoot", "The first root of anything is itself"),

	ROOT_OF_ONE("RootOfOne", "Any root of 1 equals 1"),

	ODD_ROOT_OF_NEGATIVE("OddRootOfNegative", "An odd root of a negative radicand is always negative"),

	ROOT_OF_ROOT("RootOfRoot", "Use $\\sqrt[m]{\\sqrt[n]{a}} = \\sqrt[mn]{a}$ to simplify the expression"),

	ELIMINATE_OPPOSITES("EliminateOpposites", "Eliminate the opposites"),

	ZERO_IN_ADDITION("AddingOrSubtractionZero", "When adding or subtracting zero, the quantity does not change"),

	DIVIDE_BY_ONE("DividedByOne", "Any expression divided by one remains the same"),

	REWRITE_AS_POWER("RevriteAAsB", "Rewrite %0 as %1"),

	POWER_OF_POWER("MultiplyExponents", "Simplify the expression by multiplying the exponents"),

	FACTOR_SQUARE("FactorSquare", "Factor out the perfect square"),

	EXPAND_SUM_TIMES_SUM("ExpandSumTimesSum", "Multiply everything in the first parantheses with everything in the second parantheses"),

	EXPAND_SIMPLE_TIMES_SUM("ExpandSimpleTimesSum", "Multiply %0 with everything in the parantheses"),

	BINOM_SQUARED_SUM("BinomSquaredSum", "Use $(a+b)^2 = a^2 + 2ab + b^2$ to expand"),

	BINOM_SQUARED_DIFF("BinomSquaredDiff", "Use $(a-b)^2 = a^2 - 2ab + b^2$ to expand"),

	BINOM_CUBED("BinomCubed", "Use $(a+b)^3 = a^3 + 3a^2b + 3ab^2 + b^3$ to expand"),

	TRINOM_SQUARED("TrinomSquared", "Use $(a+b+c)^2 = a^2 + b^2 + c^2 + 2ab + 2bc + 2ac$ to expand"),

	DIFFERENCE_OF_SQUARES("DifferenceOfSquares", "Use $(a+b)(a-b) = a^2-b^2$ to expand"),

	REWRITE_AS_MULTIPLICATION("RewriteAsMultiplication", "Rewrite as multiplication"),

	DISTRIBUTE_POWER_OVER_PRODUCT("DistributePowerOverProduct", "Distribute power over product"),

	EVALUATE_INVERSE_TRIGO("EvaluateInverseTrigo", "Evaluate inverse trigonometric function");
	
	private final String keyText;
	private final String defaultText;
	
	SolutionStepType(String keyText, String defaultText) {
		this.keyText = keyText;
		this.defaultText = defaultText;
	}
	
	public String getDefaultText(Localization loc, StepNode[] parameters) {
		if (parameters == null) {
			return loc.getMenuLaTeX(getKey(), getDefault());
		}

		String[] serializedDefault = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			serializedDefault[i] = parameters[i].toLaTeXString(loc, false);
		}
		return loc.getMenuLaTeX(getKey(), getDefault(), serializedDefault);
	}

	public String getDetailedText(Localization loc, int color, StepNode[] parameters) {
		if (parameters == null) {
			return loc.getMenuLaTeX(getKey(), getDefault()) + colorText(color);
		}

		String[] serializedColored = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			serializedColored[i] = parameters[i].toLaTeXString(loc, true);
		}

		return loc.getMenuLaTeX(getKey(), getDefault(), serializedColored) + colorText(color);
	}

	public static String colorText(int color) {
		return color == 0 ? "" : "\\fgcolor{" + getColorHex(color) + "}{\\;\\bullet}";
	}

	private static String getColorHex(int color) {
		switch (color % 5) {
		case 1:
			return StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_RED);
		case 2:
			return StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_BLUE);
		case 3:
			return StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_GREEN);
		case 4:
			return StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_PURPLE);
		case 0:
			return StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_ORANGE);
		default:
			return StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_BLACK);
		}
	}

	public String getKey() {
		return keyText;
	}
	
	public String getDefault() {
		return defaultText;
	}
}
