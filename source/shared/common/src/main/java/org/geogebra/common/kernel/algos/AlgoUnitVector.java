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
