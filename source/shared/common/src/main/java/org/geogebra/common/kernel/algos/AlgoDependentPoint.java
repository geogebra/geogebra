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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.Operation;

/**
 *
 * @author Markus
 */
public class AlgoDependentPoint extends AlgoElement
		implements DependentAlgo, SymbolicParametersBotanaAlgo {

	private GeoPoint P; // output

	private PVariable[] botanaVars;
	private PPolynomial[] botanaPolynomials;

	private GeoVec2D temp;

	/**
	 * @param cons
	 *            construction
	 * @param root
	 *            expression
	 * @param complex
	 *            whether output is complex
	 */
	public AlgoDependentPoint(Construction cons, ExpressionNode root,
			boolean complex) {
		super(cons);

		P = new GeoPoint(cons);
		P.setDefinition(root);

		setInputOutput(); // for AlgoElement

		if (complex) {
			P.setMode(Kernel.COORD_COMPLEX);
		}

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
		setInputFrom(P.getDefinition());
		setOnlyOutput(P);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoPoint getPoint() {
		return P;
	}

	@Override
	public ExpressionNode getExpression() {
		return P.getDefinition();
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {
			temp = ((VectorValue) P.getDefinition()
					.evaluate(StringTemplate.defaultTemplate)).getVector();
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

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaVars == null) {
			botanaVars = new PVariable[2];
			botanaVars[0] = new PVariable(kernel);
			botanaVars[1] = new PVariable(kernel);

			GeoElement left = (GeoElement) P.getDefinition().getLeft();
			GeoElement right = (GeoElement) P.getDefinition().getRight();
			Operation op = P.getDefinition().getOperation();
			if (op == Operation.NO_OPERATION && left != null) {
				PVariable[] leftBotanaVars = ((SymbolicParametersBotanaAlgo) left)
						.getBotanaVars(left);
				botanaPolynomials = new PPolynomial[2];
				botanaPolynomials[0] = new PPolynomial(botanaVars[0])
						.subtract(new PPolynomial(leftBotanaVars[0]));
				botanaPolynomials[1] = new PPolynomial(botanaVars[1])
						.subtract(new PPolynomial(leftBotanaVars[1]));
			}
			if (op == Operation.PLUS && left != null && right != null) {
				if (left instanceof GeoPoint && right instanceof GeoVector) {
					PVariable[] leftBotanaVars = ((SymbolicParametersBotanaAlgo) left)
							.getBotanaVars(left);
					PVariable[] rightBotanaVars = ((SymbolicParametersBotanaAlgo) right)
							.getBotanaVars(right);
					botanaPolynomials = new PPolynomial[2];
					/* P=left+right => P-left-right=0 */
					botanaPolynomials[0] = new PPolynomial(botanaVars[0])
							.subtract(new PPolynomial(leftBotanaVars[0]))
							.subtract(new PPolynomial(rightBotanaVars[0]));
					botanaPolynomials[1] = new PPolynomial(botanaVars[1])
							.subtract(new PPolynomial(leftBotanaVars[1]))
							.subtract(new PPolynomial(rightBotanaVars[1]));
				}
			}
			if (op == Operation.MINUS && left != null && right != null) {
				if (left instanceof GeoPoint && right instanceof GeoVector) {
					PVariable[] leftBotanaVars = ((SymbolicParametersBotanaAlgo) left)
							.getBotanaVars(left);
					PVariable[] rightBotanaVars = ((SymbolicParametersBotanaAlgo) right)
							.getBotanaVars(right);
					botanaPolynomials = new PPolynomial[2];
					/* P=left-right => P-left+right=0 */
					botanaPolynomials[0] = new PPolynomial(botanaVars[0])
							.subtract(new PPolynomial(leftBotanaVars[0]))
							.add(new PPolynomial(rightBotanaVars[0]));
					botanaPolynomials[1] = new PPolynomial(botanaVars[1])
							.subtract(new PPolynomial(leftBotanaVars[1]))
							.add(new PPolynomial(rightBotanaVars[1]));
				}
			}

			/* FIXME: This code does not handle *many* other cases yet. */
		}

		return botanaPolynomials;
	}
}
