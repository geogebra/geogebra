package geogebra.kernel.arithmetic;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.Operation;
import geogebra.kernel.geos.GeoPoint;

import java.util.Set;

/**
 * Tree containing inequalities (possibly with NOT) in leaves and AND or OR
 * operations in other nodes
 * 
 * @author Zbynek Konecny
 * 
 */
public class IneqTree {
	IneqTree left;
	IneqTree right;
	Inequality ineq;
	Operation operation = Operation.NO_OPERATION;

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

	public boolean updateCoef() {
		if (ineq != null) {
			ineq.updateCoef();
			//Application.debug(ineq.getType());
			return ineq.getType() != Inequality.INEQUALITY_INVALID;
		}
		if (left == null && right == null)
			return false;
		boolean b = true;
		if (left != null)
			b &= left.updateCoef();
		if (right != null)
			b &= right.updateCoef();
		//Application.debug("tree" + b);
		return b;
	}

	private int size;

	public int getSize() {
		return size;
	}

	public Inequality get(int i) {
		if (ineq != null)
			return ineq;
		if (i < left.getSize())
			return left.get(i);
		return right.get(i - left.getSize());
	}

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

	public double[] getZeros(Set<Double> zeros) {
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
		return null;
	}
}