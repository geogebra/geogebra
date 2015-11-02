package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Class that describes the geometry of buttons for 3D view
 * 
 * @author ggb3D
 *
 */
public class PlotterMouseCursor {

	static private float coeff = 30f;

	private int index;


	/**
	 * common constructor
	 * 
	 * @param manager
	 *            geometry manager
	 */
	public PlotterMouseCursor(Manager manager) {


		Coords n = new Coords(0, 0, 1);

		// vertices
		Coords[] a = new Coords[7];
		a[0] = new Coords(0, 0, 0);
		a[1] = new Coords(0.38 * coeff, -0.39 * coeff, 0);
		a[2] = new Coords(0.21 * coeff, -0.38 * coeff, 0);
		a[3] = new Coords(0.29 * coeff, -0.56 * coeff, 0);
		a[4] = new Coords(0.21 * coeff, -0.60 * coeff, 0);
		a[5] = new Coords(0.13 * coeff, -0.42 * coeff, 0);
		a[6] = new Coords(0, -0.55 * coeff, 0);
		
		// outline vertices
		Coords[] b = new Coords[7];
		b[0] = new Coords(-0.028 * coeff, 0.068859231 * coeff, 0);
		b[1] = new Coords(0.450461328556841 * coeff,
				-0.422193185106047 * coeff, 0);
		b[2] = new Coords(0.254264077762676 * coeff,
				-0.410652170353449 * coeff, 0);
		b[3] = new Coords(0.326453438653662 * coeff,
				-0.573078232358169 * coeff, 0);
		b[4] = new Coords(0.196313798935428 * coeff,
				-0.638148052217286 * coeff, 0);
		b[5] = new Coords(0.120971069271961 * coeff,
				-0.468626910474487 * coeff, 0);
		b[6] = new Coords(-0.0280000000000002 * coeff, -0.617597979746448
				* coeff, 0);

		// create geometry with color

		// lines
		index = manager.startNewList(-1);
		manager.startGeometry(Manager.Type.TRIANGLES);
		manager.normal(n);
		vertexBlack(manager, a[0]);
		vertexBlack(manager, b[0]);
		vertexBlack(manager, b[1]);
		vertexBlack(manager, a[0]);
		vertexBlack(manager, b[1]);
		vertexBlack(manager, a[1]);

		vertexBlack(manager, a[1]);
		vertexBlack(manager, b[1]);
		vertexBlack(manager, b[2]);
		vertexBlack(manager, a[1]);
		vertexBlack(manager, b[2]);
		vertexBlack(manager, a[2]);

		vertexBlack(manager, a[2]);
		vertexBlack(manager, b[2]);
		vertexBlack(manager, b[3]);
		vertexBlack(manager, a[2]);
		vertexBlack(manager, b[3]);
		vertexBlack(manager, a[3]);

		vertexBlack(manager, a[3]);
		vertexBlack(manager, b[3]);
		vertexBlack(manager, b[4]);
		vertexBlack(manager, a[3]);
		vertexBlack(manager, b[4]);
		vertexBlack(manager, a[4]);

		vertexBlack(manager, a[4]);
		vertexBlack(manager, b[4]);
		vertexBlack(manager, b[5]);
		vertexBlack(manager, a[4]);
		vertexBlack(manager, b[5]);
		vertexBlack(manager, a[5]);

		vertexBlack(manager, a[5]);
		vertexBlack(manager, b[5]);
		vertexBlack(manager, b[6]);
		vertexBlack(manager, a[5]);
		vertexBlack(manager, b[6]);
		vertexBlack(manager, a[6]);

		vertexBlack(manager, a[6]);
		vertexBlack(manager, b[6]);
		vertexBlack(manager, b[0]);
		vertexBlack(manager, a[6]);
		vertexBlack(manager, b[0]);
		vertexBlack(manager, a[0]);


		// polygon
		vertexWhite(manager, a[0]);
		vertexWhite(manager, a[1]);
		vertexWhite(manager, a[2]);
		vertexWhite(manager, a[0]);
		vertexWhite(manager, a[2]);
		vertexWhite(manager, a[5]);
		vertexWhite(manager, a[0]);
		vertexWhite(manager, a[5]);
		vertexWhite(manager, a[6]);
		vertexWhite(manager, a[2]);
		vertexWhite(manager, a[3]);
		vertexWhite(manager, a[4]);
		vertexWhite(manager, a[2]);
		vertexWhite(manager, a[4]);
		vertexWhite(manager, a[5]);
		manager.endGeometry();
		manager.endList();



	}


	private static void vertexWhite(Manager manager, Coords v) {
		manager.color(1, 1, 1, 1);
		manager.vertex(v);
	}

	private static void vertexBlack(Manager manager, Coords v) {
		manager.color(0, 0, 0, 1);
		manager.vertex(v);
	}



	// ////////////////////////////////
	// INDEX
	// ////////////////////////////////


	/**
	 * 
	 * @return geometry index
	 */
	public int getIndex() {
		return index;
	}

}
