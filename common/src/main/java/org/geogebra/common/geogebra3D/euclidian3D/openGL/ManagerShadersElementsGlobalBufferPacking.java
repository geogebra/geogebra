package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.GeometryForExport;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * manager packing geometries
 *
 */
public class ManagerShadersElementsGlobalBufferPacking extends ManagerShadersElementsGlobalBuffer {

	/**
	 * alpha value for invisible parts
	 */
	static final public float ALPHA_INVISIBLE_VALUE = -1f;

	private GLBufferManagerCurves bufferManagerCurves;
	private GLBufferManagerSurfaces bufferManagerSurfaces, bufferManagerSurfacesClosed;
	private GLBufferManagerPoints bufferManagerPoints;
	private GLBufferManager currentBufferManager;
	private GColor currentColor;
	private int currentTextureType;
	private ReusableArrayList<Short> indices;

	private class GeometriesSetElementsGlobalBufferPacking extends GeometriesSetElementsGlobalBuffer {

		private GLBufferManager bufferManager;
		private static final long serialVersionUID = 1L;
		private GColor color;
		private int index;
		private int oldGeometriesLength;

		/**
		 * constructor
		 * 
		 * @param bufferManager
		 *            gl buffer manager
		 * 
		 * @param color
		 *            color
		 */
		public GeometriesSetElementsGlobalBufferPacking(GLBufferManager bufferManager, GColor color) {
			this.color = color;
			this.bufferManager = bufferManager;
		}

		@Override
		public void reset() {
			oldGeometriesLength = getGeometriesLength();
			super.reset();
		}

		@Override
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
			bufferManager.setIndices(size, type);
		}

		/**
		 * update all geometries color for this set
		 * 
		 * @param color
		 *            color
		 */
		public void updateColor(GColor color) {
			this.color = color;
			bufferManager.updateColor(index, getGeometriesLength(), color);
		}

		/**
		 * update all geometries visibility for this set
		 * 
		 * @param visible
		 *            if visible
		 */
		public void updateVisibility(boolean visible) {
			bufferManager.updateVisibility(index, 0, getGeometriesLength(), visible);
		}

		@Override
		public void hideLastGeometries() {
			bufferManager.updateVisibility(index, currentGeometryIndex, oldGeometriesLength, false);
		}

		public GColor getColor() {
			return color;
		}

		/**
		 * 
		 * @return gl buffer manager
		 */
		public GLBufferManager getBufferManager() {
			return bufferManager;
		}

		@Override
		public void removeBuffers() {
			bufferManager.remove(index, getGeometriesLength());
		}

		/**
		 * geometry handler for buffer packing
		 *
		 */
		public class GeometryElementsGlobalBufferPacking extends Geometry implements GeometryForExport {

			private int geometryIndex;
			private GeometriesSetElementsGlobalBufferPacking geometrySet;

			public GeometryElementsGlobalBufferPacking(GeometriesSetElementsGlobalBufferPacking geometrySet, Type type,
					int geometryIndex) {
				super(type);
				this.geometrySet = geometrySet;
				this.geometryIndex = geometryIndex;
			}

			@Override
			protected void setBuffers() {
				// no internal buffer needed here
			}

			@Override
			public void setType(Type type) {
				this.type = type;
			}

			@Override
			public void setVertices(ArrayList<Double> array, int length) {
				setBufferCurrentIndex();
				geometrySet.getBufferManager().setVertexBuffer(array, length);
			}

			@Override
			public void setNormals(ArrayList<Double> array, int length) {
				geometrySet.getBufferManager().setNormalBuffer(array, length);
			}

			@Override
			public void setTextures(ArrayList<Double> array, int length) {
				geometrySet.getBufferManager().setTextureBuffer(array);
			}

			@Override
			public void setTexturesEmpty() {
				// not implemented yet
			}

			@Override
			public void setColors(ArrayList<Double> array, int length) {
				// not implemented yet
			}

			@Override
			public void setColorsEmpty() {
				geometrySet.getBufferManager().setColorBuffer(geometrySet.getColor());
			}

			@Override
			public int getLengthForExport() {
				return geometrySet.getBufferManager().getCurrentElementsLength();
			}

			@Override
			public GLBuffer getVerticesForExport() {
				return geometrySet.getBufferManager().getCurrentBufferVertices();
			}

			@Override
			public GLBuffer getNormalsForExport() {
				return geometrySet.getBufferManager().getCurrentBufferNormals();
			}

			@Override
			public int getElementsOffset() {
				return geometrySet.getBufferManager().getCurrentElementsOffset();
			}

			@Override
			public int getIndicesLength() {
				return geometrySet.getBufferManager().getCurrentIndicesLength();
			}

			@Override
			public GLBufferIndices getBufferIndices() {
				return geometrySet.getBufferManager().getCurrentBufferIndices();
			}

			private void setBufferCurrentIndex() {
				geometrySet.getBufferManager().setCurrentIndex(geometrySet.getIndex(), geometryIndex);
			}

			@Override
			public void initForExport() {
				setBufferCurrentIndex();
				geometrySet.getBufferManager().setBufferSegmentToCurrentIndex();
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
		bufferManagerCurves = new GLBufferManagerCurves();
		bufferManagerSurfaces = new GLBufferManagerSurfaces(this);
		bufferManagerSurfacesClosed = new GLBufferManagerSurfaces(this);
		bufferManagerPoints = new GLBufferManagerPoints(this);
		currentBufferManager = null;
	}

	@Override
	protected GeometriesSet newGeometriesSet() {
		if (currentBufferManager != null) {
			return new GeometriesSetElementsGlobalBufferPacking(currentBufferManager, currentColor);
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
		bufferManagerCurves.draw((RendererShadersInterface) renderer, hidden);
	}

	/**
	 * draw surfaces
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawSurfaces(Renderer renderer) {
		bufferManagerSurfaces.draw((RendererShadersInterface) renderer);
	}

	/**
	 * draw points
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawPoints(Renderer renderer) {
		bufferManagerPoints.draw((RendererShadersInterface) renderer);
	}

	/**
	 * draw closed surfaces
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawSurfacesClosed(Renderer renderer) {
		bufferManagerSurfacesClosed.draw((RendererShadersInterface) renderer);
	}

	@Override
	public void setPackCurve(GColor color, int lineType, int lineTypeHidden) {
		currentBufferManager = bufferManagerCurves;
		this.currentColor = color;
		this.currentTextureType = Textures.getDashIdFromLineType(lineType, lineTypeHidden);
	}

	@Override
	public void updateColor(GColor color, int index) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet != null) {
			((GeometriesSetElementsGlobalBufferPacking) geometrySet).updateColor(color);
		}
	}

	@Override
	public void updateVisibility(boolean visible, int index) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet != null) {
			((GeometriesSetElementsGlobalBufferPacking) geometrySet).updateVisibility(visible);
		}
	}

	@Override
	protected void texture(double x) {
		texture(x, currentTextureType);
	}

	@Override
	public int startNewList(int old) {
		int index = super.startNewList(old);
		currentGeometriesSet.setIndex(index, currentColor);
		return index;
	}

	@Override
	public boolean packBuffers() {
		return true;
	}

	@Override
	public void reset() {
		bufferManagerCurves.reset();
		bufferManagerSurfaces.reset();
		bufferManagerSurfacesClosed.reset();
		bufferManagerPoints.reset();
	}

	@Override
	public int startPolygons(Drawable3D d) {
		if (d.shouldBePacked()) {
			setPackSurface(d);
		}
		return super.startPolygons(d);
	}

	@Override
	public void setPackSurface(Drawable3D d) {
		currentBufferManager = d.addedFromClosedSurface()
				? bufferManagerSurfacesClosed
				: bufferManagerSurfaces;
		this.currentColor = d.getSurfaceColor();
	}

	@Override
	public void endPolygons(Drawable3D d) {
		super.endPolygons(d);
		if (d.shouldBePacked()) {
			endPacking();
		}
	}

	@Override
	public void endPacking() {
		currentBufferManager = null;
	}

	@Override
	protected void setIndicesForDrawTriangleFans(int size) {
		if (indices == null) {
			indices = new ReusableArrayList<>(size);
		}
		indices.setLength(0);
	}

	@Override
	protected void putToIndicesForDrawTriangleFans(short index) {
		indices.addValue(index);
	}

	@Override
	protected void rewindIndicesForDrawTriangleFans() {
		// nothing to do
	}

	/**
	 * 
	 * @return current indices
	 */
	public ReusableArrayList<Short> getIndices() {
		return indices;
	}

	@Override
	public void endList() {
	    super.endList();
		currentGeometriesSet.hideLastGeometries();
	}

	@Override
	public int drawPoint(DrawPoint3D d, int size, Coords center, int index) {
		if (d.shouldBePacked()) {
			this.currentColor = d.getColor();
			return bufferManagerPoints.drawPoint(d, size, center, index);
		}
		return super.drawPoint(d, size, center, index);

	}

	@Override
	public GLBufferIndices getCurrentGeometryIndices(int size) {
		if (currentBufferManager != null
				&& currentBufferManager.isTemplateForPoints()) {
			return bufferManagerPoints.getBufferTemplates()
					.getBufferIndicesArray();
		}
		return super.getCurrentGeometryIndices(size);

	}

	/**
	 * set current buffer manager
	 * 
	 * @param bufferManager
	 *            buffer manager
	 */
	public void setCurrentBufferManager(GLBufferManager bufferManager) {
		currentBufferManager = bufferManager;
	}

	/**
	 * end geometry with known size, elements length, vertices and normals
	 * arrays
	 * 
	 * @param size
	 *            indices size
	 * @param elementsLength
	 *            vertices, normals length
	 * @param vertices
	 *            vertices array
	 * @param normals
	 *            normals array
	 */
	public void endGeometry(int size, int elementsLength,
			ArrayList<Double> vertices,
			ArrayList<Double> normals) {
		currentGeometriesSet.setVertices(vertices, elementsLength * 3);
		currentGeometriesSet.setNormals(normals, elementsLength * 3);
		currentGeometriesSet.setTextures(null, 0);
		currentGeometriesSet.setColors(null, 0);
		currentGeometriesSet.bindGeometry(size, TypeElement.NONE);
	}

}
