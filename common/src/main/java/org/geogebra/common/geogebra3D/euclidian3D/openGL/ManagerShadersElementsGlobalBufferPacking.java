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
	/** color value for invisible parts */
	public static final GColor COLOR_INVISIBLE = GColor.newColor(0, 0, 0, 0);

	private GLBufferManagerCurves bufferManagerCurves;
	private GLBufferManagerCurvesClipped bufferManagerCurvesClipped;
	private GLBufferManagerSurfaces bufferManagerSurfaces;
	private GLBufferManagerSurfaces bufferManagerSurfacesClosed;
	private GLBufferManagerSurfacesClipped bufferManagerSurfacesClipped;
	private GLBufferManagerPoints bufferManagerPoints;
	private GLBufferManagerTemplatesForPoints bufferTemplates;
	private GLBufferManager currentBufferManager;
	private GColor currentColor;
	private int currentLayer;
	private int currentTextureType;
	private GLBufferIndicesArray indices;
	private float[] translate;
	private float scale;

	private class GeometriesSetElementsGlobalBufferPacking
			extends GeometriesSetElementsGlobalBuffer {

		private GLBufferManager bufferManager;
		private static final long serialVersionUID = 1L;
		private GColor color;
		private int layer;
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
		 * @param layer
		 *            layer
		 */
		public GeometriesSetElementsGlobalBufferPacking(
				GLBufferManager bufferManager, GColor color, int layer) {
			this.color = color;
			this.layer = layer;
			this.bufferManager = bufferManager;
		}

		@Override
		public void reset() {
			oldGeometriesLength = getGeometriesLength();
			super.reset();
		}

		@Override
		public void setIndex(int index, GColor color, int layer) {
			this.index = index;
			this.color = color;
			this.layer = layer;
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
		 * @param layer
		 *            layer
		 */
		public void updateColorAndLayer(GColor color, int layer) {
			this.color = color;
			this.layer = layer;
			bufferManager.updateColorAndLayer(index, getGeometriesLength(),
					color, layer);
		}

		/**
		 * update all geometries visibility for this set
		 * 
		 * @param visible
		 *            if visible
		 * @param alpha
		 *            object alpha
		 * @param layer
		 *            object layer
		 */
		public void updateVisibility(boolean visible, int alpha, int layer) {
			bufferManager.updateVisibility(index, 0, getGeometriesLength(),
					visible, alpha, layer);
		}

		@Override
		public void hideLastGeometries() {
			bufferManager.updateVisibility(index, currentGeometryIndex,
					oldGeometriesLength, false, 0, 0);
		}

		public GColor getColor() {
			return color;
		}

		public int getLayer() {
			return layer;
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

        @Override
        public boolean usePacking() {
            return true;
        }

		/**
		 * geometry handler for buffer packing
		 *
		 */
		public class GeometryElementsGlobalBufferPacking extends Geometry
				implements GeometryForExport {

			private int geometryIndex;
			private GeometriesSetElementsGlobalBufferPacking geometrySet;

			public GeometryElementsGlobalBufferPacking(
					GeometriesSetElementsGlobalBufferPacking geometrySet, Type type,
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
				// not needed: all geometries are triangles
			}

			@Override
			public Type getType() {
				// all geometries are triangles
				return Type.TRIANGLES;
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
				geometrySet.getBufferManager().setLayer(geometrySet.getLayer());
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
				geometrySet.getBufferManager().setCurrentIndex(geometrySet.getIndex(),
						geometryIndex);
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
		bufferTemplates = new GLBufferManagerTemplatesForPoints();
		bufferManagerCurves = new GLBufferManagerCurves(this);
		bufferManagerCurvesClipped = new GLBufferManagerCurvesClipped();
		bufferManagerSurfaces = new GLBufferManagerSurfaces(this);
		bufferManagerSurfacesClosed = new GLBufferManagerSurfaces(this);
		bufferManagerSurfacesClipped = new GLBufferManagerSurfacesClipped(this);
		bufferManagerPoints = new GLBufferManagerPoints(this);
		currentBufferManager = null;
		translate = new float[3];
	}

	@Override
	protected GeometriesSet newGeometriesSet(boolean mayBePacked) {
		if (mayBePacked && currentBufferManager != null) {
			return new GeometriesSetElementsGlobalBufferPacking(
					currentBufferManager, currentColor, currentLayer);
		}
		return super.newGeometriesSet(mayBePacked);
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
	 * draw clipped curves
	 * 
	 * @param renderer
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void drawCurvesClipped(Renderer renderer, boolean hidden) {
		renderer.enableClipPlanesIfNeeded();
		bufferManagerCurvesClipped.draw((RendererShadersInterface) renderer,
				hidden);
		renderer.disableClipPlanesIfNeeded();
	}

	/**
	 * draw surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfaces(Renderer renderer1) {
		bufferManagerSurfaces.draw((RendererShadersInterface) renderer1);
	}

	/**
	 * draw points
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawPoints(Renderer renderer1) {
		bufferManagerPoints.draw((RendererShadersInterface) renderer1);
	}

	/**
	 * draw closed surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfacesClosed(Renderer renderer1) {
		bufferManagerSurfacesClosed.draw((RendererShadersInterface) renderer1);
	}

	/**
	 * draw closed surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfacesClipped(Renderer renderer1) {
		renderer1.enableClipPlanesIfNeeded();
		bufferManagerSurfacesClipped.draw((RendererShadersInterface) renderer1);
		renderer1.disableClipPlanesIfNeeded();
	}

	@Override
	public void setPackCurve(Drawable3D d, boolean clipped) {
		currentBufferManager = clipped ? bufferManagerCurvesClipped
				: bufferManagerCurves;
		this.currentColor = d.getColor();
		this.currentLayer = d.getLayer();
		this.currentTextureType = Textures
				.getDashIdFromLineType(d.getLineType(), d.getLineTypeHidden());
	}

	@Override
	public void updateColorAndLayer(GColor color, int layer, int index) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet instanceof GeometriesSetElementsGlobalBufferPacking) {
			((GeometriesSetElementsGlobalBufferPacking) geometrySet)
					.updateColorAndLayer(color, layer);
		}
	}

	@Override
	public void updateVisibility(boolean visible, int index, int alpha, int layer) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet instanceof GeometriesSetElementsGlobalBufferPacking) {
			((GeometriesSetElementsGlobalBufferPacking) geometrySet)
					.updateVisibility(visible, alpha, layer);
		}
	}

	@Override
	protected void texture(double x) {
		texture(x, currentTextureType);
	}

	@Override
	public int startNewList(int old, boolean mayBePacked) {
		int index = super.startNewList(old, mayBePacked);
		currentGeometriesSet.setIndex(index, currentColor, currentLayer);
		return index;
	}

	@Override
	public boolean packBuffers() {
		return true;
	}

	@Override
	public void update(boolean reset) {
		if (reset) {
			bufferManagerCurves.reset();
			bufferManagerCurvesClipped.reset();
			bufferManagerSurfaces.reset();
			bufferManagerSurfacesClosed.reset();
			bufferManagerSurfacesClipped.reset();
			bufferManagerPoints.reset();
		} else {
			bufferManagerCurvesClipped.update();
			bufferManagerSurfacesClipped.update();
		}
	}

	@Override
	public int startPolygons(Drawable3D d) {
		if (d.shouldBePacked()) {
			setPackSurface(d, false);
		}
		return super.startPolygons(d);
	}

	@Override
	public void setPackSurface(Drawable3D d, boolean clipped) {
		currentBufferManager = clipped ? bufferManagerSurfacesClipped
				: (d.addedFromClosedSurface()
						? bufferManagerSurfacesClosed
						: bufferManagerSurfaces);
		this.currentColor = d.getSurfaceColor();
		this.currentLayer = d.getLayer();
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
		if (currentBufferManager == null) {
			super.setIndicesForDrawTriangleFans(size);
		} else {
			initIndices(size);
		}
	}

	private void initIndices(int size) {
		if (indices == null) {
			indices = new GLBufferIndicesArray(size);
		}
		indices.setLength(0);
	}

	@Override
	protected void putToIndicesForDrawTriangleFans(short index) {
		if (currentBufferManager == null) {
			super.putToIndicesForDrawTriangleFans(index);
		} else {
			indices.addValue(index);
		}
	}

	@Override
	protected void rewindIndicesForDrawTriangleFans() {
		if (currentBufferManager == null) {
			super.rewindIndicesForDrawTriangleFans();
		}
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
	public int drawPoint(DrawPoint3D d, float size, Coords center, int index) {
		if (d.shouldBePacked()) {
			// get/create point geometry with template buffer
			setCurrentBufferManager(bufferTemplates);
			bufferTemplates.selectSphereAndCreateIfNeeded(this, size);
			// draw in points manager
			setCurrentBufferManager(bufferManagerPoints);
			this.currentColor = d.getColor();
			this.currentLayer = Renderer.LAYER_DEFAULT;
			setPointValues(size, DrawPoint3D.DRAW_POINT_FACTOR, center);
			int ret = bufferManagerPoints.drawPoint(index);
			setCurrentBufferManager(null);
			return ret;
		}
		return super.drawPoint(d, size, center, index);
	}

	@Override
	public void drawPoint(Drawable3D d, float size, Coords center) {
		if (d.shouldBePacked()) {
			// get/create point geometry with template buffer
			bufferTemplates.selectSphere((int) size);
			// draw point in current curve
			this.currentColor = d.getColor();
			this.currentLayer = Renderer.LAYER_DEFAULT;
			setPointValues(size, 2.5f, center);
			bufferManagerCurves.drawPoint();
		} else {
			super.drawPoint(d, size, center);
		}
	}

	@Override
	public void createPointTemplateIfNeeded(int size) {
		setCurrentBufferManager(bufferTemplates);
		bufferTemplates.createSphereIfNeeded(this, size);
		setCurrentBufferManager(null);
	}

	private void setPointValues(float size, float sizeScale, Coords center) {
		scale = size * sizeScale;
		scaleXYZ(center);
		center.get(translate);
	}

	/**
	 * 
	 * @return translate (for point drawing)
	 */
	public float[] getTranslate() {
		return translate;
	}

	/**
	 * 
	 * @return scale (for point drawing)
	 */
	public float getScale() {
		return scale;
	}

	@Override
	public GLBufferIndices getCurrentGeometryIndices(int size) {
		if (currentBufferManager != null) {
			if (currentBufferManager.isTemplateForPoints()) {
				return bufferTemplates.getBufferIndicesArray();
			}
			if (currentBufferManager == bufferManagerSurfacesClosed
					|| currentBufferManager == bufferManagerSurfacesClipped) {
				initIndices(size);
				return indices;
			}
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
		currentGeometriesSet.bindGeometry(size, TypeElement.TEMPLATE);
	}

	/**
	 * 
	 * @return buffer templates (for points)
	 */
	public GLBufferManagerTemplatesForPoints getBufferTemplates() {
		return bufferTemplates;
	}

}
