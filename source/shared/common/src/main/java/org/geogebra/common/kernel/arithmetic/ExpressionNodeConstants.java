package org.geogebra.common.kernel.arithmetic;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * String constants + String types
 */
public interface ExpressionNodeConstants {

	/** String types */
	public enum StringType {
		/** XML */
		GEOGEBRA_XML,
		/** default */
		GEOGEBRA,
		/** Giac exact */
		GIAC,
		/** LaTeX */
		LATEX,
		/** Libre Office (or Open Office) Math Commands */
		LIBRE_OFFICE,
		/** PSTricks */
		PSTRICKS,
		/** PGF */
		PGF,
		/** Content MathML */
		CONTENT_MATHML,
		/** OGP */
		OGP,
		/** GGB-2454 for screen reader expanding "+" to "plus" etc. */
		SCREEN_READER_ASCII,
		/** screen reader keeping math symbols as is */
		SCREEN_READER_UNICODE;

		/**
		 * @return whether this is giac type
		 */
		public boolean isGiac() {
			return this.equals(StringType.GIAC);
		}
	}

	/** prefix for CAS row reference ($) */
	public static final String CAS_ROW_REFERENCE_PREFIX = "$";
	/** prefix used when serializing unicode characters to CAS */
	public static final String UNICODE_PREFIX = "unicode";
	/** delimiter used when serializing unicode characters to CAS */
	public static final String UNICODE_DELIMITER = "u";

	// public static final int NO_OPERATION = Integer.MIN_VALUE;
	/** not */
	public static final String strNOT = Unicode.NOT + "";
	/** and */
	public static final String strAND = Unicode.AND + "";
	/** and */
	public static final String strIMPLIES = Unicode.IMPLIES + "";
	/** or */
	public static final String strOR = Unicode.OR + "";
	/** or */
	public static final String strXOR = Unicode.XOR + "";
	/** less equal */
	public static final String strLESS_EQUAL = Unicode.LESS_EQUAL + "";
	/** greater equal */
	public static final String strGREATER_EQUAL = Unicode.GREATER_EQUAL + "";
	/** = with question mark */
	public static final String strEQUAL_BOOLEAN = Unicode.QUESTEQ + "";
	/** not equal */
	public static final String strNOT_EQUAL = Unicode.NOTEQUAL + "";
	/** parallel */
	public static final String strPARALLEL = Unicode.PARALLEL + "";
	/** perpendicular */
	public static final String strPERPENDICULAR = Unicode.PERPENDICULAR + "";
	/** vector product */
	public static final String strVECTORPRODUCT = Unicode.VECTOR_PRODUCT + "";
	/** is element of */
	public static final String strIS_ELEMENT_OF = Unicode.IS_ELEMENT_OF + "";
	/** is subset of */
	public static final String strIS_SUBSET_OF = Unicode.IS_SUBSET_OF + "";
	/** strict subset of */
	public static final String strIS_SUBSET_OF_STRICT = Unicode.IS_SUBSET_OF_STRICT
			+ ""
			+ "";
	/** set difference */
	public static final String strSET_DIFFERENCE = "\\";

}
