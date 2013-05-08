package geogebra.common.kernel.arithmetic;

import geogebra.common.util.Unicode;

/**
 * String constants + String types
 */
public interface ExpressionNodeConstants {
	
	/** String types */
	public enum StringType {
		/**XML*/
		GEOGEBRA_XML,
		/**default*/
		GEOGEBRA,
		/** maxima*/
		GIAC,
		/** mathpiper */
		MATH_PIPER,
		/** LaTeX */
		LATEX,
		/** Libre Office (or Open Office) Math Commands */
		LIBRE_OFFICE,
		/** PSTricks */
		PSTRICKS,
		/** PGF */
		PGF,
		/** JASYMCA */
		JASYMCA,
		/** Reduce */
		MPREDUCE,
		/** MathML */
		MATHML,
		/** OGP */
		OGP
	}
	/** prefix for CAS row reference ($) */
	public static final String CAS_ROW_REFERENCE_PREFIX = "$";
	/** prefix used when serializing unicode characters to CAS*/
	public static final String UNICODE_PREFIX = "unicode";
	/** delimiter used when serializing unicode characters to CAS*/
	public static final String UNICODE_DELIMITER = "u";

	// public static final int NO_OPERATION = Integer.MIN_VALUE;
	/** not */
	public static final String strNOT = "\u00ac";
	/** and */
	public static final String strAND = "\u2227";
	/** and */
	public static final String strIMPLIES = "\u2192";
	/** or */
	public static final String strOR = "\u2228";
	/** less equal */
	public static final String strLESS_EQUAL = "\u2264";
	/** greater equal */
	public static final String strGREATER_EQUAL = "\u2265";
	/** = with question mark */
	public static final String strEQUAL_BOOLEAN = "\u225f";
	/** not equal */
	public static final String strNOT_EQUAL = "\u2260";
	/** parallel */
	public static final String strPARALLEL = "\u2225";
	/** perpendicular */
	public static final String strPERPENDICULAR = "\u22a5";
	/** vector product */
	public static final String strVECTORPRODUCT = Unicode.VECTOR_PRODUCT+"";
	/** is element of */
	public static final String strIS_ELEMENT_OF = "\u2208";
	/** is subset of */
	public static final String strIS_SUBSET_OF = "\u2286";
	/** strict subset of */
	public static final String strIS_SUBSET_OF_STRICT = "\u2282";
	/** set difference */
	public static final String strSET_DIFFERENCE = "\\";

}
