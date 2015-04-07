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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Base class for algorithms using the CAS.
 * 
 * @author Markus Hohenwarter
 */
public abstract class AlgoCasBase extends AlgoElement {
	/** Input function */
	protected CasEvaluableFunction f;
	/** Output function */
	protected CasEvaluableFunction g;
	private Commands cmd;

	/**
	 * Creates CAS algo and sets input, output and label. Do not use if
	 * compute() or setInputOutput() are overriden.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            input function
	 */
	protected AlgoCasBase(Construction cons, String label,
			CasEvaluableFunction f, Commands cmd) {
		this(cons, f, cmd);

		setInputOutput(); // for AlgoElement
		compute();
		g.toGeoElement().setLabel(label);
	}

	/**
	 * Creates CAS algo, doesn't set any input, output or label
	 * 
	 * @param cons
	 *            construction
	 * @param f
	 *            input function
	 */
	protected AlgoCasBase(Construction cons, CasEvaluableFunction f,
			Commands cmd) {
		super(cons);
		this.f = f;
		this.cmd = cmd;
		cons.addCASAlgo(this);
		g = (CasEvaluableFunction) f.toGeoElement().copyInternal(cons);
	}

	@Override
	public final Commands getClassName() {
		return cmd;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f.toGeoElement();

		setOutputLength(1);
		setOutput(0, g.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting function
	 */
	public GeoElement getResult() {
		return g.toGeoElement();
	}

	@Override
	public final void compute() {
		if (!f.toGeoElement().isDefined()) {
			g.toGeoElement().setUndefined();
			return;
		}

		applyCasCommand(StringTemplate.prefixedDefault);
	}

	/**
	 * Build a GeoGebraCAS command, send it to CAS and use the output to update
	 * result of this algo.
	 * 
	 * @param tpl
	 *            template for serialization of the command
	 */
	protected abstract void applyCasCommand(StringTemplate tpl);

	/**
	 * Clears the cache (needed in Web when the CAS loads)
	 * 
	 * @param label
	 *            not used
	 */
	public final void clearCasEvalMap(String label) {
		f.clearCasEvalMap(label);
	}

}
