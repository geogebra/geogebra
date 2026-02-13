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

import org.geogebra.common.awt.GColor;
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
	GeometriesSetPacking(
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
	int getIndex() {
		return index;
	}

	@Override
	protected Geometry newGeometry(Manager.Type type) {
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
	void updateColorAndLayer(GColor newColor, int newLayer) {
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
	void updateVisibility(boolean visible, int alpha, int objLayer) {
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
	GColor getColor() {
		return color;
	}

	/**
	 * @return current layer
	 */
	int getLayer() {
		return layer;
	}

	/**
	 * 
	 * @return gl buffer manager
	 */
	GLBufferManager getBufferManager() {
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