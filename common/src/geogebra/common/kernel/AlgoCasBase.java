/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;

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

	/**
	 * Creates CAS algo and sets input, output and label.
	 * Do not use if compute() or setInputOutput() are overriden.
	 * @param cons construction
	 * @param label label for output
	 * @param f input function
	 */
	protected AlgoCasBase(Construction cons, String label,
			CasEvaluableFunction f) {
		this(cons, f);

		setInputOutput(); // for AlgoElement
		compute();
		g.toGeoElement().setLabel(label);
	}

	/**
	 * Creates CAS algo, doesn't set any input, output or label
	 * @param cons construction
	 * @param f input function
	 */
	protected AlgoCasBase(Construction cons, CasEvaluableFunction f) {
		super(cons);
		this.f = f;
		g = (CasEvaluableFunction) f.toGeoElement().copyInternal(cons);
	}

	@Override
	public abstract Algos getClassName();

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
	 * Build a GeoGebraCAS command, send it to CAS and use the output to
	 * update result of this algo.
	 * @param tpl template for serialization of the command
	 */
	protected abstract void applyCasCommand(StringTemplate tpl);

	@Override
	public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}
	
	

	

}
