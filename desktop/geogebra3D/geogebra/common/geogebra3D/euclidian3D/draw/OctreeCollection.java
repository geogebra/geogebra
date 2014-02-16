package geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw;

/**
 * Interface for 3D objects that are sorted as octrees.
 * 
 * @author André Eriksson
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
