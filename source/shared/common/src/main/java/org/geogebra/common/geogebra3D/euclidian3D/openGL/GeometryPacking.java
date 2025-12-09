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

/**
 * geometry handler for buffer packing
 *
 */
public class GeometryPacking extends Geometry {

	private int geometryIndex;
	private GeometriesSetPacking geometrySet;

	/**
	 * 
	 * @param manager
	 *            manager
	 * @param geometrySet
	 *            geometry set
	 * @param type
	 *            type
	 * @param geometryIndex
	 *            geometry index
	 */
	public GeometryPacking(ManagerShaders manager,
			GeometriesSetPacking geometrySet, Manager.Type type,
			int geometryIndex) {
		super(manager, type);
		this.geometrySet = geometrySet;
		this.geometryIndex = geometryIndex;
	}

	@Override
	protected void setBuffers() {
		// no internal buffer needed here
	}

	@Override
	public void setType(Manager.Type type) {
		// not needed: all geometries are triangles
	}

	@Override
	public Manager.Type getType() {
		// all geometries are triangles
		return Manager.Type.TRIANGLES;
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