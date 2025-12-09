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

import java.util.HashMap;
import java.util.Map;

public final class ListValueType implements ExpressionValueType {

	private final ExpressionValueType elementType;
	private final static Map<ExpressionValueType, ListValueType> listTypes = new HashMap<>();

	/**
	 * @param elementType the list element type
	 * @return structured type for list of objects of the given type
	 */
	public static ListValueType of(ExpressionValueType elementType) {
		return listTypes.computeIfAbsent(elementType, ListValueType::new);
	}

	private ListValueType(ExpressionValueType elementType) {
		this.elementType = elementType;
	}

	@Override
	public int getListDepth() {
		return elementType.getListDepth() + 1;
	}

	@Override
	public ExpressionValueType getElementType() {
		return elementType;
	}
}
