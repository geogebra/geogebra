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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a plane through a point and orthogonal to ...
 *
 * @author Mathieu
 */
public abstract class AlgoOrthoPlanePoint extends AlgoOrthoPlane {

	private GeoPointND point; // input
	private GeoElement secondInput; // input

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param point
	 *            point
	 * @param secondInput
	 *            orthogonal element
	 */
	public AlgoOrthoPlanePoint(Construction cons, String label,
			GeoPointND point, GeoElement secondInput) {
		super(cons);
		this.point = point;
		this.secondInput = secondInput;

		setInputOutput(new GeoElement[] { (GeoElement) point, secondInput },
				new GeoElement[] { getPlane() });

		// compute plane
		compute();
		getPlane().setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalPlane;
	}

	@Override
	protected Coords getPoint() {
		return point.getInhomCoordsInD3();
	}

	/**
	 * 
	 * @return second input
	 */
	protected GeoElement getSecondInput() {
		return secondInput;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("PlaneThroughAPerpendicularToB",
				point.getLabel(tpl), secondInput.getLabel(tpl));

	}
}
