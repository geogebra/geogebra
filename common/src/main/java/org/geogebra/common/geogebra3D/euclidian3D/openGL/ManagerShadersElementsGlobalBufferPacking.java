package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * manager packing geometries
 *
 */
public class ManagerShadersElementsGlobalBufferPacking extends ManagerShaders {

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
			return new GeometriesSetElementsGlobalBufferPacking(this,
					currentBufferManager, currentColor, currentLayer);
		}
		return super.newGeometriesSet(mayBePacked);
	}

	/**
	 * draw curves
	 * 
	 * @param renderer1
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void drawCurves(Renderer renderer1, boolean hidden) {
		bufferManagerCurves.draw(renderer1, hidden);
	}

	/**
	 * draw clipped curves
	 * 
	 * @param renderer1
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void drawCurvesClipped(Renderer renderer1, boolean hidden) {
		renderer1.enableClipPlanesIfNeeded();
		bufferManagerCurvesClipped.draw(renderer1, hidden);
		renderer1.disableClipPlanesIfNeeded();
	}

	/**
	 * draw surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfaces(Renderer renderer1) {
		bufferManagerSurfaces.draw(renderer1);
	}

	/**
	 * draw points
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawPoints(Renderer renderer1) {
		bufferManagerPoints.draw(renderer1);
	}

	/**
	 * draw closed surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfacesClosed(Renderer renderer1) {
		bufferManagerSurfacesClosed.draw(renderer1);
	}

	/**
	 * draw closed surfaces
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawSurfacesClipped(Renderer renderer1) {
		renderer1.enableClipPlanesIfNeeded();
		bufferManagerSurfacesClipped.draw(renderer1);
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
        setPackSurface(d, false);
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
        endPacking();
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

    @Override
    public void drawPoint(Drawable3D d, float size, Coords center) {
        // get/create point geometry with template buffer
        bufferTemplates.selectSphere((int) size);
        // draw point in current curve
        this.currentColor = d.getColor();
        this.currentLayer = Renderer.LAYER_DEFAULT;
        setPointValues(size, 2.5f, center);
        bufferManagerCurves.drawPoint();
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
