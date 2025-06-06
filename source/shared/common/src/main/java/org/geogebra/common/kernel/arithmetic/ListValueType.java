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
