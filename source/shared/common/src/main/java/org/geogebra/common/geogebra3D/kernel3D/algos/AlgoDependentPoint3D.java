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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.DependentAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;

/**
 *
 * @author Markus
 */
public class AlgoDependentPoint3D extends AlgoElement3D
		implements DependentAlgo {

	private GeoPoint3D P; // output

	private double[] temp;

	/**
	 * Creates new dependent 3D point
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            point expression
	 */
	public AlgoDependentPoint3D(Construction cons, ExpressionNode root,
			boolean addToConsList) {
		super(cons, addToConsList);

		P = new GeoPoint3D(cons);
		P.setDefinition(root);

		setInputOutput(); // for AlgoElement

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
	public GeoPoint3D getPoint3D() {
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
			temp = ((Vector3DValue) P.getDefinition()
					.evaluate(StringTemplate.defaultTemplate))
							.getPointAsDouble();
			if (Double.isInfinite(temp[0]) || Double.isInfinite(temp[1])
					|| Double.isInfinite(temp[2])) {
				P.setUndefined();
			} else {
				ExpressionNode def = P.getDefinition();
				P.setCoords(temp[0], temp[1], temp[2], 1.0);
				P.setDefinition(def);
			}

			// P.setMode(temp.getMode());

		} catch (Exception e) {
			P.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return P.getDefinition().toString(tpl);
	}

}
