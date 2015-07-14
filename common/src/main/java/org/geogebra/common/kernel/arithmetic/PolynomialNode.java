package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.plugin.Operation;

public class PolynomialNode {

	private PolynomialNode left;
	private PolynomialNode right;
	private org.geogebra.common.kernel.prover.polynomial.Polynomial poly;
	private Operation operation;

	public PolynomialNode(PolynomialNode left, PolynomialNode right,
			Operation operation) {
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	public PolynomialNode() {
	}

	public PolynomialNode getLeft() {
		return left;
	}

	public void setLeft(PolynomialNode left) {
		this.left = left;
	}

	public PolynomialNode getRight() {
		return right;
	}

	public void setRight(PolynomialNode right) {
		this.right = right;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public void setPoly(
			org.geogebra.common.kernel.prover.polynomial.Polynomial poly) {
		this.poly = poly;
	}

	public org.geogebra.common.kernel.prover.polynomial.Polynomial getPoly() {
		return poly;
	}

	public Integer evaluateInteger() {
		return this.poly.getConstant();
	}
}
