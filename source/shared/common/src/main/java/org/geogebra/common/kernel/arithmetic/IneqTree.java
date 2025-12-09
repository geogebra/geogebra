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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	 * {@link #recomputeSize()} needed to make this up to date
	 * @return Number of inequalities in this tree
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

	/**
	 * @return A filtered list from all the encountered inequalities. If there exist two
	 * or more inequalities that share the same border, only returns one of the strict borders
	 * for that inequality.
	 */
	public List<Inequality> getPreferredBorders() {
		List<Inequality> borders = new ArrayList<>();
		for (Inequality inequality : getInequalities()) {
			boolean alreadyPresent = false;
			for (int j = 0; j < borders.size(); j++) {
				Inequality border = borders.get(j);
				if (inequality.isEqualBorder(border).boolVal()) {
					if (isPreferredBorder(border, inequality)) {
						borders.set(j, inequality);
					}
					alreadyPresent = true;
					break;
				}
			}
			if (!alreadyPresent) {
				borders.add(inequality);
			}
		}
		return borders;
	}

	private Set<Inequality> getInequalities() {
		recomputeSize();
		Set<Inequality> inequalities = new HashSet<>();
		for (int i = 0; i < size; i++) {
			inequalities.add(get(i));
		}
		return inequalities;
	}

	/**
	 * @return Whether {@code candidate} should replace {@code current} as border to be drawn.
	 */
	private boolean isPreferredBorder(Inequality current, Inequality candidate) {
		return (candidate.isStrict() && !current.isStrict() && operation == Operation.AND)
				|| (!candidate.isStrict() && current.isStrict() && operation == Operation.OR);
	}
}