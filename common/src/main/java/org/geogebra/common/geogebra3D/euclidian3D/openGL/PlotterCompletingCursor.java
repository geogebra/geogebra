package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Class for "completing task" cursor
 * 
 * @author ggb3D
 *
 */
public class PlotterCompletingCursor {

	static private float coeff = 30f, coeff2 = 1.25f;

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

		index = -1;

		// circle
		indexCircle = manager.startNewList(-1);
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);
		manager.texture(0, 0);
		manager.color(1f, 0.5f, 0f, 1f);
		for (int i = 0; i < longitude; i++) {
			p.setX(u[i]);
			p.setY(v[i]);
			manager.vertex(p);
			p.setX(u[i] * coeff2);
			p.setY(v[i] * coeff2);
			manager.vertex(p);
		}
		for (int i = 0; i < longitude; i++) {
			p.setX(-v[i]);
			p.setY(u[i]);
			manager.vertex(p);
			p.setX(-v[i] * coeff2);
			p.setY(u[i] * coeff2);
			manager.vertex(p);
		}
		for (int i = 0; i < longitude; i++) {
			p.setX(-u[i]);
			p.setY(-v[i]);
			manager.vertex(p);
			p.setX(-u[i] * coeff2);
			p.setY(-v[i] * coeff2);
			manager.vertex(p);
		}
		for (int i = 0; i <= longitude; i++) {
			p.setX(v[i]);
			p.setY(-u[i]);
			manager.vertex(p);
			p.setX(v[i] * coeff2);
			p.setY(-u[i] * coeff2);
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
		manager.startGeometry(Manager.Type.TRIANGLE_FAN);
		manager.texture(0, 0);
		manager.color(r, g, b, a);
		manager.triangleFanApex(Coords.O);
	}

	private void endGeometry() {
		manager.endGeometry();
	}

	private float r, g, b, a;

	public void draw(double value, Coords color) {

		if (value > 0.9f) {
			// draw circle
			manager.draw(indexCircle);

		} else {
			int l = (int) (longitude * value);

			r = (float) color.getX();
			g = (float) color.getY();
			b = (float) color.getZ();
			a = (float) color.getW();

			index = manager.startNewList(index);

			// right up
			startGeometry();
			for (int i = 0; i <= l; i++) {
				p.setX(u[i]);
				p.setY(v[i]);
				manager.triangleFanVertex(p);
			}
			endGeometry();

			// left down
			startGeometry();
			for (int i = 0; i <= l; i++) {
				p.setX(-u[i]);
				p.setY(-v[i]);
				manager.triangleFanVertex(p);
			}
			endGeometry();

			// left up
			startGeometry();
			for (int i = 0; i <= l; i++) {
				p.setX(-v[i]);
				p.setY(u[i]);
				manager.triangleFanVertex(p);
			}
			endGeometry();

			// right down
			startGeometry();
			for (int i = 0; i <= l; i++) {
				p.setX(v[i]);
				p.setY(-u[i]);
				manager.triangleFanVertex(p);
			}
			endGeometry();

			manager.endList();

			// draw it
			manager.draw(index);

		}

	}

}
