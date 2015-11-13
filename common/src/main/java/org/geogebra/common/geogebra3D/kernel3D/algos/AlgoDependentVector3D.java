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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.DependentAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;

/**
 *
 * @author Michael
 * @version
 */
public class AlgoDependentVector3D extends AlgoElement3D implements
		DependentAlgo {

	private GeoVector3D vec; // output

	private double[] temp;

	/** Creates new AlgoDependentVector */
	public AlgoDependentVector3D(Construction cons, String label,
			ExpressionNode root) {

		this(cons, root);

		vec.setLabel(label);
	}

	/** Creates new AlgoDependentVector */
	public AlgoDependentVector3D(Construction cons, ExpressionNode root) {
		super(cons);


		vec = new GeoVector3D(cons);
		vec.setDefinition(root);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		// v.z = 0.0d;
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = vec.getDefinition().getGeoElementVariables();

		setOnlyOutput(vec);
		setDependencies(); // done by AlgoElement
	}

	public GeoVector3D getVector3D() {
		return vec;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		ExpressionNode def = vec.getDefinition();
		try {
			temp = ((Vector3DValue) vec.getDefinition()
					.evaluate(StringTemplate.defaultTemplate))
					.getPointAsDouble();

			vec.setCoords(temp);
		} catch (Exception e) {
			vec.setUndefined();
		}
		vec.setDefinition(def);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return vec.getDefinition().toString(tpl);
	}

	// TODO Consider locusequability
}
