/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoParabolaPointLine.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public abstract class AlgoParabolaPointLineND extends AlgoElement {

	protected GeoPointND F; // input
	protected GeoLineND l; // input
	protected GeoConicND parabola; // output

	public AlgoParabolaPointLineND(Construction cons, String label,
			GeoPointND F, GeoLineND l) {
		this(cons, F, l);
		parabola.setLabel(label);
	}

	public AlgoParabolaPointLineND(Construction cons, GeoPointND F, GeoLineND l) {
		super(cons);
		this.F = F;
		this.l = l;
		parabola = newGeoConic(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	abstract protected GeoConicND newGeoConic(Construction cons);

	@Override
	public Commands getClassName() {
		return Commands.Parabola;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_PARABOLA;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) F;
		input[1] = (GeoElement) l;

		super.setOutputLength(1);
		super.setOutput(0, parabola);
		setDependencies(); // done by AlgoElement
	}

	public GeoConicND getParabola() {
		return parabola;
	}

	// Made public for LocusEqu
	public GeoPointND getFocus() {
		return F;
	}

	// Made public for LocusEqu
	public GeoLineND getLine() {
		return l;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("ParabolaWithFocusAandDirectrixB",
				F.getLabel(tpl), l.getLabel(tpl));

	}

}
