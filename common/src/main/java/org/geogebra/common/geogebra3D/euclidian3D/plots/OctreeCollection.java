package org.geogebra.common.geogebra3D.euclidian3D.plots;

/**
 * Interface for 3D objects that are sorted as octrees.
 * 
 * @author Andre Eriksson
 */
public interface OctreeCollection {

	/**
	 * @return An octree with the basic primitive in the object (ie. segments or
	 *         triangles).
	 */
	public Octree getObjectOctree();

	/**
	 * @return An octree with the visible triangles in the octree.
	 */
	public Octree getVisibleTriangleOctree();
}
