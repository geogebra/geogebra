package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Manager to create GLLists using GL elements methods
 * 
 * @author mathieu
 *
 */
public class ManagerElementForGLList extends Manager {

	private Manager managerOriginal;

	public ManagerElementForGLList(Renderer renderer, EuclidianView3D view3d,
			Manager managerOriginal) {
		super(view3d);
		setRenderer(renderer);
		this.managerOriginal = managerOriginal;
	}

	@Override
	protected void setRenderer(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Renderer getRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int startNewList(int old) {
		// not used here
		return -1;
	}

	@Override
	public void endList() {
		// not used here
	}

	private ArrayList<Double> vx, vy, vz, nx, ny, nz, tx, ty;

	private boolean hasTexture = false;

	@Override
	public void startGeometry(Type type) {
		if (vx == null) {
			vx = new ArrayList<Double>();
			vy = new ArrayList<Double>();
			vz = new ArrayList<Double>();
			nx = new ArrayList<Double>();
			ny = new ArrayList<Double>();
			nz = new ArrayList<Double>();
			tx = new ArrayList<Double>();
			ty = new ArrayList<Double>();
		} else {
			vx.clear();
			vy.clear();
			vz.clear();
			nx.clear();
			ny.clear();
			nz.clear();
			tx.clear();
			ty.clear();
		}

		hasTexture = false;
		managerOriginal.startGeometry(type);

	}

	@Override
	public void endGeometry() {
		managerOriginal.endGeometry();
	}

	@Override
	public void endGeometry(int size, TypeElement type) {
		endGeometry();
	}

	@Override
	public int startPolygons(int old) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void drawPolygon(Coords n, Coords[] v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setDummyTexture() {
		managerOriginal.texture(0, 0);
	}

	@Override
	public void endPolygons() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLabel(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void vertex(double x, double y, double z) {
		vx.add(x);
		vy.add(y);
		vz.add(z);
	}

	@Override
	protected void vertexInt(double x, double y, double z) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void vertices(double[] vertices) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void normal(double x, double y, double z) {
		nx.add(x);
		ny.add(y);
		nz.add(z);
	}

	@Override
	protected void texture(double x, double y) {
		tx.add(x);
		ty.add(y);
		hasTexture = true;
	}

	@Override
	protected void color(double r, double g, double b) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void color(double r, double g, double b, double a) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void pointSize(double size) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void rectangleGeometry(double x, double y, double z,
			double width, double height) {
		// TODO Auto-generated method stub

	}

	private GLBufferIndicesForGLList arrayI = null;

	@Override
	public GLBufferIndices getCurrentGeometryIndices(int size) {
		if (arrayI == null) {
			arrayI = new GLBufferIndicesForGLList(this);
		}
		return arrayI;
	}

	private class GLBufferIndicesForGLList implements GLBufferIndices {

		private ManagerElementForGLList manager;

		public GLBufferIndicesForGLList(ManagerElementForGLList manager) {
			this.manager = manager;
		}

		public void allocate(int length) {
			// TODO Auto-generated method stub

		}

		public void setLimit(int length) {
			// TODO Auto-generated method stub

		}

		public void put(short value) {
			manager.put(value);

		}

		public short get() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void rewind() {
			// TODO Auto-generated method stub

		}

		public void set(ArrayList<Short> array, int length) {
			// TODO Auto-generated method stub

		}

		public int capacity() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void array(short[] ret) {
			// TODO Auto-generated method stub

		}

		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		public void setEmpty() {
			// TODO Auto-generated method stub

		}

	}


	public void put(short value) {
		// App.debug("" + value);
		managerOriginal.normal(nx.get(value), ny.get(value),
				nz.get(value));
		if (hasTexture) {
			managerOriginal.texture(tx.get(value), ty.get(value));
		}
		managerOriginal.vertex(vx.get(value), vy.get(value),
				vz.get(value));

	}

}
