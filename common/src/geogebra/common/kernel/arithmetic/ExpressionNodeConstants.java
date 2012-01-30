package geogebra.common.kernel.arithmetic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ExpressionNodeConstants {

	public enum StringType {
		GEOGEBRA_XML, GEOGEBRA, MAXIMA, MATH_PIPER, LATEX, PSTRICKS, PGF, JASYMCA, MPREDUCE, MATHML
	}

	public static final String CAS_ROW_REFERENCE_PREFIX = "$";
	public static final String UNICODE_PREFIX = "unicode";
	public static final String UNICODE_DELIMITER = "u";

	// public static final int NO_OPERATION = Integer.MIN_VALUE;
	public static final String strNOT = "\u00ac";
	public static final String strAND = "\u2227";
	public static final String strOR = "\u2228";
	public static final String strLESS_EQUAL = "\u2264";
	public static final String strGREATER_EQUAL = "\u2265";
	public static final String strEQUAL_BOOLEAN = "\u225f";
	public static final String strNOT_EQUAL = "\u2260";
	public static final String strPARALLEL = "\u2225";
	public static final String strPERPENDICULAR = "\u22a5";
	public static final String strVECTORPRODUCT = "\u2297";
	public static final String strIS_ELEMENT_OF = "\u2208";
	public static final String strIS_SUBSET_OF = "\u2286";
	public static final String strIS_SUBSET_OF_STRICT = "\u2282";
	public static final String strSET_DIFFERENCE = "\\";

	/*
	 * these should also be documented here:
	 * http://wiki.geogebra.org/en/Manual:Naming_Objects
	 */
	public static final Set<String> RESERVED_FUNCTION_NAMES = new HashSet<String>(
			Arrays.asList("x", "y", "abs", "sgn", "sqrt", "exp", "log", "ln",
					"ld", "lg", "cos", "sin", "tan", "acos", "arcos", "arccos",
					"asin", "arcsin", "atan", "arctan", "cosh", "sinh", "tanh",
					"acosh", "arcosh", "arccosh", "asinh", "arcsinh", "atanh",
					"arctanh", "atan2", "erf", "psi", "freehand", "floor",
					"ceil", "round", "random", "conjugate", "arg", "gamma",
					"gammaRegularized", "beta", "betaRegularized", "sec",
					"csc", "cosec", "cot", "sech", "csch", "coth"));
}
