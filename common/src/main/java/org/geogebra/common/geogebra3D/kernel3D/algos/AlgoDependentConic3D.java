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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.commands.ParametricProcessor3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDependentConic3D extends AlgoElement3D {

	private GeoConic3D conic; // output

	private ExpressionValue[] coeffX, coeffY, coeffZ;

	private boolean trig;

	/** Creates new dependent conic */
	public AlgoDependentConic3D(Construction cons, ExpressionNode root,
			ExpressionValue[] coeffX, ExpressionValue[] coeffY,
			ExpressionValue[] coeffZ, boolean trig) {
		super(cons);
		conic = new GeoConic3D(cons);
		conic.setDefinition(root);
		this.coeffX = coeffX;
		this.coeffY = coeffY;
		this.coeffZ = coeffZ;
		this.trig = trig;


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
		input = ((Equation) conic.getDefinition().unwrap()).getRHS()
				.getGeoElementVariables();

		setOnlyOutput(conic);
		setDependencies(); // done by AlgoElement
	}

	public GeoConic3D getConic3D() {
		return conic;
	}

	public ExpressionNode getExpressionNode() {
		return conic.getDefinition();
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {
			ExpressionNode def = conic.getDefinition();
			if (trig) {
				ParametricProcessor3D.updateTrigConic(conic, coeffX, coeffY,
						coeffZ);
			} else {
				ParametricProcessor3D.updateParabola(conic, coeffX, coeffY,
						coeffZ);
			}
			conic.setDefinition(def);
			// P.setMode(temp.getMode());

		} catch (Exception e) {
			e.printStackTrace();
			conic.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return conic.getDefinition().toString(tpl);
	}

	// TODO Consider locusequability
}
