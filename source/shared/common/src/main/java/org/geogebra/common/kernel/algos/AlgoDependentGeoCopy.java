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
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;

/**
 * Creates a dependent copy of the given GeoElement.
 */
public class AlgoDependentGeoCopy extends AlgoElement implements DependentAlgo {

	private ExpressionNode origGeoNode;
	private GeoElement origGeo; // input
	private GeoElement copyGeo; // output

	/**
	 * @param cons
	 *            construction
	 * @param origGeoNode
	 *            original expression
	 */
	public AlgoDependentGeoCopy(Construction cons, ExpressionNode origGeoNode) {
		this(cons, (GeoElement) origGeoNode
				.evaluate(StringTemplate.defaultTemplate), origGeoNode);
	}

	/**
	 * @param cons
	 *            construction
	 * @param origGeo
	 *            original element
	 * @param origGeoNode either "geo" or "geo(x)"
	 */
	public AlgoDependentGeoCopy(Construction cons, GeoElement origGeo,
			ExpressionNode origGeoNode) {
		super(cons);
		this.origGeo = origGeo;

		// just for the toString() method
		this.origGeoNode = origGeoNode;

		copyGeo = origGeo.copy();
		setInputOutput(); // for AlgoElement

		compute();

	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = origGeo;

		setOnlyOutput(copyGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return dependent copy of original geo
	 */
	public GeoElement getGeo() {
		return copyGeo;
	}

	/**
	 * @return input geo
	 */
	public GeoElement getOrigGeo() {
		return origGeo;
	}

	// copy geo
	@Override
	public final void compute() {
		try {
			copyGeo.set(origGeo);
		} catch (Exception e) {
			copyGeo.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// make sure X=(1,2)+t(3,4) does not go to XML as "expression" argument value
		if (tpl.hasType(StringType.GEOGEBRA_XML) && !origGeo.isLabelSet()
				&& origGeo instanceof GeoLine
				&& ((GeoLine) origGeo).getEquationForm()
				== LinearEquationRepresentable.Form.PARAMETRIC) {
			((GeoLine) origGeo).setEquationForm(LinearEquationRepresentable.Form.EXPLICIT);
			String ret = origGeo.getLabel(tpl);
			((GeoLine) origGeo).setEquationForm(LinearEquationRepresentable.Form.PARAMETRIC);
			return ret;
		}
		// we use the expression as it may add $ signs
		// to the label like $A$1
		return origGeoNode.toString(tpl);
	}

	@Override
	public ExpressionNode getExpression() {
		return origGeoNode;
	}

	public void setExpression(ExpressionNode expression) {
		origGeoNode = expression;
	}

	@Override
	protected String toExpString(StringTemplate tpl) {
		if (copyGeo.isLabelSet() && (copyGeo.isGeoFunction() || copyGeo.isGeoFunctionNVar())) {
			return copyGeo.getLabel(tpl) + "(" + ((VarString) copyGeo).getVarString(tpl)
					+ ") = " + origGeoNode.toString(tpl);
		}
		return super.toExpString(tpl);
	}
}
