/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;

/**
 * Creates a dependent copy of the given GeoElement.
 */
public class AlgoDependentGeoCopy extends AlgoElement implements DependentAlgo {

	private ExpressionNode origGeoNode;
	private GeoElement origGeo, copyGeo; // input, ouput

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param origGeo
	 *            original element
	 */
	public AlgoDependentGeoCopy(Construction cons, String label,
			GeoElement origGeo) {
		this(cons, label, origGeo, origGeo.wrap());
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param origGeoNode
	 *            original expression
	 */
	public AlgoDependentGeoCopy(Construction cons, String label,
			ExpressionNode origGeoNode) {
		this(cons, label, (GeoElement) origGeoNode
				.evaluate(StringTemplate.defaultTemplate), origGeoNode);
	}

	private AlgoDependentGeoCopy(Construction cons, String label,
			GeoElement origGeo, ExpressionNode origGeoNode) {
		super(cons);
		this.origGeo = origGeo;

		// just for the toString() method
		this.origGeoNode = origGeoNode;

		copyGeo = origGeo.copy();
		setInputOutput(); // for AlgoElement

		compute();
		copyGeo.setLabel(label);
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

		setOutputLength(1);
		setOutput(0, copyGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return depedent copy of original geo
	 */
	public GeoElement getGeo() {
		return copyGeo;
	}

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
		// make sure X=(1,2)+t(3,4) does not go to XML as "expression" argument
		// value
		if (tpl.hasType(StringType.GEOGEBRA_XML) && !origGeo.isLabelSet()
				&& origGeo instanceof GeoLine
				&& ((GeoLine) origGeo).getMode() == GeoLine.PARAMETRIC) {
			((GeoLine) origGeo).setMode(GeoLine.EQUATION_EXPLICIT);
			String ret = ((GeoLine) origGeo).getLabel(tpl);
			((GeoLine) origGeo).setMode(GeoLine.PARAMETRIC);
			return ret;
		}
		// we use the expression as it may add $ signs
		// to the label like $A$1
		return origGeoNode.toString(tpl);
	}

	// TODO Consider locusequability
}
