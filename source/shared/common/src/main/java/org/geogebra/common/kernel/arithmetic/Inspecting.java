package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.Operation;

/**
 * Allows checking whether at least one part of a structured expression value has a
 * certain property.
 */
public interface Inspecting {
	/**
	 * Do the local check
	 * @param value expression
	 * @return {@code true} whether this expression itself has given property (not the
	 * subparts)
	 */
	boolean check(ExpressionValue value);

	/**
	 * @param value value to test
	 * @return {@code true} if the expression contains operations &lt; ,&lt;=, &gt;, &gt;=
	 */
	static boolean isInequality(ExpressionValue value) {
		return value.isExpressionNode()
				&& ((ExpressionNode) value).getOperation().isInequality();
	}

	/**
	 * @param value value to test
	 * @return {@code true} if the value is a {@link Command}
	 */
	static boolean isCommand(ExpressionValue value) {
		return value instanceof Command;
	}

	/**
	 * @param value value to test
	 * @return {@code true} if the value is a complex number
	 */
	static boolean isComplexNumber(ExpressionValue value) {
		return ExpressionNode.isImaginaryUnit(value);
	}

	/**
	 * @param value value to test
	 * @return {@code true} if value is a division of vectors
	 */
	static boolean isVectorDivision(ExpressionValue value) {
		if (value.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) value;
			if (en.getOperation() == Operation.DIVIDE) {
				return en.getRightTree().evaluatesToNDVector()
						&& en.getLeftTree().evaluatesToNDVector();
			}
		}
		return false;
	}

	/**
	 * @param value value to test
	 * @return {@code true} if the value is a {@link GeoElement} and has
	 * some kind of dependency (e.g. algo dependency, random element)
	 * @apiNote Instead of isConstant we sometimes (always?) want to check only for Geos
	 * that are not labeled, symbolic or dependent ie we don't need to
	 * distinguish between MyDouble(1) and GeoNumeric(1)
	 */
	static boolean isDynamicGeoElement(ExpressionValue value) {
		if (!value.isGeoElement()) {
			return false;
		}
		GeoElement geo = (GeoElement) value;
		return !geo.isIndependent() || geo.isLabelSet()
				|| geo.isLocalVariable() || value instanceof GeoDummyVariable
				|| geo.isGeoCasCell() || geo.isRandomGeo();
	}

	/**
	 * @param value value to test
	 * @return {@code true} if value is a text type
	 */
	static boolean isText(ExpressionValue value) {
		return value instanceof GeoText || value instanceof MyStringBuffer;
	}

	/**
	 * @param value value to test
	 * @return {@code true} if value is {@link MySpecialDouble}
	 */
	static boolean isMySpecialDouble(ExpressionValue value) {
		return value instanceof MySpecialDouble;
	}

	/**
	 * @param value value to test
	 * @return {@code true} if value is not defined
	 */
	static boolean isUndefined(ExpressionValue value) {
		return value instanceof NumberValue && !((NumberValue) value).isDefined();
	}

	/**
	 * @param value value to test
	 * @return {@code true} if value is a {@link FunctionVariable}
	 */
	static boolean isFunctionVariable(ExpressionValue value) {
		return value instanceof FunctionVariable;
	}
}