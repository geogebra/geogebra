package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * Set of geometries
 * 
 * @author mathieu
 *
 */
@SuppressWarnings("serial")
public class GeometriesSet extends ArrayList<Geometry> {

	private Geometry currentGeometry;
	/** current geometry index */
	protected int currentGeometryIndex;
	private int geometriesLength;
	/** manager */
	protected final ManagerShaders manager;

	/**
	 * Creates geometry set.
	 * 
	 * @param manager
	 *            manager
	 */
	public GeometriesSet(ManagerShaders manager) {
		this.manager = manager;
		reset();
	}

	/**
	 * set index and color
	 * 
	 * @param index
	 *            index
	 * @param color
	 *            color
	 * @param layer
	 *            layer
	 */
	public void setIndex(int index, GColor color, int layer) {
		// no need here
	}

	/**
	 * says this geometry set is reset
	 */
	public void reset() {
		currentGeometryIndex = 0;
		geometriesLength = 0;
	}

	/**
	 * hide last geometries when ending a list
	 */
	public void hideLastGeometries() {
		// nothing to do here
	}

	/**
	 * 
	 * @return geometries length
	 */
	public int getGeometriesLength() {
		return geometriesLength;
	}

	/**
	 * start a new geometry
	 * 
	 * @param type
	 *            type of primitives
	 */
	public void startGeometry(Type type) {
		if (currentGeometryIndex < size()) {
			currentGeometry = get(currentGeometryIndex);
			currentGeometry.setType(type);
		} else {
			currentGeometry = newGeometry(type);
			add(currentGeometry);
		}

		currentGeometryIndex++;
		geometriesLength++;
	}

	/**
	 * @param type
	 *            geometry type
	 * @return new geometry for the given type
	 */
	protected Geometry newGeometry(Type type) {
		return new Geometry(this.manager, type);
	}

	/**
	 * allocate buffers of current geometry
	 * 
	 * @param size
	 *            memory size
	 */
	public void allocate(int size) {
		currentGeometry.allocateBuffers(size);
	}

	/**
	 * put vertex values into buffer
	 * 
	  * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void vertexDirect(double x, double y, double z) {
		currentGeometry.vertexDirect(x, y, z);
	}

	/**
	 * put normal values into buffer
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void normalDirect(double x, double y, double z) {
		currentGeometry.normalDirect(x, y, z);
	}

	/**
	 * ends current geometry
	 */
	public void endGeometry() {
		currentGeometry.end();
	}

	/**
	 * bind current geometry to its buffer
	 * 
	 * @param size
	 *            indices size
	 * @param type
	 *            type for element indices
	 */
	public void bindGeometry(int size, TypeElement type) {
		currentGeometry.bind(size, type);
	}

	/**
	 * set vertices for current geometry
	 * 
	 * @param vertices
	 *            vertices
	 * @param length
	 *            vertices length
	 */
	public void setVertices(ArrayList<Double> vertices, int length) {
		currentGeometry.setVertices(vertices, length);
		currentGeometry.setLength(length / 3);
	}

	/**
	 * Set normals of current geometry.
	 * 
	 * @param normals
	 *            normals
	 * @param length
	 *            length to copy
	 */
	public void setNormals(ArrayList<Double> normals, int length) {
		if (length == 3) { // only one normal for all vertices
			currentGeometry.setNormals(normals, length);
		} else if (length == 3 * currentGeometry.getLength()) {
			currentGeometry.setNormals(normals, length);
		}
	}

	/**
	 * Set colors of current geometry.
	 * 
	 * @param textures
	 *            textures
	 * @param length
	 *            length to copy
	 */
	public void setTextures(ArrayList<Double> textures, int length) {
		if (length == 2 * currentGeometry.getLength()) {
			currentGeometry.setTextures(textures, length);
		} else {
			currentGeometry.setTexturesEmpty();
		}
	}

	/**
	 * Set colors of current geometry.
	 * 
	 * @param colors
	 *            colors
	 * @param length
	 *            length to copy
	 */
	public void setColors(ArrayList<Double> colors, int length) {
		if (length == 4 * currentGeometry.getLength()) {
			currentGeometry.setColors(colors, length);
		} else {
			currentGeometry.setColorsEmpty();
		}
	}

    /**
     *
     * @return true if this set use packing
     */
	public boolean usePacking() {
	    return false;
    }

	/**
	 * remove GL buffers
	 */
	public void removeBuffers() {
		// not needed here
	}

	/**
	 * 
	 * @return current geometry
	 */
	public Geometry getCurrentGeometry() {
		return currentGeometry;
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