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
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.DependentNumberAdapter;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.Operation;

/**
 * Algorithm for dependent numbers, e.g. c = a + b.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentNumber extends AlgoElement
		implements DependentAlgo, SymbolicParametersBotanaAlgo, SetRandomValue {

	private GeoNumberValue number; // output

	private boolean rewriteFormula = true;

	private DependentNumberAdapter proverAdapter;

	/**
	 * Creates new AlgoDependentNumber
	 * 
	 * @param cons
	 *            construction
	 * 
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 */

	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle) {
		this(cons, root, isAngle, null, true);
	}

	/**
	 * Creates new AlgoDependentNumber
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 * @param evaluate
	 *            pre-evaluated result
	 */
	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate) {
		this(cons, root, isAngle, evaluate, true);
	}

	/**
	 * Creates new AlgoDependentNumber
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 * @param evaluate
	 *            pre-evaluated result
	 * @param addToConstructionList
	 *            add object to the construction list
	 */
	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate,
			boolean addToConstructionList) {
		this(cons, root, isAngle, evaluate, addToConstructionList, true);
	}

	/**
	 * Creates new AlgoDependentNumber
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            expression defining the result
	 * @param isAngle
	 *            true for angles
	 * @param evaluate
	 *            pre-evaluated result
	 * @param addToConstructionList
	 *            add object to the construction list
	 * @param rewrite
	 *            rewrite the related formulas in other geos
	 */
	public AlgoDependentNumber(Construction cons, ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate,
			boolean addToConstructionList, boolean rewrite) {
		super(cons, addToConstructionList);
		rewriteFormula = rewrite;
		if (evaluate instanceof GeoNumberValue) {
			// fix error with a=7, b = a renaming a instead of creating b
			number = (GeoNumberValue) ((GeoNumberValue) evaluate)
					.copyInternal(cons);
			// just a copy of segment / polygon / arc, not eg. list1(1)
			if (!number.isGeoNumeric() && root.unwrap().isGeoElement()) {
				number.setEuclidianVisible(false);
			}
		} else if (isAngle) {
			number = new GeoAngle(cons);

			// check fileloading to make loading old files (<=4.2) works
			// no allowReflexAngle or forceReflexAngle in XML by default
			if (!cons.isFileLoading() || cons.isAllowUnboundedAngles()) {
				// make sure eg summing angles of polygon a+b+c+d gives correct
				// answer
				((GeoAngle) number).setAngleStyle(AngleStyle.UNBOUNDED);
			}
			// dependent angles are not drawable
			((GeoAngle) number).setDrawable(false, false);
		} else {
			number = new GeoNumeric(cons);
		}
		number.toGeoElement().setDefinition(root);
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
		setInputFrom(number.getDefinition());
		setOutputLength(1);
		setOutput(0, number.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting number
	 */
	public GeoNumberValue getNumber() {
		return number;
	}

	@Override
	public ExpressionNode getExpression() {
		return number.getDefinition();
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		if (!rewriteFormula) {
			return;
		}
		try {
			NumberValue nv = (NumberValue) number.getDefinition()
					.evaluate(StringTemplate.defaultTemplate);
			ExpressionNode def = number.getDefinition();
			if (number instanceof GeoNumeric) {
				((GeoNumeric) number).setValue(nv.getDouble());
			} else {
				number.set(nv.toGeoElement(cons));
			}
			number.setDefinition(def);
		} catch (Throwable e) {
			number.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// was defined as e.g. r = 5a - 3b
		// return 5a - 3b
		return number.getDefinition().toString(tpl);
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return getProverAdapter().getBotanaVars();
	}

	/**
	 * @return prover adapter
	 */
	public DependentNumberAdapter getProverAdapter() {
		if (proverAdapter == null) {
			proverAdapter = new DependentNumberAdapter();
		}
		return proverAdapter;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		return getProverAdapter().getBotanaPolynomials(this, geo);
	}

	@Override
	public boolean mayShowDescriptionInsteadOfDefinition() {
		return false;
	}

	@Override
	public boolean setRandomValue(GeoElementND d) {
		if (number.getDefinition() != null
				&& number.getDefinition().getOperation() == Operation.RANDOM) {
			double val = Math.min(Math.max(d.evaluateDouble(), 0), 1);
			((GeoNumeric) number.getDefinition().getLeft())
					.setValue(val);
			number.getDefinition().reset();
			((GeoNumeric) number).setValue(val);
			return true;
		}
		return false;
	}

	/**
	 * @return Rewrite formulas appearing in other geos to contain GeoGebra
	 *         definitions. E.g. when entering a+2b, convert this formula to
	 *         Segment[A,B] + 2Segment[C,D] in all other occurrences. Sometimes
	 *         this is not what we want, e.g. on creating formulas from the
	 *         prover automatically.
	 */
	public boolean isRewriteFormula() {
		return rewriteFormula;
	}

}
