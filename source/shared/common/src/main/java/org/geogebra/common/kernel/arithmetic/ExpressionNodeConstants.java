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

import org.geogebra.editor.share.util.Unicode;

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
		SCREEN_READER_UNICODE,
		/** Linear Notation */
		LINEAR_NOTATION;

		/**
		 * @return Whether this is the giac type.
		 */
		public boolean isGiac() {
			return this == GIAC;
		}
	}

	/** prefix for CAS row reference ($) */
	String CAS_ROW_REFERENCE_PREFIX = "$";
	/** prefix used when serializing unicode characters to CAS */
	String UNICODE_PREFIX = "unicode";
	/** delimiter used when serializing unicode characters to CAS */
	String UNICODE_DELIMITER = "u";

	// int NO_OPERATION = Integer.MIN_VALUE;
	/** not */
	String strNOT = String.valueOf(Unicode.NOT);
	/** and */
	String strAND = String.valueOf(Unicode.AND);
	/** and */
	String strIMPLIES = String.valueOf(Unicode.IMPLIES);
	/** or */
	String strOR = String.valueOf(Unicode.OR);
	/** or */
	String strXOR = String.valueOf(Unicode.XOR);
	/** less equal */
	String strLESS_EQUAL = String.valueOf(Unicode.LESS_EQUAL);
	/** greater equal */
	String strGREATER_EQUAL = String.valueOf(Unicode.GREATER_EQUAL);
	/** = with question mark */
	String strEQUAL_BOOLEAN = String.valueOf(Unicode.QUESTEQ);
	/** not equal */
	String strNOT_EQUAL = String.valueOf(Unicode.NOTEQUAL);
	/** parallel */
	String strPARALLEL = String.valueOf(Unicode.PARALLEL);
	/** perpendicular */
	String strPERPENDICULAR = String.valueOf(Unicode.PERPENDICULAR);
	/** vector product */
	String strVECTORPRODUCT = String.valueOf(Unicode.VECTOR_PRODUCT);
	/** is element of */
	String strIS_ELEMENT_OF = String.valueOf(Unicode.IS_ELEMENT_OF);
	/** is subset of */
	String strIS_SUBSET_OF = String.valueOf(Unicode.IS_SUBSET_OF);
	/** strict subset of */
	String strIS_SUBSET_OF_STRICT = String.valueOf(Unicode.IS_SUBSET_OF_STRICT);
	/** set difference */
	String strSET_DIFFERENCE = "\\";

}
