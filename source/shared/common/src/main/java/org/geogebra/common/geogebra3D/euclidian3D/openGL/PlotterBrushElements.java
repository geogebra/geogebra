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

package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrushSection.TickStep;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Plotter brush with shaders drawElements()
 * 
 * @author mathieu
 *
 */
public class PlotterBrushElements extends PlotterBrush {

	private static int SECTION_SIZE_NOT_STARTED = -1;
	private static int SECTION_SIZE_STARTED = 0;
	private int sectionSize = SECTION_SIZE_NOT_STARTED;

	/**
	 * constructor
	 * 
	 * @param manager
	 *            geometry manager
	 */
	public PlotterBrushElements(Manager manager) {
		super(manager);
	}

	@Override
	public void arc(Coords center, Coords v1, Coords v2, double radius,
			double arcStart, double extent, int longitude) {

		startCurve();

		super.arc(center, v1, v2, radius, arcStart, extent, longitude);

		endCurve();
	}

	@Override
	public void arcExtendedWithArrows(Coords center, Coords v1, Coords v2,
			double radius, double arcStart, double extent, int longitude) {
		startCurve();

		super.arcExtendedWithArrows(center, v1, v2, radius, arcStart, extent,
				longitude);

		endCurve();
	}

	@Override
	public void arcEllipse(Coords center, Coords v1, Coords v2, double a,
			double b, double arcStart, double extent) {

		startCurve();
		super.arcEllipse(center, v1, v2, a, b, arcStart, extent);
		endCurve();
	}

	@Override
	public void hyperbolaBranch(Coords center, Coords v1, Coords v2, double a,
			double b, double tMin, double tMax) {

		startCurve();
		super.hyperbolaBranch(center, v1, v2, a, b, tMin, tMax);
		endCurve();
	}

	@Override
	public void parabola(Coords center, Coords v1, Coords v2, double p,
			double tMin, double tMax, Coords p1, Coords p2) {

		startCurve();
		super.parabola(center, v1, v2, p, tMin, tMax, p1, p2);
		endCurve();
	}

	/**
	 * Start a curve.
	 */
	private void startCurve() {
		manager.startGeometry(Manager.Type.TRIANGLES);
		sectionSize = SECTION_SIZE_STARTED;

	}

	/**
	 * End the curve.
	 */
	private void endCurve() {

		if (sectionSize < SECTION_SIZE_STARTED) {
			// no curve drawn
			sectionSize = SECTION_SIZE_NOT_STARTED;
			return;
		}

		// last tube rule
		for (int i = 0; i < LATITUDES; i++) {
			draw(end, SINUS[i], COSINUS[i], 1);
		}

		manager.endGeometry(sectionSize, TypeElement.CURVE);

		sectionSize = SECTION_SIZE_NOT_STARTED;
	}

	@Override
	public void join() {
		// draw curve part
		for (int i = 0; i < LATITUDES; i++) {
			draw(start, SINUS[i], COSINUS[i], 0); // bottom of the tube rule
		}
		sectionSize++;
	}

	@Override
	public void segment(Coords p1, Coords p2) {
		startCurve();
		super.segment(p1, p2);
		endCurve();
	}

	@Override
	public void firstPoint(double[] pos, Gap moveToAllowed) {
		moveTo(pos);
	}

	@Override
	public void moveTo(double[] pos) {

		// close last part
		if (sectionSize >= SECTION_SIZE_STARTED) {
			endCurve();
		}

		// start new curve
		startCurve();

		super.moveTo(pos);
	}

	@Override
	public void moveTo(double x, double y, double z) {
		// close last part
		if (sectionSize >= SECTION_SIZE_STARTED) {
			endCurve();
		}

		// start new curve
		startCurve();

		drawTo(x, y, z, false);
	}

	@Override
	public void endPlot() {
		endCurve();
	}

	@Override
	protected void drawTick(Coords p1b, Coords p2b, float i,
			float ticksThickness, float lineThickness) {
		setTextureX(i);
		moveTo(p1b);
		setTextureX(0);
		moveTo(p1b, TickStep.START);
		setThickness(ticksThickness);
		setTextureX(0);
		moveTo(p1b, TickStep.START);
		moveTo(p1b, TickStep.MIDDLE);
		moveTo(p2b, TickStep.MIDDLE);
		moveTo(p2b, TickStep.END);
		setThickness(lineThickness);
		moveTo(p2b, TickStep.END);
		setTextureX(i);
		moveTo(p2b, TickStep.OUT);
	}

	@Override
	protected void drawArrowBase(float arrowPos, Coords arrowBase) {
		setTextureX(1 - arrowPos);
		moveTo(arrowBase);
		setTextureX(0);
		moveTo(arrowBase, TickStep.START);
	}

	@Override
	protected void drawArrowBaseOuter(Coords arrowBase) {
		moveTo(arrowBase, TickStep.START);
		moveTo(arrowBase, TickStep.OUT);
	}

}
