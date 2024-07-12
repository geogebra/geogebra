/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentVector.java
 *
 * Created on 30. August 2001, 21:37
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
