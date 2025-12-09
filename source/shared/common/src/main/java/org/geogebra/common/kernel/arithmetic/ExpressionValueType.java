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

/**
 * Value type for ExpressionValues. Can be structured ({@link ListValueType})
 * or simple ({@link ValueType})
 */
public interface ExpressionValueType {
	/**
	 * @return level of list nesting. Always 0 for simple types.
	 */
	int getListDepth();

	/**
	 * If this is a structured type, return the type of its elements.
	 * @return element type if relevant, {@link ValueType#UNKNOWN} otherwise
	 */
	ExpressionValueType getElementType();
}
