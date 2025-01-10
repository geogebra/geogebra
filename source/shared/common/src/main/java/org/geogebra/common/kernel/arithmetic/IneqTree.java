package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.Set;

import org.geogebra.common.kernel.arithmetic.Inequality.IneqType;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.ExtendedBoolean;

/**
 * Tree containing inequalities (possibly with NOT) in leaves and AND or OR
 * operations in other nodes
 * 
 * @author Zbynek Konecny
 * 
 */
public class IneqTree {
	private IneqTree left;
	private IneqTree right;
	private Inequality ineq;
	private Operation operation = Operation.NO_OPERATION;
	private int size;

	/**
	 * @param right
	 *            the right to set
	 */
	public void setRight(IneqTree right) {
		this.right = right;
	}

	/**
	 * @return the right
	 */
	public IneqTree getRight() {
		return right;
	}

	/**
	 * @param left
	 *            the left to set
	 */
	public void setLeft(IneqTree left) {
		this.left = left;
	}

	/**
	 * @return the left
	 */
	public IneqTree getLeft() {
		return left;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * @param ineq
	 *            the ineq to set
	 */
	public void setIneq(Inequality ineq) {
		this.ineq = ineq;
	}

	/**
	 * @return the ineq
	 */
	public Inequality getIneq() {
		return ineq;
	}

	/**
	 * Recomputes coefficients
	 * 
	 * @return true if this tree contains valid inequalities
	 */
	public boolean updateCoef() {
		if (ineq != null) {
			ineq.updateCoef();
			return ineq.getType() != IneqType.INEQUALITY_INVALID;
		}
		if (left == null && right == null) {
			return false;
		}
		boolean b = true;
		if (left != null) {
			b &= left.updateCoef();
		}
		if (right != null) {
			b &= right.updateCoef();
		}
		return b;
	}

	/**
	 * recomputeSize needed to make this up to date
	 * 
	 * @return number of inequalities in this tree
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param i
	 *            index
	 * @return i-th inequality in the tree in left to right order
	 */
	public Inequality get(int i) {
		if (ineq != null) {
			return ineq;
		}
		if (i < left.getSize()) {
			return left.get(i);
		}
		return right.get(i - left.getSize());
	}

	/**
	 * Make suregetSize()returns correct number
	 */
	public void recomputeSize() {
		if (ineq != null) {
			size = 1;
		} else {
			size = 0;
		}
		if (left != null) {
			left.recomputeSize();
			size += left.size;
		}
		if (right != null) {
			right.recomputeSize();
			size += right.size;
		}

	}

	/**
	 * @param zeros
	 *            set to which zeros should be added
	 */
	public void getZeros(Set<Double> zeros) {
		if (ineq != null) {
			ArrayList<Double> zeroPoints = ineq.getZeros();
			zeros.addAll(zeroPoints);
		}
		if (left != null) {
			left.getZeros(zeros);
		}
		if (right != null) {
			right.getZeros(zeros);
		}
	}

	/**
	 * @return whether all inequalities are valid
	 */
	public boolean isValid() {
		if (this.ineq != null
				&& this.ineq.getType() == IneqType.INEQUALITY_INVALID) {
			return false;
		}
		return (left == null || left.isValid())
				&& (right == null || right.isValid());
	}

	/**
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return value (TRUE/FALSE) in neighborhood of (x,y) if it's constant in the area,
	 *         UNKNOWN otherwise
	 */
	public ExtendedBoolean valueAround(double x, double y) {
		IneqTree expr = this;
		if (expr.getIneq() != null) {
			return expr.getIneq().valueAround(x, y);
		}
		ExtendedBoolean leftVal = expr.getLeft().valueAround(x, y);
		switch (expr.getOperation()) {
		case AND_INTERVAL:
		case AND:
			return leftVal.and(expr.getRight().valueAround(x, y));
		case OR:
			return leftVal.or(expr.getRight().valueAround(x, y));
		case IMPLICATION:
			return leftVal.and(expr.getRight().valueAround(x, y).negate()).negate();
		case NOT:
			return leftVal.negate();
		}
		return ExtendedBoolean.UNKNOWN;
	}
}