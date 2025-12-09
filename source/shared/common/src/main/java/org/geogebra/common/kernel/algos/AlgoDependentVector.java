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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVector;

/**
 * 
 * @author Markus
 */
public class AlgoDependentVector extends AlgoElement implements DependentAlgo {

	private GeoVector v; // output

	private GeoVec2D temp;

	/**
	 * @param cons
	 *            construction
	 * @param root
	 *            expression
	 */
	public AlgoDependentVector(Construction cons, ExpressionNode root) {
		super(cons);

		v = new GeoVector(cons);
		v.setDefinition(root);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		v.z = 0.0d;
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInputFrom(v.getDefinition());
		setOnlyOutput(v);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoVector getVector() {
		return v;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {
			temp = ((VectorValue) v.getDefinition()
					.evaluate(StringTemplate.defaultTemplate)).getVector();
			v.x = temp.getX();
			v.y = temp.getY();
		} catch (Exception e) {
			v.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return v.getDefinition().toString(tpl);
	}

	@Override
	public ExpressionNode getExpression() {
		return v.getDefinition();
	}

}
