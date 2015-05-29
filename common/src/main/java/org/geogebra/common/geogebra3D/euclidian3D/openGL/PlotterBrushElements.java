package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;

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

		endCurve(longitude);
	}

	private boolean useStartCurve = false;

	/**
	 * say we'll start a curve
	 * 
	 * @param size
	 *            vertices size of the curves
	 */
	private void startCurve() {
		useStartCurve = true;
		manager.startGeometry(Manager.Type.TRIANGLES);

		

	}

	/**
	 * end the curve
	 * 
	 * @param size
	 *            vertices size of the curves
	 */
	private void endCurve(int size) {

		App.debug("endCurve");

		// last tube rule
		for (int i = 0; i < LATITUDES; i++) {
			draw(end, SINUS[i], COSINUS[i], 1);
		}


		((ManagerShaders) manager).endGeometry(size);
		useStartCurve = false;

	}


	private int sectionSize;

	@Override
	public void join() {

		if (useStartCurve) {
			// draw curve part
			for (int i = 0; i < LATITUDES; i++) {
				draw(start, SINUS[i], COSINUS[i], 0); // bottom of the tube rule
			}
			sectionSize++;
		} else {
			super.join();
		}


	}

	@Override
	public void segment(Coords p1, Coords p2) {
		startCurve();
		sectionSize = 0;

		super.segment(p1, p2);

		endCurve(sectionSize);
	}



	@Override
	public void moveTo(double[] pos) {

		// close last part
		if (useStartCurve) {
			endCurve(sectionSize);
		}

		// start new part
		startCurve();
		sectionSize = 0;

		drawTo(pos, false);
	}

	@Override
	public void endPlot() {
		endCurve(sectionSize);
	}

}
