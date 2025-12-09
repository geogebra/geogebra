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
 */
public class AlgoDependentVector3D extends AlgoElement3D
		implements DependentAlgo {

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
		setInputFrom(vec.getDefinition());
		setOnlyOutput(vec);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting vector
	 */
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

	@Override
	public ExpressionNode getExpression() {
		return vec.getDefinition();
	}

}
