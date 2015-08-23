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

	static private float coeff = 30f, coeffCircle = 1.25f,
			coeffCompleting = 1.25f, coeffCircleOut = 0.75f;

	static public double WIDTH = 10;

	private int index, indexCircle, indexCircleOut;

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
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);
		manager.setDummyTexture();
		for (int i = 0; i < longitude; i++) {
			vertexStrip(u[i], v[i], coeffCircle);
		}
		for (int i = 0; i < longitude; i++) {
			vertexStrip(-v[i], u[i], coeffCircle);
		}
		for (int i = 0; i < longitude; i++) {
			vertexStrip(-u[i], -v[i], coeffCircle);
		}
		for (int i = 0; i <= longitude; i++) {
			vertexStrip(v[i], -u[i], coeffCircle);
		}
		manager.endGeometry();
		manager.endList();

		// circle out
		indexCircleOut = manager.startNewList(-1);
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);
		manager.setDummyTexture();
		for (int i = 0; i < longitude; i++) {
			vertexFan(u[i], v[i], coeffCircle);
		}
		for (int i = 0; i < longitude; i++) {
			vertexFan(-v[i], u[i], coeffCircle);
		}
		for (int i = 0; i < longitude; i++) {
			vertexFan(-u[i], -v[i], coeffCircle);
		}
		for (int i = 0; i <= longitude; i++) {
			vertexFan(v[i], -u[i], coeffCircle);
		}
		manager.endGeometry();
		manager.endList();
	}

	private void vertexStrip(float u1, float v1, float c) {
		p.setX(u1);
		p.setY(v1);
		manager.vertex(p);
		p.setX(u1 * c);
		p.setY(v1 * c);
		manager.vertex(p);
	}

	private void vertexFan(float u1, float v1, float c) {
		p.setX(0);
		p.setY(0);
		manager.vertex(p);
		p.setX(u1 * c);
		p.setY(v1 * c);
		manager.vertex(p);
	}

	private void vertexStripOrFan(float u1, float v1, float c, boolean out) {
		if (out) {
			vertexFan(u1, v1, c);
		} else {
			vertexStrip(u1, v1, c);
		}
	}

	// ////////////////////////////////
	// INDEX
	// ////////////////////////////////

	private Coords p = new Coords(3);

	private void startGeometry() {
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);
		manager.setDummyTexture();
		manager.color(r, g, b, a);
	}

	private void endGeometry() {
		manager.endGeometry();
	}

	private static float r = 0.25f, g = 0.25f, b = 0.25f, a = 0.75f;

	public void drawCircle(boolean out) {
		manager.color(0.5f, 0.5f, 0.5f, 0.5f);
		if (out) {
			manager.draw(indexCircleOut);
		} else {
			manager.draw(indexCircle);
		}
	}

	public void drawCompleting(double value, boolean out) {

		if (value <= 0f) {
			return;
		} else if (value >= 1f) {
			manager.color(r, g, b, a);
			if (out) {
				manager.draw(indexCircleOut);
			} else {
				manager.draw(indexCircle);
			}
		}

		int l = value > 1f ? longitude : (int) (longitude * value);

		index = manager.startNewList(index);

		// right up
		startGeometry();
		for (int i = 0; i <= l; i++) {
			vertexStripOrFan(u[i], v[i], coeffCompleting, out);
		}
		endGeometry();

		// left down
		startGeometry();
		for (int i = 0; i <= l; i++) {
			vertexStripOrFan(-u[i], -v[i], coeffCompleting, out);
		}
		endGeometry();

		// left up
		startGeometry();
		for (int i = 0; i <= l; i++) {
			vertexStripOrFan(-v[i], u[i], coeffCompleting, out);
		}
		endGeometry();

		// right down
		startGeometry();
		for (int i = 0; i <= l; i++) {
			vertexStripOrFan(v[i], -u[i], coeffCompleting, out);
		}
		endGeometry();

		manager.endList();

		// draw it
		manager.draw(index);


	}

}
