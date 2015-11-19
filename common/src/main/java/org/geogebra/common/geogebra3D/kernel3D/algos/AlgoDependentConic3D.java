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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root; // input
	private GeoConic3D P; // output

	private ExpressionValue[] coeffX, coeffY, coeffZ;

	public AlgoDependentConic3D(Construction cons, String label,
			ExpressionNode root, ExpressionValue[] coeffX,
			ExpressionValue[] coeffY, ExpressionValue[] coeffZ) {
		this(cons, root, coeffX, coeffY, coeffZ);

		P.setLabel(label);

	}

	/** Creates new dependent conic */
	public AlgoDependentConic3D(Construction cons, ExpressionNode root,
			ExpressionValue[] coeffX, ExpressionValue[] coeffY,
			ExpressionValue[] coeffZ) {
		super(cons);
		this.root = root;
		this.coeffX = coeffX;
		this.coeffY = coeffY;
		this.coeffZ = coeffZ;
		P = new GeoConic3D(cons);

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
		input = ((Equation) root.unwrap()).getRHS().getGeoElementVariables();

		setOnlyOutput(P);
		setDependencies(); // done by AlgoElement
	}

	public GeoConic3D getConic3D() {
		return P;
	}

	public ExpressionNode getExpressionNode() {
		return root;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {

			ParametricProcessor3D.updateParabola(P, coeffX, coeffY, coeffZ);

			// P.setMode(temp.getMode());

		} catch (Exception e) {
			e.printStackTrace();
			P.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return root.toString(tpl);
	}

	// TODO Consider locusequability
}
