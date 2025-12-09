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

import org.geogebra.common.geogebra3D.kernel3D.commands.ParametricProcessor3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.util.debug.Log;

/**
 *
 * @author Markus
 */
public class AlgoDependentConic3D extends AlgoElement3D {

	private GeoConic3D conic; // output

	private ExpressionValue[] coeffX;
	private ExpressionValue[] coeffY;
	private ExpressionValue[] coeffZ;

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
		setInputFrom(((Equation) conic.getDefinition().unwrap()).getRHS());

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
			Log.debug(e);
			conic.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return conic.getDefinition().toString(tpl);
	}

}
