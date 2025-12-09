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

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author Markus
 */
public class AlgoAngleConic extends AlgoAngle {

	private GeoConic c; // input
	private GeoAngle angle; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 */
	public AlgoAngleConic(Construction cons, String label, GeoConic c) {
		super(cons);
		this.c = c;
		angle = new GeoAngle(cons);
		setInputOutput(); // for AlgoElement
		compute();
		angle.setDrawableNoSlider();
		angle.setLabel(label);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		setOnlyOutput(angle);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting angle
	 */
	public GeoAngle getAngle() {
		return angle;
	}

	// compute conic's angle
	@Override
	public final void compute() {
		// take a look at first eigenvector
		angle.setValue(Math.atan2(c.eigenvec[0].getY(), c.eigenvec[0].getX()));
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("AngleOfA", "Angle of %0",
				c.getLabel(tpl));
	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {

		m[0] = c.getB().getX();
		m[1] = c.getB().getY();
		firstVec[0] = 1;
		firstVec[1] = 0;

		return true;
	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {
		drawCoords[0] = c.getMidpoint3D();
		drawCoords[1] = Coords.VX;
		drawCoords[2] = c.getEigenvec3D(0);

		return true;
	}

}
