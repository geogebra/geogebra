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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.geos.GeoVector;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoDependentVector extends AlgoElement implements DependentAlgo {

	private ExpressionNode root; // input
	private GeoVector v; // output

	private GeoVec2D temp;

	/** Creates new AlgoDependentVector */
	public AlgoDependentVector(Construction cons, String label,
			ExpressionNode root) {
		this(cons, root);
		v.setLabel(label);
	}

	public AlgoDependentVector(Construction cons, ExpressionNode root) {
		super(cons);
		this.root = root;

		v = new GeoVector(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		v.z = 0.0d;
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentVector;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = root.getGeoElementVariables();

		super.setOutputLength(1);
		super.setOutput(0, v);
		setDependencies(); // done by AlgoElement
	}

	public GeoVector getVector() {
		return v;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {
			temp = ((VectorValue) root.evaluate(StringTemplate.defaultTemplate)).getVector();
			v.x = temp.getX();
			v.y = temp.getY();
		} catch (Exception e) {
			v.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return root.toString(tpl);
	}

	// TODO Consider locusequability
}
