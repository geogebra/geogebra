/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentPoint.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 *
 * @author Markus
 */
public class AlgoDependentPoint extends AlgoElement implements DependentAlgo,
		SymbolicParametersBotanaAlgo {

	private GeoPoint P; // output

	private Variable[] botanaVars;
	private GeoVec2D temp;

	/**
	 * Creates new AlgoJoinPoints
	 * 
	 * @param cons
	 * @param label
	 * @param root
	 *            expression defining the result
	 * @param complex
	 *            true if result is complex number
	 */
	public AlgoDependentPoint(Construction cons, String label,
			ExpressionNode root, boolean complex) {
		this(cons, root, complex);
		P.setLabel(label);
	}

	public AlgoDependentPoint(Construction cons, ExpressionNode root,
			boolean complex) {
		super(cons);

		P = new GeoPoint(cons);
		P.setDefinition(root);

		setInputOutput(); // for AlgoElement

		if (complex)
			P.setMode(Kernel.COORD_COMPLEX);

		// compute value of dependent number
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = P.getDefinition().getGeoElementVariables();

		setOutputLength(1);
		setOutput(0, P);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getPoint() {
		return P;
	}

	public ExpressionNode getExpression() {
		return P.getDefinition();
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {
			temp = ((VectorValue) P.getDefinition().evaluate(
					StringTemplate.defaultTemplate))
					.getVector();
			if (Double.isInfinite(temp.getX())
					|| Double.isInfinite(temp.getY())) {
				P.setUndefined();
			} else {
				ExpressionNode def = P.getDefinition();
				P.setCoords(temp.getX(), temp.getY(), 1.0);
				P.setDefinition(def);
			}

			// P.setMode(temp.getMode());

		} catch (Exception e) {
			P.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return P.getDefinition() == null ? "?"
				: P.getDefinition().toString(tpl);
	}

	public Variable[] getBotanaVars(GeoElementND geo) {
		GeoElement left = (GeoElement) P.getDefinition().getLeft();
		// GeoElement right = (GeoElement) root.getRight();
		if (left != null) {
			botanaVars = ((SymbolicParametersBotanaAlgo) left)
					.getBotanaVars(left);
		}
		return botanaVars;
	}
	

	public Polynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		return null;
	}
}
