package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * Set of geometries, that are packed
 *
 */
class GeometriesSetPacking extends GeometriesSet {

	private GLBufferManager bufferManager;
	private static final long serialVersionUID = 1L;
	private GColor color;
	private int layer;
	private int index;
	private int oldGeometriesLength;

	/**
	 * constructor
	 * 
	 * @param manager
	 *            manager
	 * @param bufferManager
	 *            gl buffer manager
	 * 
	 * @param color
	 *            color
	 * @param layer
	 *            layer
	 */
	public GeometriesSetPacking(
			ManagerShaders manager,
			GLBufferManager bufferManager, GColor color, int layer) {
		super(manager);
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
		return new GeometryPacking(manager, this, type,
				currentGeometryIndex);
	}

	@Override
	public void bindGeometry(int size, TypeElement type) {
		bufferManager.setIndices(size, type);
	}

	/**
	 * update all geometries color for this set
	 * 
	 * @param newColor
	 *            color
	 * @param newLayer
	 *            layer
	 */
	public void updateColorAndLayer(GColor newColor, int newLayer) {
		this.color = newColor;
		this.layer = newLayer;
		bufferManager.updateColorAndLayer(index, getGeometriesLength(),
				newColor, newLayer);
	}

	/**
	 * update all geometries visibility for this set
	 * 
	 * @param visible
	 *            if visible
	 * @param alpha
	 *            object alpha
	 * @param objLayer
	 *            object layer
	 */
	public void updateVisibility(boolean visible, int alpha, int objLayer) {
		bufferManager.updateVisibility(index, 0, getGeometriesLength(),
				visible, alpha, objLayer);
	}

	@Override
	public void hideLastGeometries() {
		bufferManager.updateVisibility(index, currentGeometryIndex,
				oldGeometriesLength, false, 0, 0);
	}

	/**
	 * @return current color
	 */
	public GColor getColor() {
		return color;
	}

	/**
	 * @return current layer
	 */
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

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj); // see EQ_DOESNT_OVERRIDE_EQUALS in SpotBugs
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}