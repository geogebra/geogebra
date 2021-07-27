package org.geogebra.common.kernel.prover;

import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.plugin.Operation;

/**
 * @author Csilla Solyom-Gecse
 * @author Zoltan Kovacs
 *
 */
public class PolynomialNode {

	private PolynomialNode left;
	private PolynomialNode right;
	private PPolynomial poly;
	private Operation operation;

	/**
	 * Creates a polynomial node with an operation (root node) and left/right
	 * nodes.
	 * 
	 * @param left
	 *            the left node
	 * @param right
	 *            the right node
	 * @param operation
	 *            the operation
	 */
	public PolynomialNode(PolynomialNode left, PolynomialNode right,
			Operation operation) {
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	/**
	 * Creates an empty polynomial node.
	 */
	public PolynomialNode() {
	}

	/**
	 * Getter for the left node of the polynomial node.
	 * 
	 * @return the left node
	 */
	public PolynomialNode getLeft() {
		return left;
	}

	/**
	 * Sets the left node of the polynomial node.
	 * 
	 * @param left
	 *            the left node
	 */
	public void setLeft(PolynomialNode left) {
		this.left = left;
	}

	/**
	 * Getter for the right node of the polynomial node.
	 * 
	 * @return the right node
	 */
	public PolynomialNode getRight() {
		return right;
	}

	/**
	 * Sets the right node of the polynomial node.
	 * 
	 * @param right
	 *            the right node
	 */
	public void setRight(PolynomialNode right) {
		this.right = right;
	}

	/**
	 * Getter for the operation.
	 * 
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * Setter for the operation.
	 * 
	 * @param operation
	 *            the operation
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	/**
	 * Setter for the polynomial.
	 * 
	 * @param poly
	 *            the polynomial
	 */
	public void setPoly(PPolynomial poly) {
		this.poly = poly;
	}

	/**
	 * Getter for the polynomial.
	 * 
	 * @return the polynomial
	 */
	public PPolynomial getPoly() {
		return poly;
	}

	/**
	 * Converts the polynomial to Long.
	 * 
	 * @return the constant polynomial
	 */
	public Long evaluateLong() {
		return this.poly.getConstant().longValue();
	}

}
