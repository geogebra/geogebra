/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDirection.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDirection extends AlgoElement {

	private GeoLineND g; // input
	private GeoVectorND v; // output

	/** Creates new AlgoDirection */
	public AlgoDirection(Construction cons, String label, GeoLineND g) {
		this(cons, g);
		v.setLabel(label);
	}

	public AlgoDirection(Construction cons, GeoLineND g) {
		super(cons);
		this.g = g;
		v = new GeoVector(cons);

		GeoPointND possStartPoint = g.getStartPoint();
		if (possStartPoint != null && possStartPoint.isLabelSet()) {
			try {
				v.setStartPoint(possStartPoint);
			} catch (CircularDefinitionException e) {
			}
		}

		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		((GeoVector) v).z = 0.0d;
		compute();

	}

	@Override
	public Commands getClassName() {
		return Commands.Direction;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = g.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, v.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	public GeoVectorND getVector() {
		return v;
	}

	GeoLineND getg() {
		return g;
	}

	// direction vector of g
	@Override
	public final void compute() {
		((GeoVector) v).x = ((GeoLine) g).y;
		((GeoVector) v).y = -((GeoLine) g).x;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("DirectionOfA", g.getLabel(tpl));
	}

	// TODO Consider locusequability
}
