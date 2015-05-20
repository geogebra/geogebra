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

		startCurve(longitude);

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
	private void startCurve(int size) {
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

		// last tube rule
		for (int i = 0; i < LATITUDES; i++) {
			draw(end, SINUS[i], COSINUS[i], 1);
		}

		// creates indices buffer
		short[] bufferI = ((ManagerShadersBindBuffers) manager)
				.getCurrentGeometryIndices(3 * 2 * size * LATITUDES);

		int index = 0;
		for (int k = 0; k < size; k++) {
			for (int i = 0; i < LATITUDES; i++) {
				int iNext = (i + 1) % LATITUDES;
				// first triangle
				bufferI[index] = (short) (i + k * LATITUDES);
				index++;
				bufferI[index] = (short) (i + (k + 1) * LATITUDES);
				index++;
				bufferI[index] = (short) (iNext + (k + 1) * LATITUDES);
				index++;
				// second triangle
				bufferI[index] = (short) (i + k * LATITUDES);
				index++;
				bufferI[index] = (short) (iNext + (k + 1) * LATITUDES);
				index++;
				bufferI[index] = (short) (iNext + k * LATITUDES);
				index++;

				// App.debug((iNext + (k + 1) * LATITUDES) + " / "
				// + ((short) (iNext + (k + 1) * LATITUDES)));
			}
		}

		// for (int i = 0; i < bufferI.length; i++) {
		// bufferI[i] = 0;
		// }

		manager.endGeometry();
		useStartCurve = false;

		App.debug("" + size);
	}

	@Override
	public void join() {

		if (useStartCurve) {
			// draw curve part
			for (int i = 0; i < LATITUDES; i++) {
				draw(start, SINUS[i], COSINUS[i], 0); // bottom of the tube rule
			}

		} else {
			super.join();
		}


	}

}
