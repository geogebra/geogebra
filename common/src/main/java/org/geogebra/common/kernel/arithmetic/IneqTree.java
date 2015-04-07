package org.geogebra.common.kernel.arithmetic;

import java.util.Set;

import org.geogebra.common.kernel.arithmetic.Inequality.IneqType;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.Operation;

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
		if (left == null && right == null)
			return false;
		boolean b = true;
		if (left != null)
			b &= left.updateCoef();
		if (right != null)
			b &= right.updateCoef();
		return b;
	}

	private int size;

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
		if (ineq != null)
			return ineq;
		if (i < left.getSize())
			return left.get(i);
		return right.get(i - left.getSize());
	}

	/**
	 * Make suregetSize()returns correct number
	 */
	public void recomputeSize() {
		if (ineq != null) {
			size = 1;
		} else
			size = 0;
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
			GeoPoint[] zeroPoints = ineq.getZeros();
			for (int i = 0; i < zeroPoints.length; i++) {
				zeros.add(zeroPoints[i].getX());
			}
		}
		if (left != null) {
			left.getZeros(zeros);
		}
		if (right != null) {
			right.getZeros(zeros);
		}
	}
}