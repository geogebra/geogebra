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

package org.geogebra.common.export;

import org.geogebra.common.util.StringUtil;

public class MathmlTemplate {
	/**
	 * Add MATHM code of an operation.
	 * 
	 * @param sb
	 *            builder
	 * @param op
	 *            operation
	 * @param leftStr
	 *            left string
	 * @param rightStr
	 *            right string
	 */
	public static void mathml(StringBuilder sb, String op, String leftStr,
			String rightStr) {
		mathml(sb, op, "", leftStr, "", "", rightStr, "");
	}

	/**
	 * Add MATHM code of an operation.
	 * 
	 * @param sb
	 *            builder
	 * @param op
	 *            operation
	 * @param preL
	 *            left prefix
	 * @param leftStr
	 *            left string
	 * @param postL
	 *            left postfix
	 * @param preR
	 *            right prefix
	 * @param rightStr
	 *            right string
	 * @param postR
	 *            right postfix
	 */
	public static void mathml(StringBuilder sb, String op, String preL,
			String leftStr, String postL, String preR, String rightStr,
			String postR) {
		sb.append("<apply>");
		sb.append(op);
		sb.append(preL);

		if (leftStr.startsWith("<apply>")) {
			sb.append(leftStr);
		} else if (StringUtil.isNumber(leftStr)) {
			sb.append("<cn>");
			sb.append(leftStr);
			sb.append("</cn>");
		} else {
			sb.append("<ci>");
			sb.append(leftStr);
			sb.append("</ci>");
		}

		sb.append(postL);
		sb.append(preR);

		if (rightStr != null) {
			if (rightStr.startsWith("<apply>")) {
				sb.append(rightStr);
			} else if (StringUtil.isNumber(rightStr)) {
				sb.append("<cn>");
				sb.append(rightStr);
				sb.append("</cn>");
			} else {
				sb.append("<ci>");
				sb.append(rightStr);
				sb.append("</ci>");
			}
		}

		sb.append(postR);

		sb.append("</apply>");

	}
}
