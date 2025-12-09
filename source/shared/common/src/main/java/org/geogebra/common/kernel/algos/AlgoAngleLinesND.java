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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author Markus
 */
public abstract class AlgoAngleLinesND extends AlgoAngle
		implements DrawInformationAlgo {
	/** input line */
	protected GeoLineND g;
	/** input line */
	protected GeoLineND h;
	/** output angle */
	protected GeoAngle angle;

	/**
	 * Creates new unlabeled angle between lines algo
	 * 
	 * @param cons
	 *            construction
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 * @param orientation
	 *            orientation (for 3D)
	 */
	protected AlgoAngleLinesND(Construction cons, GeoLineND g, GeoLineND h,
			GeoDirectionND orientation) {
		super(cons);
		setInput(g, h, orientation);
		angle = newGeoAngle(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	/**
	 * set input
	 * 
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 * @param orientation
	 *            orientation
	 */
	protected void setInput(GeoLineND g, GeoLineND h,
			GeoDirectionND orientation) {
		this.g = g;
		this.h = h;
	}

	/**
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 */
	protected AlgoAngleLinesND(GeoLineND g, GeoLineND h) {
		super(((GeoElement) g).getConstruction(), false);
		this.g = g;
		this.h = h;
	}

	/**
	 * @param cons
	 *            construction
	 */
	protected AlgoAngleLinesND(Construction cons) {
		super(cons);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g;
		input[1] = (GeoElement) h;

		setOnlyOutput(angle);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting angle
	 * 
	 * @return resulting angle
	 */
	public GeoAngle getAngle() {
		return angle;
	}

	/**
	 * Returns the first line
	 * 
	 * @return first line
	 */
	public GeoLineND getg() {
		return g;
	}

	/**
	 * Returns the second line
	 * 
	 * @return second line
	 */
	public GeoLineND geth() {
		return h;
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("AngleBetweenAB",
				"Angle between %0, %1",
				g.getLabel(tpl),
				h.getLabel(tpl));

	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {

		if (((GeoLine) g).linDep((GeoLine) h)) {
			// angle will be 0 or 180, GeoVec3D.cross won't return a sensible
			// answer
			GeoPointND sp = h.getStartPoint();

			if (sp == null) {
				sp = g.getStartPoint();
			}

			if (sp != null) {
				m[0] = sp.getInhomX();
				m[1] = sp.getInhomY();

			} else {
				m[0] = Double.POSITIVE_INFINITY;
				m[1] = Double.POSITIVE_INFINITY;
			}

		} else {

			double[] n = GeoVec3D.cross((GeoLine) g, (GeoLine) h).get();
			m[0] = n[0] / n[2];
			m[1] = n[1] / n[2];
		}

		// first vec
		((GeoLine) g).getDirection(firstVec);
		return true;
	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {
		double[] n = GeoVec3D.cross((GeoLine) g, (GeoLine) h).get();

		Coords center;

		if (DoubleUtil.isZero(n[2])) {
			center = g.getStartInhomCoords().copyVector();
		} else {
			center = new Coords(n[0] / n[2], n[1] / n[2], 0, 1);
		}
		drawCoords[0] = center;
		drawCoords[1] = g.getDirectionInD3();
		drawCoords[2] = h.getDirectionInD3();

		return true;
	}

}
