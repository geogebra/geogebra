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

import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Possible value types of expression after evaluation
 *
 */
public enum ValueType implements ExpressionValueType {
	/**
	 * Has no algebraic properties, eg a button
	 */
	VOID,
	/**
	 * cannot be determined (eg a variable)
	 */
	UNKNOWN,
	/**
	 * Bool
	 */
	BOOLEAN,
	/**
	 * Number
	 */
	NUMBER,
	/**
	 * 2D point or vector (cartesian or polar)
	 */
	NONCOMPLEX2D,
	/**
	 * Complex point or vector
	 */
	COMPLEX,
	/**
	 * 3D point or vector
	 */
	VECTOR3D,
	/**
	 * Equation
	 */
	EQUATION,
	/**
	 * Text
	 */
	TEXT,
	/**
	 * Function R^n -&gt; R
	 */
	FUNCTION,
	/**
	 * Function R^n -&gt; R^2
	 */
	PARAMETRIC2D,
	/**
	 * Function R^n -&gt; R^3
	 */
	PARAMETRIC3D;

	/**
	 * @param op
	 *            operation
	 * @param left
	 *            left argument
	 * @param right
	 *            right argument
	 * @return expected type
	 */
	public static ExpressionValueType resolve(Operation op, ExpressionValue left,
			ExpressionValue right) {

		switch (op) {
		case PLUS:
		case INVISIBLE_PLUS:
			if (right.evaluatesToText()) {
				return ValueType.TEXT;
			}
			return plusMinusType(left, right);
		case MINUS:
			return plusMinusType(left, right);
		case MULTIPLY:
			ExpressionValueType leftType = left.getValueType();
			ExpressionValueType rightType = right.getValueType();

			// eg Evaluate((x,y)*(({{1,2},{4,5}})*(x,y))+({6,7})*(x,y))
			// (x,y,z)(({{1,2,3},{4,5,6},{7,8,9}})(x,y,z))+({10,11,12})(x,y,z)
			if (leftType == ValueType.TEXT || leftType.getListDepth() > 0) {
				return leftType;
			}

			if (rightType == ValueType.TEXT || rightType.getListDepth() > 0) {
				if (leftType == ValueType.NONCOMPLEX2D && right.getListDepth() == 2) {
					return leftType;
				}
				return rightType;
			}

			// scalar product
			if ((rightType == ValueType.NONCOMPLEX2D
					|| rightType == ValueType.VECTOR3D)
					&& (leftType == ValueType.NONCOMPLEX2D
							|| leftType == ValueType.VECTOR3D)) {
				return ValueType.NUMBER;
			}
			// number * vector
			if (rightType == ValueType.NONCOMPLEX2D
					|| rightType == ValueType.VECTOR3D) {
				return rightType;
			}
			return leftType;
		case DIVIDE:
			if (right.evaluatesToList()) {
				return right.getValueType();
			}
			if (right.getValueType() == COMPLEX) {
				return COMPLEX;
			}
			return left.getValueType();
		case DOLLAR_VAR_COL:
		case DOLLAR_VAR_ROW:
		case DOLLAR_VAR_ROW_COL:
		case NO_OPERATION:
			return left.getValueType();

		case SGN:
		case ABS:
		case ALT:
		case ARG:
			return checkList(left, ValueType.NUMBER);

		case ARBCOMPLEX:
			return ValueType.COMPLEX;

		case CONJUGATE:
			return checkList(left, ValueType.COMPLEX);

		case LOGB:
		case NROOT:
		case ROUND2:
		case ARCTAN2:
			return ValueType.COMPLEX.check(left, checkList(left,
					checkList(right, ValueType.NUMBER)));

		case ACOSH:
		case ASINH:
		case ARCCOS:
		case ARCSIN:
		case ARCSIND:
		case ARCTAN:
		case ATANH:
		case ROUND:
		case SI:
		case SIN:
		case SINH:
		case SQRT:
		case SQRT_SHORT:
		case BETA:
		case BETA_INCOMPLETE:
		case BETA_INCOMPLETE_REGULARIZED:
		case CBRT:
		case CI:
		case CEIL:
		case COS:
		case COSH:
		case COT:
		case COTH:
		case SEC:
		case SECH:
		case CSC:
		case CSCH:
		case EI:
		case TAN:
		case TANH:
		case ERF:
		case EXP:
		case FACTORIAL:
		case FLOOR:
		case FRACTIONAL_PART:
		case GAMMA:
		case GAMMA_INCOMPLETE:
		case GAMMA_INCOMPLETE_REGULARIZED:
		case LOG:
		case LOG10:
		case LOG2:
			return ValueType.COMPLEX.check(left,
					checkList(left, ValueType.NUMBER));
		case FUNCTION:
			return ValueType.COMPLEX.check(right,
					checkList(right, ValueType.NUMBER));
		case FUNCTION_NVAR:
		case FREEHAND:
		case DATA:
		case ARBCONST:
		case ARBINT:
			return ValueType.NUMBER;
		case DIFF:
		case DERIVATIVE:
			return ValueType.FUNCTION;

		case IF:
		case IF_SHORT:
			return right.getValueType();
		case IF_ELSE:
			return right.getValueType();
		case IF_LIST:
			if (right instanceof ListValue && ((ListValue) right).size() > 0) {
				return ((ListValue) right).get(0).getValueType();
			}
			break;

		case INTEGRAL:
			break;

		case MATRIXTOVECTOR:
			if (!(left.unwrap() instanceof MyList)) {
				return left.getValueType();
			}

			MyList list = (MyList) left.unwrap();
			return list.size() == 3 ? ValueType.VECTOR3D
					: ValueType.NONCOMPLEX2D;
		case MULTIPLY_OR_FUNCTION:
			break;
		case IS_ELEMENT_OF:
		case IS_SUBSET_OF:
		case IS_SUBSET_OF_STRICT:
		case NOT_EQUAL:
		case EQUAL_BOOLEAN:
			return ValueType.BOOLEAN;
		case ELEMENT_OF:
			if (right instanceof ListValue) {
				ExpressionValueType elementType = left.getValueType();
				for (int i = 0; i < ((ListValue) right).size(); i++) {
					elementType = elementType.getElementType();
				}
				return elementType;
			}
			return ValueType.UNKNOWN;
		case NOT:
			return checkList(left, ValueType.BOOLEAN);
		case OR:
		case AND:
		case XOR:
		case IMPLICATION:
		case AND_INTERVAL:
		case PARALLEL:
		case PERPENDICULAR:
		case LESS:
		case LESS_EQUAL:
		case GREATER:
		case GREATER_EQUAL:
			return checkList(left,
					checkList(right, ValueType.BOOLEAN));
		case LAMBERTW:
		case POLYGAMMA:
			break;
		case POWER:
			if ((left.getValueType() == ValueType.NONCOMPLEX2D
					|| left.getValueType() == ValueType.VECTOR3D)
					&& DoubleUtil.isEqual(2, right.evaluateDouble())) {
				return ValueType.NUMBER;
			}
			return left.getValueType();
		case PSI:
			break;

		case SET_DIFFERENCE:
			return left.getValueType();

		case SUBSTITUTION:
			break;
		case INVERSE_NORMAL:
		case SUM:
		case PRODUCT:
			break;
		case SEQUENCE:
			return ListValueType.of(ValueType.NUMBER);
		case VECTORPRODUCT:
			return ValueType.VECTOR3D.check(left, ValueType.NUMBER);
		case VEC_FUNCTION:
			if (left.unwrap() instanceof GeoCurveCartesian) {
				return ValueType.NONCOMPLEX2D;
			}
			if (left.unwrap() instanceof GeoCurveCartesianND) {
				return ValueType.VECTOR3D;
			}
			break;
		case XCOORD:
		case YCOORD:
		case ZCOORD:
			return checkList(left, ValueType.NUMBER);
		case REAL:
		case IMAGINARY:
		case RANDOM:
			return ValueType.NUMBER;
		case ZETA:
			break;

		default:
			Log.error("missing case in doResolve(): " + op);
			break;
		}
		return ValueType.NUMBER;
	}

	private static ExpressionValueType plusMinusType(ExpressionValue left,
			ExpressionValue right) {
		if (right.evaluatesToList()) {
			return right.getValueType();
		}
		if (right.getValueType() == ValueType.VECTOR3D
				|| left.getValueType() == ValueType.VECTOR3D) {
			return ValueType.VECTOR3D;
		}
		if (right.getValueType() == ValueType.NONCOMPLEX2D
				|| left.getValueType() == ValueType.NONCOMPLEX2D) {
			return ValueType.NONCOMPLEX2D;
		}
		return COMPLEX.check(right, left.getValueType());
	}

	private ExpressionValueType check(ExpressionValue arg, ExpressionValueType fallback) {
		return arg.getValueType() == this ? this : fallback;
	}

	private static ExpressionValueType checkList(ExpressionValue arg,
			ExpressionValueType fallback) {
		return arg.getValueType().getListDepth() > 0 ? ListValueType.of(fallback) : fallback;
	}

	public boolean isVector() {
		return this == NONCOMPLEX2D || this == VECTOR3D || this == COMPLEX;
	}

	@Override
	public int getListDepth() {
		return 0;
	}

	@Override
	public ExpressionValueType getElementType() {
		return UNKNOWN;
	}
}
