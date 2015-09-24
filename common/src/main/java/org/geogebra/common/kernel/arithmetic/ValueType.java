package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;

public enum ValueType {
	VOID, UNKNOWN, BOOLEAN, NUMBER, NONCOMPLEX2D, COMPLEX, VECTOR3D, EQUATION, TEXT, LIST, FUNCTION, PARAMETRIC2D, PARAMETRIC3D;

	/**
	 * @param op
	 *            operation
	 * @param left
	 *            left argument
	 * @param right
	 *            right argument
	 * @return expected type
	 */
	public static ValueType resolve(Operation op, ExpressionValue left,
			ExpressionValue right, Resolution res) {
		if (left != null) {
			res.setListDepth(left.getListDepth());
		}
		switch (op) {
		case PLUS:
			if (right.evaluatesToText()) {
				return ValueType.TEXT;
			}
			if (right.evaluatesToList()) {
				return ValueType.LIST;
			}
			if (right.getValueType() == ValueType.NONCOMPLEX2D
					|| left.getValueType() == ValueType.NONCOMPLEX2D) {
				return ValueType.NONCOMPLEX2D;
			}
			return left.getValueType();
		case MINUS:
			if (right.evaluatesToList()) {
				return ValueType.LIST;
			}
			if (right.getValueType() == ValueType.NONCOMPLEX2D
					|| left.getValueType() == ValueType.NONCOMPLEX2D) {
				return ValueType.NONCOMPLEX2D;
			}
			return left.getValueType();
		case MULTIPLY:
			if (right.evaluatesToText()) {
				return ValueType.TEXT;
			}
			if (right.evaluatesToList()) {
				return ValueType.LIST;
			}
			// scalar product
			if (right.getValueType() == ValueType.NONCOMPLEX2D
					&& left.getValueType() == ValueType.NONCOMPLEX2D) {
				return ValueType.NUMBER;
			}
			return left.getValueType();
		case DIVIDE:
			if (right.evaluatesToList()) {
				return ValueType.LIST;
			}
			return left.getValueType();
		case $VAR_COL:
		case $VAR_ROW:
		case $VAR_ROW_COL:
		case NO_OPERATION:
			return left.getValueType();

		case SGN:
		case ABS:
		case ALT:
		case ARG:
			return ValueType.LIST.check(left, ValueType.NUMBER);

		case ARBCOMPLEX:
			return ValueType.COMPLEX;

		case CONJUGATE:
			return ValueType.LIST.check(left, ValueType.COMPLEX);

		case LOGB:
		case NROOT:
		case ROUND2:
		case ARCTAN2:
			return ValueType.COMPLEX.check(
					left,
					ValueType.LIST.check(left,
							ValueType.LIST.check(right, ValueType.NUMBER)));

		case ACOSH:
		case ASINH:
		case ARCCOS:
		case ARCSIN:
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
		case FUNCTION:
			return ValueType.COMPLEX.check(left,
					ValueType.LIST.check(left, ValueType.NUMBER));
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
			return left.getValueType();
		case IF_ELSE:
			return right.getValueType();
		case IF_LIST:
			if (right instanceof ListValue && ((ListValue) right).size() > 0) {
				return ((ListValue) right).getListElement(0).getValueType();
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
		case ELEMENT_OF:
			return ValueType.BOOLEAN;
		case NOT:
			return ValueType.LIST.check(left, ValueType.BOOLEAN);
		case OR:
		case AND:
		case IMPLICATION:
		case AND_INTERVAL:
		case PARALLEL:
		case PERPENDICULAR:
		case LESS:
		case LESS_EQUAL:
		case GREATER:
		case GREATER_EQUAL:
			return ValueType.LIST.check(left,
					ValueType.LIST.check(right, ValueType.BOOLEAN));
		case POLYGAMMA:
			break;
		case POWER:
			if (left.getValueType() == ValueType.NONCOMPLEX2D
					&& Kernel.isEqual(2, right.evaluateDouble())) {
				return ValueType.NUMBER;
			}
			return left.getValueType();
		case PSI:
			break;

		case SET_DIFFERENCE:
			return ValueType.LIST;

		case SUBSTITUTION:
			break;
		case SUM:
			break;

		case VECTORPRODUCT:
			ValueType.VECTOR3D.check(left, ValueType.NUMBER);
		case VEC_FUNCTION:
			break;
		case XCOORD:
		case YCOORD:
		case ZCOORD:
		case REAL:
		case IMAGINARY:
		case RANDOM:
			return ValueType.NUMBER;

		case ZETA:
			break;

		}
		return ValueType.NUMBER;
	}

	private ValueType check(ExpressionValue arg, ValueType fallback) {
		return arg.getValueType() == this ? this : fallback;
	}

}
