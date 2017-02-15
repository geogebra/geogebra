package org.geogebra.common.kernel.prover;

import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.plugin.Operation;

public class PolynomialNode {

	private PolynomialNode left;
	private PolynomialNode right;
	private PPolynomial poly;
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

	public void setPoly(PPolynomial poly) {
		this.poly = poly;
	}

	public PPolynomial getPoly() {
		return poly;
	}

	public Long evaluateLong() {
		return this.poly.getConstant().longValue();
	}
}
