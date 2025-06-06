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
