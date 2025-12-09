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

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;

/**
 * Manager to create GLLists using GL elements methods
 * 
 * @author mathieu
 *
 */
public class ManagerElementForGLList extends Manager {

	private Manager managerOriginal;
	private ArrayList<Double> vx;
	private ArrayList<Double> vy;
	private ArrayList<Double> vz;
	private ArrayList<Double> nx;
	private ArrayList<Double> ny;
	private ArrayList<Double> nz;
	private ArrayList<Double> tx;
	private ArrayList<Double> ty;

	private boolean hasTexture = false;
	private GLBufferIndicesForGLList arrayI = null;

	/**
	 * @param renderer
	 *            renderer
	 * @param view3d
	 *            view
	 * @param managerOriginal
	 *            wrapped manager
	 */
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
	public int startNewList(int old, boolean mayBePacked) {
		// not used here
		return -1;
	}

	@Override
	public void endList() {
		// not used here
	}

	@Override
	public void startGeometry(Type type) {
		if (vx == null) {
			vx = new ArrayList<>();
			vy = new ArrayList<>();
			vz = new ArrayList<>();
			nx = new ArrayList<>();
			ny = new ArrayList<>();
			nz = new ArrayList<>();
			tx = new ArrayList<>();
			ty = new ArrayList<>();
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
	public int startPolygons(Drawable3D d) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void setDummyTexture() {
		managerOriginal.texture(0, 0);
	}

	@Override
	public void endPolygons(Drawable3D d) {
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
	protected void rectangleGeometry(double x, double y, double z, double width,
			double height) {
		// TODO Auto-generated method stub
	}

	@Override
	public GLBufferIndices getCurrentGeometryIndices(int size) {
		if (arrayI == null) {
			arrayI = new GLBufferIndicesForGLList(this);
		}
		return arrayI;
	}

	private static class GLBufferIndicesForGLList implements GLBufferIndices {

		private ManagerElementForGLList manager;

		public GLBufferIndicesForGLList(ManagerElementForGLList manager) {
			this.manager = manager;
		}

		@Override
		public void allocate(int length) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setLimit(int length) {
			// TODO Auto-generated method stub

		}

		@Override
		public void put(short value) {
			manager.put(value);

		}

		@Override
		public void put(int index, short value) {
			put(value);
		}

		@Override
		public short get() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void rewind() {
			// TODO Auto-generated method stub
		}

		@Override
		public int capacity() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void array(short[] ret) {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setEmpty() {
			// TODO Auto-generated method stub

		}

		@Override
		public void reallocate(int size) {
			// not needed
		}

		@Override
		public void position(int newPosition) {
			// not needed
		}

	}

	/**
	 * Put normal, texture and vertex for given index to the original manager
	 * 
	 * @param value
	 *            index
	 */
	public void put(short value) {
		// Log.debug("" + value);
		managerOriginal.normal(nx.get(value), ny.get(value), nz.get(value));
		if (hasTexture) {
			managerOriginal.texture(tx.get(value), ty.get(value));
		}
		managerOriginal.vertex(vx.get(value), vy.get(value), vz.get(value));

	}
	
	@Override
	protected ScalerXYZ getScalerXYZ() {
		return managerOriginal.getScalerXYZ();
	}
	
	@Override
	public void setScalerIdentity() {
		managerOriginal.setScalerIdentity();
	}

	@Override
	public void setScalerView() {
		managerOriginal.setScalerView();
	}

}
