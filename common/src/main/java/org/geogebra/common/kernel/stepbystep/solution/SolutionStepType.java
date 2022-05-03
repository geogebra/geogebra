package org.geogebra.common.kernel.stepbystep.solution;

public enum SolutionStepType {

	WRAPPER("", ""),

	GROUP_WRAPPER("", ""),

	SUBSTEP_WRAPPER("", ""),

	TABLE("", ""),

	EQUATION("", "%0"),

	LIST("", "%0"),

	SIMPLIFY("SimplifyA", "Simplify %0"),

	EXPAND("ExpandA", "Expand %0"),

	FACTOR("FactorA", "Factor %0"),

	SOLVE("Solve", "Solve %0"),

	SOLVE_FOR("SolveFor", "Solve %0 for %1"),

	SOLVE_IN("SolveIn", "Solve %0 in %1"),

	SOLVE_WHEN("SolveWhen", "Solve %0 when %1"),

	TRUE_FOR_ALL("TrueForAllAInB", "The equation is true for all %0"),

	NO_REAL_SOLUTION("NoRealSolutions", "No Real Solutions"),

	INVALID_NOT_IN_RANGE("InvalidNotInRange",
			"%0 is not a valid solution, " + "because $%0 \\notin %1$"),

	SOLUTION("SolutionA", "Solution: %0"),

	SOLUTIONS("SolutionsA", "Solutions: %0"),

	STATEMENT_IS_TRUE("StatementIsTrue", "The statement is true"),

	STATEMENT_IS_FALSE("StatementIsFalse", "The statement is false"),

	POSITIVE_GE_ZERO("PositiveGEZero", "The statement is true for all values of %0"),

	POSITIVE_GT_ZERO("PositiveGTZero", "The statement is true, except when %0"),

	POSITIVE_LE_ZERO("PositiveLEZero", "The statement is only true when %0"),

	POSITIVE_LT_ZERO("PositiveLTZero", "The statement is false for all values of %0"),

	ZERO_GE_POSITIVE("PositiveGEZero", "The statement is only true when %0"),

	ZERO_GT_POSITIVE("PositiveGTZero", "The statement is false for all values of %0"),

	ZERO_LE_POSITIVE("PositiveLEZero", "The statement is true for all values of %0"),

	ZERO_LT_POSITIVE("PositiveLTZero", "The statement is true, except when %0"),

	POSITIVE_L_NEGATIVE("PositiveLNegative", "The statement is false for all values of %0"),

	POSTIVE_G_NEGATIVE("PositiveGNegative", "The statement is true for all values of %0"),

	NEGATIVE_L_POSITIVE("NegativeLPositive", "The statement is true for all values of %0"),

	NEGATIVE_G_POSITIVE("NegativeGPositive", "The statement is false for all values of %0"),

	LEADING_COEFFICIENT_POSITIVE("LeadingCoefficientPositive",
			"$a = %0 > 0$ therefore $%1 > 0$ for all %2"),

	LEADING_COEFFICIENT_NEGATIVE("LeadingCoefficientNegative",
			"$a = %0 < 0$ therefore $%1 < 0$ for all %2"),

	LEFT_POSITIVE_RIGHT_NEGATIVE("LeftPositiveRightNegative",
			"The left hand side is always positive, the right is always negative"),

	EXCLUDE_UNDEFINED_POINTS("ExcludeUndefinedPoints", "Exclude undefined points",
			"Exclude undefined points: %0"),

	PLUG_IN_AND_CHECK("PlugInAndCheck", "Plug in and check if %0 is a correct solution"),

	CHECK_VALIDITY("CheckingValidityOfSolutions", "Checking validity of solutions"),

	VALID_SOLUTION("ValidSolution", "Valid Solution: %0"),

	INVALID_SOLUTION("InvalidSolution", "Invalid Solution: %0"),

	VALID_SOLUTION_ABS("ValidSolutionAbs", "$%0 \\in %1$"),

	INVALID_SOLUTION_ABS("ValidSolutionAbs", "$%0 \\notin %1$"),

	FIND_UNDEFINED_POINTS("FindUndefinedPoints", "Find undefined points"),

	DETERMINE_THE_DEFINED_RANGE("DetermineTheDefinedRange", "Determine the defined range"),

	ROOTS_AND_SIGN_TABLE("FindRootsAndCreateSignTable",
			"Find the roots of the absolute values and create sign table"),

	RESOLVE_ABSOLUTE_VALUES("ResolveAbsoluteValues", "Resolve Absolute Values",
			"$\\left|a\\right| = \\left|b\\right| \\implies a = \\pm b$"),

	IS_POSITIVE_IN("IsPositiveIn", "Resolve Absolute Values", "$%0 \\ge 0$ in %1"),

	IS_NEGATIVE_IN("IsNegativeIn", "Resolve Absolute Values", "$%0 \\le 0$ in %1"),

	NEGATE_BOTH_SIDES("NegateBothSides", "Negate both sides"),

	SQUARE_ROOT("TakeSquareRoot", "Take square root of both sides"),

	CUBE_ROOT("TakeCubeRoot", "Take cube root of both sides"),

	NTH_ROOT("TakeNthRoot", "Take %0 root of both sides"),

	SQUARE_BOTH_SIDES("SquareBothSides", "Square both sides"),

	RAISE_TO_POWER("RaiseToPower", "Raise both sides to the %0 power"),

	ADD_TO_BOTH_SIDES("AddAToBothSides", "Add %0 to both sides"),

	ADD_TO_BOTH_SIDES_NUM("AddToBithSidesNum", "Add %0 to the %1 equation"),

	SUBTRACT_FROM_BOTH_SIDES("SubtractAFromBothSides", "Subtract %0 from both sides"),

	SUBTRACT_FROM_BOTH_SIDES_NUM("SubtractFromBothSidesNum",
			"Subtract %0 from both sides of equation %1"),

	MULTIPLY_BOTH_SIDES("MultiplyBothSidesByA", "Multiply both sides by %0"),

	MULTIPLY_BOTH_SIDES_NUM("MultiplyBothSidesNum", "Multiply both sides by %0 of equation %1"),

	DIVIDE_BOTH_SIDES("DivideBothSidesByA", "Divide both sides by %0"),

	DIVIDE_BOTH_SIDES_NUM("DivideBothSidesNum", "Divide both sides by %0 of equation %1"),

	FACTOR_EQUATION("FactorEquation", "Factor equation"),

	RECIPROCATE_BOTH_SIDES("ReciprocateBothSides", "Reciprocate  both sides"),

	USE_QUADRATIC_FORMULA("UseQuadraticFormulaWithABC",
			"Use quadratic formula with $a = %0$, $b = %1$, $c = %2$"),

	QUADRATIC_FORMULA("QuadraticFormula", "$%0 = \\frac{-b \\pm \\sqrt{b^2-4ac}}{2a}$"),

	COMPLETE_THE_CUBE("CompleteCube", "Complete the cube"),

	COMPLETE_THE_SQUARE("CompleteSquare", "Complete the square"),

	NO_SOLUTION_SIN_COS("NoSolutionSinCos",
			"$%0 \\in \\left[-1, 1\\right]$ for all $%1 \\in \\mathbb{R}$"),

	NO_SOLUTION_ARCSIN("NoSolutionArcsin",
			"$%0 \\in \\left[-\\frac{\\pi}{2}, \\frac{\\pi}{2}\\right]$ "
					+ "for all $%1 \\in \\mathbb{R}$"),

	NO_SOLUTION_ARCCOS("NoSolutionArccos",
			"$%0 \\in \\left[0, \\pi\\right]$ for all $%1 \\in \\mathbb{R}$"),

	NO_SOLUTION_ARCTAN("NoSolutionArctan",
			"$%0 \\in \\left[-\\frac{\\pi}{2}, \\frac{\\pi}{2}\\right]$ "
					+ "for all $%1 \\in \\mathbb{R}$"),

	REPLACE_WITH("ReplaceAWithB", "Replace %0 with %1"),

	REPLACE_AND_REGROUP("ReplaceAndRegroup", "Replace %0 in %1 and regroup"),

	REPLACE_AND_SOLVE("ReplaceAndSolve", "Replace %0 with %1 in %2 and solve the equation"),

	EXPAND_FRACTIONS("ExpandFractions", "Expand Fractions, the common denominator is: %0"),

	PRODUCT_IS_ZERO("ProductIsZero", "Product is zero"),

	FRACTION_IS_ZERO("FractionIsZero", "Set the numerator equal to zero",
			"When the quotient of expressions equals 0, the numerator has to be 0"),

	REGROUP_WRAPPER("RegroupExpression", "Regroup Expression"),

	SIMPLIFICATION_WRAPPER("SimplifyExpression", "Simplify Expression"),

	CONVERT_DECIMALS("ConvertDecimals", "Convert decimals to fractions"),

	DOUBLE_MINUS("DoubleMinus", "A double negative is a positive"),

	RATIONALIZE_DENOMINATOR("RationalizeDenominator", "Rationalize the denominator."),

	MULTIPLY_NUM_DENOM("MultiplyNumeratorAndDenominator",
			"Mutiply the numerator and denominator by %0"),

	DISTRIBUTE_POWER_FRAC("DistributePowerOverFraction", "Distribute power over fraction"),

	DISTRIBUTE_ROOT_FRAC("DistributeRootOverFraction", "Distribute the root over the fraction"),

	DISTRIBUTE_MINUS("DistributeMinus", "Distribute minus"),

	ADD_CONSTANTS("AddConstants", "Add constants"),

	COLLECT_LIKE_TERMS("CollectLikeTerms", "Collect like terms", "Collect like terms: %0"),

	ADD_FRACTIONS("AddFractions", "Add fractions"),

	ADD_NUMERATORS("AddNumerators", "Add numerators"),

	COMMON_FRACTION("CommonFraction", "Write the product as a single fraction"),

	CANCEL_FRACTION("CancelFraction", "Cancel common divisors", "Cancel %0 in the fraction"),

	MULTIPLIED_BY_ZERO("MultipliedByZero", "Anything multiplied by zero is zero"),

	REGROUP_PRODUCTS("RegroupProducts", "Regroup products"),

	MULTIPLY_CONSTANTS("MultiplyConstants", "Multiply constants"),

	MULTIPLIED_BY_ONE("MultipliedByOne", "Multiply constants",
			"Any expression multiplied by one is itself"),

	EVEN_NUMBER_OF_NEGATIVES("EvenNumberOfNegative", "Multiply negatives",
			"Multiplying an even number of negative numbers gives a positive"),

	ODD_NUMBER_OF_NEGATIVES("OddNumberOfNegative", "Multiply negatives",
			"Multiplying an odd number of negative numbers gives a negative"),

	EVEN_POWER_NEGATIVE("EvenPowerNegative", "An even power of a negative number is a positive"),

	ODD_POWER_NEGATIVE("OddPowerNegative", "An odd power of a negative number is a negative"),

	NEGATIVE_NUM_AND_DENOM("NegativeNumeratorAndDenominator",
			"Use $\\frac{-a}{-b} = \\frac{a}{b}$"),

	NEGATIVE_NUM_OR_DENOM("NegativeNumeratorOrDenominator",
			"Use $\\frac{-a}{b} = \\frac{a}{-b} = -\\frac{a}{b}$"),

	REDUCE_ROOT_AND_POWER("ReduceRootAndPower", "Reduce the root and power",
			"Reduce the root and power by: %0"),

	REDUCE_ROOT_AND_POWER_EVEN("ReduceRootAndPowerEven", "Reduce the root and power",
			"Reduce the root and power by: %0"),

	EVALUATE_FRACTION("EvaluateFraction", "Evaluate fraction"),

	EVALUATE_POWER("EvaluatePower", "Evaluate powers"),

	EVALUATE_ROOT("EvaluateRoot", "Evaluate roots"),

	ZEROTH_POWER("ZerothPower", "Evaluate powers", "The zeroth power of anything is one"),

	FIRST_POWER("FirstPower", "Evaluate powers", "The first power of anything is itself"),

	FIRST_ROOT("FirstRoot", "Evaluate roots", "The first root of anything is itself"),

	ROOT_OF_ONE("RootOfOne", "Any root of 1 is 1"),

	EVEN_ROOT_OF_NEGATIVE("EvenRootOfNegative",
			"The expression is undefined in the set of the real numbers",
			"The square root of a negative number is undefined in the set of the real numbers"),

	ODD_ROOT_OF_NEGATIVE("OddRootOfNegative",
			"An odd root of a negative radicand is always negative"),

	ROOT_OF_ROOT("RootOfRoot",
			"Use $\\sqrt[m]{\\sqrt[n]{a}} \\equiv \\sqrt[mn]{a}$ to simplify the expression"),

	REWRITE_DECIMAL_AS_COMMON_FRACTION("RewriteDecimalAsCommonFraction",
			"Rewrite decimal as common fraction"),

	SPLIT_PRODUCTS("SplitProducts", "Split products for factoring"),

	SPLIT_FRACTIONS("SplitFractions", "Split fractions"),

	SPLIT_POWERS("SplitPowers", "Use power rules", "Rewrite %0 as %1"),

	SPLIT_ROOTS("SplitRoots", "Use radical rules",
			"The root of a product is equal to the product of the roots of each factor"),

	ELIMINATE_OPPOSITES("EliminateOpposites", "Eliminate the opposites"),

	EXPONENTIAL_OF_LOG("ExponentialOfLog", "Exponential of logarithm"),

	ZERO_IN_ADDITION("AddingOrSubtractionZero",
			"When adding or subtracting zero, the quantity does not change"),

	ZERO_DIVIDED("ZeroDivided", "Evaluate Fractions", "Zero divided by anything is zero"),

	EVALUATE_DIVISION("EvaluateDivision", "Evaluate Fractions", "Evaluate the division"),

	DIVIDED_BY_ITSELF("DividedByItself", "Evaluate Fractions",
			"Any expression divided by itself equals 1"),

	DIVIDED_BY_ZERO("DividedByZero", "The expression is undefined",
			"Any expression divided by zero is undefined"),

	DIVIDE_BY_ONE("DividedByOne", "Evaluate Fractions",
			"Any expression divided by one remains the same"),

	DIVIDE_BY_NEGATVE_ONE("DividedByNegativeOne", "Evaluate Fractions",
			"Any expression divided by negative one negates the expression"),

	REWRITE_AS("RewriteAAsB", "Rewrite %0 as %1"),

	REWRITE_COMPLEX_FRACTION("RewriteComplexFraction", "Rewrite complex fraction"),

	POWER_OF_POWER("MultiplyExponents", "Simplify the expression by multiplying the exponents"),

	NEGATIVE_POWER("NegativePower", "Express with a positive exponent",
			"Express with a positive exponent using $a^{-n} \\equiv \\frac{1}{a^n}$"),

	FACTOR_SQUARE("FactorSquare", "Factor out the perfect square"),

	EXPAND_SUM_TIMES_SUM("ExpandSumTimesSum", "Expand product",
			"Multiply everything in the first parentheses with "
					+ "everything in the second parentheses"),

	EXPAND_SIMPLE_TIMES_SUM("ExpandSimpleTimesSum", "Expand product",
			"Multiply %0 with everything in the parentheses"),

	BINOM_SQUARED_SUM("BinomSquaredSum", "Use $(a+b)^2 \\equiv a^2 + 2ab + b^2$ to expand"),

	BINOM_SQUARED_DIFF("BinomSquaredDiff", "Use $(a-b)^2 \\equiv a^2 - 2ab + b^2$ to expand"),

	BINOM_CUBED("BinomCubed", "Use $(a+b)^3 \\equiv a^3 + 3a^2b + 3ab^2 + b^3$ to expand"),

	TRINOM_SQUARED("TrinomSquared",
			"Use $(a+b+c)^2 \\equiv a^2 + b^2 + c^2 + 2ab + 2bc + 2ac$ to expand"),

	DIFFERENCE_OF_SQUARES("DifferenceOfSquares", "Use $(a+b)(a-b) \\equiv a^2-b^2$ to expand"),

	FACTOR_FRACTIONS("FactorFractions", "Factor fractions"),

	SUM_OF_CUBES("SumOfCubes", "Use $a^3 + b^3 = (a + b)(a^2 - ab + b^2)$ to factor"),

	BINOM_SQUARED_SUM_FACTOR("BinomSquaredSum", "Use $a^2 + 2ab + b^2 \\equiv (a+b)^2$ to factor"),

	BINOM_SQUARED_DIFF_FACTOR("BinomSquaredDiff",
			"Use $a^2 - 2ab + b^2 \\equiv (a-b)^2$ to factor"),

	DIFFERENCE_OF_CUBES_FACTOR("DifferenceOfCubes",
			"Use $a^3 - b^3 = (a - b)(a^2 + ab + b^2)$ to factor"),

	DIFFERENCE_OF_SQUARES_FACTOR("DifferenceOfSquaresFactor",
			"Use $a^2-b^2 \\equiv (a+b)(a-b)$ to factor"),

	BINOM_CUBED_SUM_FACTOR("BinomCubed",
			"Use $a^3 + 3a^2b + 3ab^2 + b^3 \\equiv (a+b)^3$ to factor"),

	BINOM_CUBED_DIFF_FACTOR("BinomCubed",
			"Use $a^3 - 3a^2b + 3ab^2 - b^3 \\equiv (a+b)^3$ to factor"),

	IS_POSITIVE_IN_INEQUALITY("IsPositiveInInequality",
			"%0 is positive in %1, because there are "
					+ "an even number of negative values in the product"),

	IS_NEGATIVE_IN_INEQUALITY("IsNegativeInInequality",
			"%0 is negative in %1, because there are "
					+ "an odd number of negative values in the product"),

	IS_ZERO_IN("IsZeroIn", "%0 is zero in %1, because at least one of the multiplicands is zero"),

	IS_INVALID_IN("IsInvalidIn", "%0 is invalid in %1, because there is a zero in the denominator"),

	FACTOR_QUADRATIC("FactorQuadratic", "Factor the expression using $x_{1}, x_{2}$",
			"Use $ax^2+bx+c = a\\left(x - x_{1}\\right)\\left(x - x_{2}\\right)$ "
					+ "to factor the expression"),

	FACTOR_OUT("FactorOutA", "Factor common", "Factor out %0"),

	FACTOR_COMMON("FactorCommon", "Factor common"),

	FACTOR_POLYNOMIAL("FactorPolynomial", "Factor polynomial"),

	FACTOR_FROM_PAIR("FactorOutAFromEveryPair", "Factor out %0 from every pair"),

	FACTOR_MINUS("FactorMinus", "Factor out the minus sign"),

	FACTOR_GCD("FactorGCD", "Factor out the greatest common divisor of %0 and %1: %2"),

	REORGANIZE_EXPRESSION("ReorganizeExpression", "Reorganize expression"),

	REWRITE_AS_MULTIPLICATION("RewriteAsMultiplication", "Rewrite as multiplication"),

	DISTRIBUTE_POWER_OVER_PRODUCT("DistributePowerOverProduct", "Distribute power over product"),

	SQUARE_ROOT_MULTIPLIED_BY_ITSELF("SquareRootMultipliedByItself",
			"When the square root of an expression in multiplied by itself, "
					+ "the result is that expression"),

	EXPAND_ROOT("ExpandRoot",
			"Using $\\sqrt[n]{a} \\equiv \\sqrt[mn]{a^m}$, expand the expression"),

	PRODUCT_OF_ROOTS("ProductOfRoots",
			"The product of roots with the same index is equal to the root of the product"),

	POLYNOMIAL_DIVISION("DivideAByBToGetC", "Divide %0 by %1 to get %2"),

	EVALUATE_TRIGO("EvaluateTrigo", "Evaluate trigonometric function"),

	TRIGO_ODD_SIN("TrigoOddSin", "Sine is an odd function", "$\\sin(-x) = -\\sin(x)$"),

	TRIGO_EVEN_COS("TrigoEvenCos", "Cosine is an even function", "$\\cos(-x) = \\cos(x)$"),

	TRIGO_ODD_TAN("TrigoOddTan", "Tangent is an odd function", "$\\tan(-x) = \\tan(x)$"),

	FACTOR_OUT_2PI("FactorOut2Pi", "Factor out $2 \\pi$ where possible"),

	FACTOR_OUT_PI("FactorOut2Pi", "Factor out $\\pi$ where possible"),

	ELIMINATE_THE_PERIOD_SIN("EliminatePeriodSin", "Eliminate the period",
			"$\\sin(x + k \\cdot 2 \\pi) = \\sin(x)$, for any $k \\in \\mathbb{Z}$"),

	ELIMINATE_THE_PERIOD_COS("EliminatePeriodSin", "Eliminate the period",
			"$\\cos(x + k \\cdot 2 \\pi) = \\cos(x)$, for any $k \\in \\mathbb{Z}$"),

	ELIMINATE_THE_PERIOD_TAN("EliminatePeriodSin", "Eliminate the period",
			"$\\tan(x + k \\cdot \\pi) = \\tan(x)$, for any $k \\in \\mathbb{Z}$"),

	REDUCE_TO_FRIST_QUADRANT_SIN_II("ReduceToFirstQuadrantSinII",
			"Reduce to first quadrant", "Use $\\sin(x) = \\sin(\\pi - x)$"),

	REDUCE_TO_FRIST_QUADRANT_SIN_III("ReduceToFirstQuadrantSinIII",
			"Reduce to first quadrant", "Use $\\sin(x) = -\\sin(x - \\pi)$"),

	REDUCE_TO_FRIST_QUADRANT_SIN_IV("ReduceToFirstQuadrantSinIV",
			"Reduce to first quadrant", "Use $\\sin(x) = -\\sin(2 \\pi - x)$"),

	REDUCE_TO_FRIST_QUADRANT_COS_II("ReduceToFirstQuadrantCosII",
			"Reduce to first quadrant", "Use $\\cos(x) = -\\cos(\\pi - x)$"),

	REDUCE_TO_FRIST_QUADRANT_COS_III("ReduceToFirstQuadrantCosIII",
			"Reduce to first quadrant", "Use $\\cos(x) = -\\cos(x - \\pi)$"),

	REDUCE_TO_FRIST_QUADRANT_COS_IV("ReduceToFirstQuadrantCosIV",
			"Reduce to first quadrant", "Use $\\cos(x) = \\cos(2 \\pi - x)$"),

	REDUCE_TO_FRIST_QUADRANT_TAN("ReduceToFirstQuadrantTan",
			"Reduce to first quadrant", "Use $\\tan(x) = -\\tan(\\pi - x)$"),

	SPLIT_FRACTIONS_WITH_PI("SplitFractionsWithPi", "Split fractions with $\\pi$"),

	REWRITE_SIN_PI_POSITIVE("RewriteSinPiPositive", "Use rewriting rules",
			"$\\sin(x + \\pi) = -\\sin(x)$"),

	REWRITE_SIN_PI_NEGATIVE("RewriteSinPiNegative", "Use rewriting rules",
			"$\\sin(\\pi - x) = \\sin(x)$"),

	REWRITE_COS_PI_POSITIVE("RewriteCosPiPositive", "Use rewriting rules",
			"$\\cos(x + \\pi) = -\\cos(x)$"),

	REWRITE_COS_PI_NEGATIVE("RewriteCosPiNegative", "Use rewriting rules",
			"$\\cos(\\pi - x) = -\\cos(x)$"),

	REWRITE_SIN_PI_HALF_POSITIVE("RewriteSinPiHalfPositive", "Use rewriting rules",
			"$\\sin\\left(x + \\frac{\\pi}{2}\\right) = \\cos(x)$"),

	REWRITE_SIN_PI_HALF_NEGATIVE("RewriteSinPiHalfNegative", "Use rewriting rules",
			"$\\sin\\left(\\frac{\\pi}{2} - x\\right) = \\cos(x)$"),

	REWRITE_COS_PI_HALF_POSITIVE("RewriteCosPiHalfPositive", "Use rewriting rules",
			"$\\cos\\left(x + \\frac{\\pi}{2}\\right) = -\\sin(x)$"),

	REWRITE_COS_PI_HALF_NEGATIVE("RewriteCosPiHalfNegative", "Use rewriting rules",
			"$\\cos\\left(\\frac{\\pi}{2} - x\\right) = \\sin(x)$"),

	REWRITE_TAN_PI_HALF_POSITIVE("RewriteTanPiHalfPositive", "Use rewriting rules",
			"$\\tan\\left(x + \\frac{\\pi}{2}\\right) = -\\frac{1}{\\tan(x)}$"),

	REWRITE_TAN_PI_HALF_NEGATIVE("RewriteTanPiHalfNegative", "Use rewriting rules",
			"$\\tan\\left(\\frac{\\pi}{2} - x\\right) = \\frac{1}{\\tan(x)}$"),

	EVALUATE_INVERSE_TRIGO("EvaluateInverseTrigo", "Evaluate inverse trigonometric function"),

	POSITIVE_UNDER_ABSOLUTE_VALUE("PositiveUnderAbsoluteValue", "Use rules for absolute values",
			"The absolute value of a positive value is itself"),

	NEGATIVE_UNDER_ABSOLUTE_VALUE("NegativeUnderAbsoluteValue", "Use rules for absolute values",
			"The absolute value of a negative value is the negation of itself"),

	EVEN_POWER_OF_ABSOLUTE_VALUE("EvenPowerOfAbsoluteValue", "Use rules for absolute values",
			"An even power of an absolute value is already positive"),

	DIFFERENTIATE("Differentiate", "Differentiate %0"),

	DIFF_SUM("SumRule", "Use the sum rule",
			"$\\frac{d}{dx} \\left[f(x) + g(x)\\right] = \\frac{d}{dx}f(x) + \\frac{d}{dx}g(x)$"),

	DIFF_CONSTANT("ConstantRule", "The derivative of a constant is zero"),

	DIFF_CONSTANT_COEFFICIENT("ConstantCoefficientRule", "Use the constant factor rule",
			"$\\frac{d}{dx} \\left[k \\cdot f(x) \\right] = k \\cdot \\frac{d}{dx} f(x)$"),

	DIFF_PRODUCT("ProductRule", "Use the product rule",
			"$\\frac{d}{dx}\\left[f(x) \\cdot g(x)\\right] = \\frac{d}{dx} f(x) \\cdot g(x) + f"
					+ "(x) \\cdot \\frac{d}{dx} g(x)$"),

	DIFF_FRACTION("QuotientRule", "Use the quotient rule",
			"$\\frac{d}{dx} \\frac{f(x)}{g(x)} = \\frac{\\frac{d}{dx} f(x) \\cdot g(x) - f(x) "
					+ "\\cdot \\frac{d}{dx} g(x)}{(g(x))^2}$"),

	DIFF_VARIABLE("DifferentiateVariable", "Use the power rule", "$\\frac{d}{dx} x = 1$"),

	DIFF_POWER("PowerRule", "Use the power rule", "$\\frac{d}{dx} x^n = n x^{n-1}$"),

	DIFF_EXPONENTIAL_E("ExponentialRuleE", "Use the exponential rule", "$\\frac{d}{dx} e^x = e^x$"),

	DIFF_EXPONENTIAL("ExponentialRule", "Use the exponential rule",
			"$\\frac{d}{dx} a^x = \\ln(a) a^x$"),

	DIFF_ROOT("RootRule", "Use the root rule",
			"$\\frac{d}{dx} \\sqrt[n]{x} = \\frac{1}{n \\sqrt[n]{x^{n-1}}}$"),

	DIFF_LOG("LogRule", "Use the log rule",
			"$\\frac{d}{dx} \\( \\log_{a} \\left(x\\right) \\) = \\frac{1}{\\ln(a) \\cdot x}$"),

	DIFF_NATURAL_LOG("NaturalLogRule", "Use the log rule",
			"$\\frac{d}{dx} \\ln(x) = \\frac{1}{x}$"),

	DIFF_SIN("SinRule", "Use the rules of trigonometric functions",
			"$\\frac{d}{dx} sin(x) = cos(x)$"),

	DIFF_COS("CosRule", "Use the rules of trigonometric funtions",
			"$\\frac{d}{dx} cos(x) = -sin(x)$"),

	DIFF_TAN("TanRule", "Use the rules of trigonometric funtions",
			"$\\frac{d}{dx} tan(x) = \\frac{1}{cos^2(x)}$"),

	DIFF_ARCSIN("ArcsinRule", "Use the rules of inverse trigonometric funtions",
			"$\\frac{d}{dx} arcsin(x) = \\frac{1}{\\sqrt{1-x^2}}$"),

	DIFF_ARCCOS("ArccosRule", "Use the rules of inverse trigonometric funtions",
			"$\\frac{d}{dx} arccos(x) = -\\frac{1}{\\sqrt{1-x^2}}$"),

	DIFF_ARCTAN("ArctanRule", "Use the rules of inverse trigonometric funtions",
			"$\\frac{d}{dx} arctan(x) = \\frac{1}{x^2+1}$"),

	DIFF_POWER_CHAIN("PowerRuleChain", "Use the power rule",
			"$\\frac{d}{dx} (u(x))^n = n (u(x))^{n-1} \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_EXPONENTIAL_E_CHAIN("ExponentialRuleEChain", "Use the exponential rule",
			"$\\frac{d}{dx} a^{u(x)} = \\ln(a) a^{u(x)} \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_EXPONENTIAL_CHAIN("ExponentialRuleChain", "Use the exponential rule",
			"$\\frac{d}{dx} e^{u(x)} = e^{u(x)} \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_ROOT_CHAIN("RootRuleChain", "Use the root rule",
			"$\\frac{d}{dx} \\sqrt[n]{u(x)} = \\frac{1}{n \\sqrt[n]{(u(x))^{n-1}}} \\cdot "
					+ "\\frac{d}{dx} u(x)$"),

	DIFF_LOG_CHAIN("LogRuleChain", "Use the log rule",
			"$\\frac{d}{dx} \\log_{a}(u(x)) "
					+ "= \\frac{1}{\\ln(a) \\cdot u(x)} \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_NATURAL_LOG_CHAIN("NaturalLogRuleChain", "Use the log rule",
			"$\\frac{d}{dx} \\ln(u(x)) = \\frac{1}{u(x)} \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_SIN_CHAIN("SinRuleChain", "Use the rules of trigonometric funtions",
			"$\\frac{d}{dx} sin(u(x)) = cos(u(x)) \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_COS_CHAIN("CosRuleChain", "Use the rules of trigonometric funtions",
			"$\\frac{d}{dx} cos(u(x)) = -sin(u(x)) \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_TAN_CHAIN("TanRuleChain", "Use the rules of trigonometric funtions",
			"$\\frac{d}{dx} tan(u(x)) = \\frac{1}{cos^2(u(x))} \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_ARCSIN_CHAIN("ArcsinRuleChain", "Use the rules of inverse trigonometric funtions",
			"$\\frac{d}{dx} arcsin(u(x)) "
					+ "= \\frac{1}{\\sqrt{1-(u(x))^2}} \\cdot \\frac{d}{dx} u(x)$"),

	DIFF_ARCCOS_CHAIN("ArccosRuleChain", "Use the rules of inverse trigonometric funtions",
			"\\frac{d}{dx} arccos(u(x)) = -\\frac{1}{\\sqrt{1-(u(x))^2}} "
					+ "\\cdot \\frac{d}{dx} u(x)"),

	DIFF_ARCTAN_CHAIN("ArctanRuleChain", "Use the rules of inverse trigonometric funtions",
			"\\frac{d}{dx} arctan(u(x)) = \\frac{1}{(u(x))^2+1} \\cdot \\frac{d}{dx} u(x)"),

	ADD_EQUATIONS("AddEquations", "Add the equations to eliminate %0"),

	USE_CRAMERS_RULE("UseCramersRule", "Use Cramer's rule to solve the equation system"),

	DETERMINANTS("", "$D = %3$, $D_{1} = %0$, $D_{2} = %1$, $D_{3} = %2$"),

	CRAMER_VARIABLE("", "$%0 = \\frac{D_{%1}}{D} = \\frac{%2}{%3}$"),

	USE_LEIBNIZ_FORMULA("UseLeibnizFormula", "Evaluate the determinant",
			"Use the Leibniz formula to evaluate the determinant"),

	CALCULATE_DETERINANT("CalculateDeterminant", "Calculate the determinant %0"),

	WRITE_IN_MATRIX_FORM("WriteInMatrixForm", "Write the equation system in matrix form"),

	WRITE_IN_SYSTEM_FORM("WriteInSystemForm", "Write the matrix in equation system form"),

	MULTIPLY_ROW_AND_ADD("MultiplyRowAndAdd", "Multiply row %0 by %1 and add it to row %2"),

	MULTIPLY_EACH_ELEMENT_AND_ADD("MultiplyEachElementAndAdd",
			"Multiply each element in row %0 by %1 and add that product to row %2"),

	DIVIDE_ROW("DivideRow", "Divide row %0 by %1"),

	DIVIDE_EACH_ELEMENT("DivideEachElement", "Divide each element in row %0 by %1");

	private final String keyText;
	private final String defaultText;
	private final String detailedText;

	SolutionStepType(String keyText, String defaultText, String detailedText) {
		this.keyText = keyText;
		this.defaultText = defaultText;
		this.detailedText = detailedText;
	}

	SolutionStepType(String keyText, String defaultText) {
		this(keyText, defaultText, defaultText);
	}

	public String getKey() {
		return keyText;
	}

	public String getDefault() {
		return defaultText;
	}

	public String getDetailed() {
		return detailedText;
	}
}
