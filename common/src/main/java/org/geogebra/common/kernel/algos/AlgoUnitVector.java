/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoUnitVectorVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 *
 * @author Markus
 */
public abstract class AlgoUnitVector extends AlgoElement {

	protected GeoElement inputGeo; // input
	protected GeoVectorND u; // output
	protected boolean normalize;
	protected double length;

	/** Creates new AlgoOrthoVectorVector */
	public AlgoUnitVector(Construction cons, GeoElement inputGeo,
			boolean normalize) {
		super(cons);
		this.normalize = normalize;
		this.inputGeo = inputGeo;
		u = createVector(cons);

		GeoPointND possStartPoint = getInputStartPoint();
		if (possStartPoint != null && possStartPoint.isLabelSet()) {
			try {
				u.setStartPoint(possStartPoint);
			} catch (CircularDefinitionException e) {
				// can't happen for new vector v
			}
		}

		setInputOutput(); // for AlgoElement

		compute();
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @return new vector
	 */
	abstract protected GeoVectorND createVector(Construction cons1);

	abstract protected GeoPointND getInputStartPoint();

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputGeo;

		setOnlyOutput(u);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public Commands getClassName() {
		return normalize ? Commands.UnitVector : Commands.Direction;
	}

	public GeoVectorND getVector() {
		return u;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		if (!normalize) {
			return getLoc().getPlainDefault("DirectionOfA", "Direction of %0",
					inputGeo.getLabel(tpl));
		}
		return getLoc().getPlainDefault("UnitVectorOfA", "Unit vector of %0",
				inputGeo.getLabel(tpl));
	}

}
