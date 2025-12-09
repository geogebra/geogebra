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
