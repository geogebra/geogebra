/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Algorithm for dependent numbers, e.g. c = a + b.
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class AlgoDependentNumber extends AlgoElement implements DependentAlgo {

	private ExpressionNode root; // input
	private GeoNumberValue number; // output

	/**
	 * Creates new AlgoJoinPoints
	 * 
	 * @param cons
	 * @param label
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 * */
	public AlgoDependentNumber(Construction cons, String label,
			ExpressionNode root, boolean isAngle, ExpressionValue evaluate) {
		this(cons, root, isAngle, evaluate);
		number.setLabel(label);
	}
	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle) {
		this(cons, root, isAngle, null);

	}

	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate) {
		super(cons);
		this.root = root;

		// simplify constant integers, e.g. -1 * 300 becomes -300
		root.simplifyConstantIntegers();
		if (evaluate instanceof GeoNumberValue) {
			// fix error with a=7, b = a renaming a instead of creating b
			number = (GeoNumberValue) ((GeoNumberValue) evaluate)
					.copyInternal(cons);
		} else if (isAngle) {
			number = new GeoAngle(cons);

			// check fileloading to make loading old files (<=4.2) works
			// no allowReflexAngle or forceReflexAngle in XML by default
			if (!cons.isFileLoading() || cons.isAllowUnboundedAngles()) {
				// make sure eg summing angles of polygon a+b+c+d gives correct
				// answer
				((GeoAngle) number).setAngleStyle(AngleStyle.UNBOUNDED);
			}
		} else {
			number = new GeoNumeric(cons);
		}
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
		input = root.getGeoElementVariables();
		if (input == null) {
			input = new GeoElement[0];
		}
		setOutputLength(1);
		setOutput(0, number.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	public GeoNumberValue getNumber() {
		return number;
	}

	public ExpressionNode getExpression() {
		return root;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {
			NumberValue nv = (NumberValue) root
					.evaluate(StringTemplate.defaultTemplate);
			if (number instanceof GeoNumeric) {
				((GeoNumeric) number).setValue(nv.getDouble());
			} else {
				number.set(nv.toGeoElement());
			}
		} catch (Throwable e) {
			number.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// was defined as e.g. r = 5a - 3b
		// return 5a - 3b
		return root.toString(tpl);
	}

	// TODO Consider locusequability
}
