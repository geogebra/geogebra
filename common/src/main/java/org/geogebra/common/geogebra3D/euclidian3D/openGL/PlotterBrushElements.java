package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.euclidian.plot.CurvePlotter.Gap;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Plotter brush with shaders drawElements()
 * 
 * @author mathieu
 *
 */
public class PlotterBrushElements extends PlotterBrush {

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
			double start, double extent, int longitude) {

		startCurve();

		super.arc(center, v1, v2, radius, start, extent, longitude);

		endCurve();
	}

	@Override
	public void arcEllipse(Coords center, Coords v1, Coords v2, double a,
			double b, double start, double extent) {

		startCurve();
		super.arcEllipse(center, v1, v2, a, b, start, extent);
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
	 * say we'll start a curve
	 * 
	 * @param size
	 *            vertices size of the curves
	 */
	private void startCurve() {
		manager.startGeometry(Manager.Type.TRIANGLES);
		sectionSize = 0;

	}

	/**
	 * end the curve
	 * 
	 */
	private void endCurve() {

		// last tube rule
		for (int i = 0; i < LATITUDES; i++) {
			draw(end, SINUS[i], COSINUS[i], 1);
		}


		((ManagerShaders) manager).endGeometry(sectionSize, TypeElement.CURVE);

		sectionSize = -1;

	}


	private int sectionSize = -1;

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

		// needs to specify sectionSize = -1 before moveTo() to avoid endCurve()
		sectionSize = -1;
		moveTo(pos);

	}

	@Override
	public void moveTo(double[] pos) {

		// close last part
		if (sectionSize >= 0) {
			endCurve();
		}

		// start new part
		startCurve();

		drawTo(pos, false);
	}

	@Override
	public void endPlot() {
		endCurve();
	}

	@Override
	public int end() {
		sectionSize = -1;
		return super.end();
	}
}
