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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Creates all angles of a polygon.
 */

public abstract class AlgoAnglePolygonND extends AlgoAngle {

	protected GeoPolygon poly; // input
	protected OutputHandler<GeoElement> outputAngles;
	protected AlgoAnglePointsND algoAngle;
	/** when using angle on polygon to get interior angles */
	protected boolean internalAngle;

	/**
	 * @param cons
	 *            construction
	 * @param internalAngle
	 *            whether to return internal angles
	 */
	public AlgoAnglePolygonND(Construction cons, boolean internalAngle) {
		super(cons);
		this.internalAngle = internalAngle;
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param poly
	 *            polygon
	 * @param orientation
	 *            plane orientation
	 * @param internalAngle
	 *            whether to return internal angles
	 */
	public AlgoAnglePolygonND(Construction cons, String[] labels,
			GeoPolygon poly, GeoDirectionND orientation, boolean internalAngle) {
		this(cons, poly, orientation, internalAngle);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		outputAngles.setLabelsMulti(labels);

		update();
	}

	AlgoAnglePolygonND(Construction cons, GeoPolygon p,
			GeoDirectionND orientation, boolean internalAngle) {
		super(cons);
		this.internalAngle = internalAngle;
		setPolyAndOrientation(p, orientation);
		algoAngle = newAlgoAnglePoints(cons);
		outputAngles = createOutputAngles();
		setInputOutput(); // for AlgoElement
		compute();
	}

	/**
	 * set polygon and orientation
	 * 
	 * @param p
	 *            polygon
	 * @param orientation
	 *            orientation
	 */
	protected void setPolyAndOrientation(GeoPolygon p,
			GeoDirectionND orientation) {
		this.poly = p;
	}

	/**
	 * @param cons1
	 *            construction
	 * @return helper algo
	 */
	abstract protected AlgoAnglePointsND newAlgoAnglePoints(Construction cons1);

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = poly;

		setDependencies();
	}

	/**
	 * @return output angles
	 */
	public GeoElement[] getAngles() {
		return getOutput();
	}

	/**
	 * @return input polygon
	 */
	public GeoPolygon getPolygon() {
		return poly;
	}

	@Override
	public final void compute() {
		int length = poly.getPoints() == null ? 0 : poly.getPoints().length;
		int dir = !internalAngle || poly.getAreaWithSign() > 0
								? 1 : length - 1;
		outputAngles.adjustOutputSize(length > 0 ? length : 1);

		for (int i = 0; i < length; i++) {
			algoAngle.setABC(poly.getPointND((i + dir) % length),
					poly.getPointND(i),
					poly.getPointND((i + length - dir) % length));
			algoAngle.compute();

			GeoAngle angle = (GeoAngle) outputAngles.getElement(i);
			angle.set(algoAngle.getAngle());
			if (!angle.isDrawable) {
				angle.setDrawableNoSlider();
			}
			angle.setDrawAlgorithm(algoAngle.copy());
			cons.removeFromConstructionList(algoAngle);
		}
		// other points are undefined
		for (int i = length; i < outputAngles.size(); i++) {
			outputAngles.getElement(i).setUndefined();
		}
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		if (internalAngle) {
			return getLoc().getPlainDefault("AnglesOfA", "Angles of %0", poly.getLabel(tpl));
		}
		return getLoc().getPlainDefault("AngleOfA", "Angle of %0",
				poly.getLabel(tpl));
	}

	/**
	 * 
	 * @return output angles handler
	 */
	protected OutputHandler<GeoElement> createOutputAngles() {
		return new OutputHandler<>(() -> {
			GeoAngle p = newGeoAngle(cons);
			if (internalAngle) {
				p.setAngleStyle(AngleStyle.ANTICLOCKWISE);
			}
			p.setValue(0);
			p.setParentAlgorithm(this);
			return p;
		});
	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {
		// nothing to do here
		return true;
	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {
		// nothing to do here
		return true;
	}

	@Override
	final public Commands getClassName() {
		if (internalAngle) {
			return Commands.InteriorAngles;
		}
		return super.getClassName();
	}

}
