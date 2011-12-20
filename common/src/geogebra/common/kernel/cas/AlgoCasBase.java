/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;

/**
 * Base class for algorithms using the CAS.
 * 
 * @author Markus Hohenwarter
 */
public abstract class AlgoCasBase extends AlgoElement {

	protected CasEvaluableFunction f; // input
	protected CasEvaluableFunction g; // output

	protected AlgoCasBase(Construction cons, String label,
			CasEvaluableFunction f) {
		this(cons, f);

		setInputOutput(); // for AlgoElement
		compute();
		g.toGeoElement().setLabel(label);
	}

	protected AlgoCasBase(Construction cons, CasEvaluableFunction f) {
		super(cons);
		this.f = f;
		g = (CasEvaluableFunction) f.toGeoElement().copyInternal(cons);
	}

	@Override
	public abstract String getClassName();

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) f.toGeoElement();

		setOutputLength(1);
		setOutput(0, (GeoElement) g.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	public GeoElement getResult() {
		return (GeoElement) g.toGeoElement();
	}

	@Override
	public final void compute() {
		if (!f.toGeoElement().isDefined()) {
			((GeoElement) g.toGeoElement()).setUndefined();
			return;
		}

		applyCasCommand();
	}

	protected abstract void applyCasCommand();

	@Override
	public String toString() {
		return getCommandDescription();
	}

}
