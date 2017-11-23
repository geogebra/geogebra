package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;

/**
 * manager packing geometries
 *
 */
public class ManagerShadersElementsGlobalBufferPacking extends ManagerShadersElementsGlobalBuffer {

	/**
	 * alpha value for invisible parts
	 */
	static final public float ALPHA_INVISIBLE_VALUE = -1f;

	private GLBufferManager bufferManager;
	private boolean isPacking;
	private GColor currentColor;
	private int currentTextureType;

	private class GeometriesSetElementsGlobalBufferPacking extends GeometriesSetElementsGlobalBuffer {

		private ManagerShadersElementsGlobalBufferPacking manager;
		private static final long serialVersionUID = 1L;
		private GColor color;
		private int index;

		/**
		 * constructor
		 * 
		 * @param manager
		 *            geometry manager
		 * 
		 * @param color
		 *            color
		 */
		public GeometriesSetElementsGlobalBufferPacking(ManagerShadersElementsGlobalBufferPacking manager,
				GColor color) {
			this.manager = manager;
			this.color = color;
		}

		public void setIndex(int index, GColor color) {
			this.index = index;
			this.color = color;
		}

		/**
		 * 
		 * @return geometry set index
		 */
		public int getIndex() {
			return index;
		}

		@Override
		protected Geometry newGeometry(Type type) {
			return new GeometryElementsGlobalBufferPacking(this, type, currentGeometryIndex);
		}

		@Override
		public void bindGeometry(int size, TypeElement type) {
			manager.getBufferManager().setIndices(size);
		}

		/**
		 * update all geometries color for this set
		 * 
		 * @param color
		 *            color
		 */
		public void updateColor(GColor color) {
			this.color = color;
			manager.getBufferManager().updateColor(index, getGeometriesLength(), color);
		}

		public GColor getColor() {
			return color;
		}

		/**
		 * 
		 * @return geometry manager
		 */
		public ManagerShadersElementsGlobalBufferPacking getManager() {
			return manager;
		}

		/**
		 * geometry handler for buffer packing
		 *
		 */
		public class GeometryElementsGlobalBufferPacking extends Geometry {

			private int geometryIndex;
			private GeometriesSetElementsGlobalBufferPacking geometrySet;

			public GeometryElementsGlobalBufferPacking(GeometriesSetElementsGlobalBufferPacking geometrySet, Type type,
					int geometryIndex) {
				super(type);
				this.geometrySet = geometrySet;
				this.geometryIndex = geometryIndex;
			}

			protected void setBuffers() {
				// no internal buffer needed here
			}

			public void setType(Type type) {
				this.type = type;
			}

			public void setVertices(ArrayList<Double> array, int length) {
				// Log.debug("v length = " + length);
				geometrySet.getManager().getBufferManager().setCurrentIndex(geometrySet.getIndex(), geometryIndex);
				geometrySet.getManager().getBufferManager().setVertexBuffer(array, length);
			}

			public void setNormals(ArrayList<Double> array, int length) {
				// Log.debug("n length = " + length);
				geometrySet.getManager().getBufferManager().setNormalBuffer(array, length);
			}

			public void setTextures(ArrayList<Double> array, int length) {
				// Log.debug("t length = " + length);
				geometrySet.getManager().getBufferManager().setTextureBuffer(array, length);
			}

			public void setColors(ArrayList<Double> array, int length) {
				// Log.debug("c length = " + length);
			}

			public void setColorsEmpty() {
				geometrySet.getManager().getBufferManager().setColorBuffer(geometrySet.getColor());
			}

		}

	}

	/**
	 * constructor
	 * 
	 * @param renderer
	 *            renderer
	 * @param view3d
	 *            3D view
	 */
	public ManagerShadersElementsGlobalBufferPacking(Renderer renderer,
			EuclidianView3D view3d) {
		super(renderer, view3d);
		bufferManager = new GLBufferManager();
		isPacking = false;
	}

	@Override
	protected GeometriesSet newGeometriesSet() {
		if (isPacking) {
			return new GeometriesSetElementsGlobalBufferPacking(this, currentColor);
		}
		return super.newGeometriesSet();
	}

	/**
	 * draw curves
	 * 
	 * @param renderer
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void drawCurves(Renderer renderer, boolean hidden) {
		bufferManager.draw((RendererShadersInterface) renderer, hidden);
	}

	@Override
	public void setIsPacking(boolean isPacking) {
		this.isPacking = isPacking;
	}

	@Override
	public void setCurrentColor(GColor color) {
		this.currentColor = color;
	}

	@Override
	public void updateColor(GColor color, int index) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet != null) {
			((GeometriesSetElementsGlobalBufferPacking) geometrySet).updateColor(color);
		}
	}

	@Override
	protected void texture(double x) {
		texture(x, currentTextureType);
	}

	@Override
	public void setCurrentLineType(int lineType, int lineTypeHidden) {
		this.currentTextureType = Textures.getDashIdFromLineType(lineType, lineTypeHidden);
	}

	@Override
	public int startNewList(int old) {
		int index = super.startNewList(old);
		currentGeometriesSet.setIndex(index, currentColor);
		return index;
	}

	/**
	 * 
	 * @return buffer manager
	 */
	public GLBufferManager getBufferManager() {
		return bufferManager;
	}

	@Override
	protected void removeGeometrySet(int index) {
		GeometriesSet set = removeGeometrySetFromList(index);
		if (set != null) {
			if (set instanceof GeometriesSetElementsGlobalBufferPacking) {
				bufferManager.remove(index, set.getGeometriesLength());
			} else {
				((GeometriesSetElementsGlobalBuffer) set).removeBuffers();
			}
		}
	}

	@Override
	public boolean packBuffers() {
		return true;
	}

	@Override
	public void reset() {
		bufferManager.reset();
	}

}
