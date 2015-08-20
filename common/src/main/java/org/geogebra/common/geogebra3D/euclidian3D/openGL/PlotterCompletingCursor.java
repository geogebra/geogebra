package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Class for "completing task" cursor
 * 
 * @author ggb3D
 *
 */
public class PlotterCompletingCursor {

	static public float START_DRAW = 0.5f;
	static public float END_DRAW = 2f;

	static private float coeff = 30f, coeff2 = 1.25f;

	static public double WIDTH = 10;

	private int index, indexCircle;

	private Manager manager;

	static private float[] u, v;

	static int longitude = 32;

	static {
		float dt = (float) 1 / longitude;
		float da = (float) (Math.PI * dt / 2);
		u = new float[longitude + 1];
		v = new float[longitude + 1];
		for (int i = 0; i <= longitude; i++) {
			u[i] = (float) Math.cos(i * da) * coeff;
			v[i] = (float) Math.sin(i * da) * coeff;

		}
	}

	/**
	 * common constructor
	 * 
	 * @param manager
	 *            geometry manager
	 */
	public PlotterCompletingCursor(Manager manager) {

		this.manager = manager;

		// index for completing part
		index = -1;

		// circle
		indexCircle = manager.startNewList(-1);
		manager.startGeometry(Manager.Type.LINE_STRIP);
		manager.texture(0, 0);
		manager.color(0.5f, 0.5f, 0.5f, 0.5f);
		for (int i = 0; i < longitude; i++) {
			p.setX(u[i]);
			p.setY(v[i]);
			manager.vertex(p);
		}
		for (int i = 0; i < longitude; i++) {
			p.setX(-v[i]);
			p.setY(u[i]);
			manager.vertex(p);
		}
		for (int i = 0; i < longitude; i++) {
			p.setX(-u[i]);
			p.setY(-v[i]);
			manager.vertex(p);
		}
		for (int i = 0; i <= longitude; i++) {
			p.setX(v[i]);
			p.setY(-u[i]);
			manager.vertex(p);
		}
		manager.endGeometry();
		manager.endList();
	}

	// ////////////////////////////////
	// INDEX
	// ////////////////////////////////

	private Coords p = new Coords(3);

	private void startGeometry() {
		manager.startGeometry(Manager.Type.LINE_STRIP);
		manager.texture(0, 0);
		manager.color(r, g, b, a);
	}

	private void endGeometry() {
		manager.endGeometry();
	}

	private static float r = 0.25f, g = 0.25f, b = 0.25f, a = 0.75f;

	public void drawCircle() {
		manager.draw(indexCircle);
	}

	public void drawCompleting(double value) {

		int l = value > 1f ? longitude : (int) (longitude * value);

		index = manager.startNewList(index);

		// right up
		startGeometry();
		for (int i = 0; i <= l; i++) {
			p.setX(u[i]);
			p.setY(v[i]);
			manager.vertex(p);
		}
		endGeometry();

		// left down
		startGeometry();
		for (int i = 0; i <= l; i++) {
			p.setX(-u[i]);
			p.setY(-v[i]);
			manager.vertex(p);
		}
		endGeometry();

		// left up
		startGeometry();
		for (int i = 0; i <= l; i++) {
			p.setX(-v[i]);
			p.setY(u[i]);
			manager.vertex(p);
		}
		endGeometry();

		// right down
		startGeometry();
		for (int i = 0; i <= l; i++) {
			p.setX(v[i]);
			p.setY(-u[i]);
			manager.vertex(p);
		}
		endGeometry();

		manager.endList();

		// draw it
		manager.draw(index);


	}

}
