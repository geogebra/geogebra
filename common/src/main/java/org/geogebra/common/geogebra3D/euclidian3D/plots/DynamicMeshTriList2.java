package org.geogebra.common.geogebra3D.euclidian3D.plots;

import org.geogebra.common.geogebra3D.euclidian3D.plots.java.nio.FloatBuffer;

/**
 * A triangle list for dynamic meshes
 * 
 * @author Andre Eriksson
 */
public interface DynamicMeshTriList2 {

	/**
	 * @param e
	 *            the element to add
	 */
	abstract public void add(DynamicMeshElement2 e);

	/**
	 * @param e
	 *            the element to add
	 * @param i
	 *            triangle index (used for surfaces)
	 */
	abstract public void add(DynamicMeshElement2 e, int i);

	/**
	 * @param e
	 *            the element to remove
	 * @return true if the element was removed, otherwise false
	 */
	abstract public boolean remove(DynamicMeshElement2 e);

	/**
	 * @param e
	 *            the element to remove
	 * @param i
	 *            triangle index (used for surfaces)
	 * @return true if the element was removed, otherwise false
	 */
	abstract public boolean remove(DynamicMeshElement2 e, int i);

	/**
	 * @param t
	 *            the element to attempt to hide
	 * @return true if the element was hidden, otherwise false
	 */
	abstract public boolean hide(DynamicMeshElement2 t);

	/**
	 * @param t
	 *            the element to attempt to show
	 * @return true if the element was shown, otherwise false
	 */
	abstract public boolean show(DynamicMeshElement2 t);

	/**
	 * Reevaluates vertices, error, etc. for all elements in the list.
	 * 
	 * @param currentVersion
	 *            current mesh version
	 */
	public void recalculate(int currentVersion);

	/**
	 * Reinserts an element into the list - used when an element is updated
	 * 
	 * @param a
	 *            element to reinsert
	 * @param version
	 *            current version of the mesh
	 */
	abstract void reinsert(DynamicMeshElement2 a, int version);

	/**
	 * @return the triangle buffer
	 */
	public abstract FloatBuffer getTriangleBuffer();

	/**
	 * @return the float buffer
	 */
	public abstract FloatBuffer getNormalBuffer();

	/**
	 * @return number of triangles in the list
	 */
	public abstract int getTriAmt();

	/**
	 * @return number of chunks in the list
	 */
	public abstract int getChunkAmt();
}
