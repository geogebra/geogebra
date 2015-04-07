package org.geogebra.common.geogebra3D.euclidian3D.plots;

import java.util.LinkedList;

/**
 * A 3D octree for collision, picking etc.
 * 
 * @author André Eriksson
 */
public abstract class Octree {
	/** The maximum level in the octree */
	static final short MAX_LEVEL = 10;

	/** The maximum triangle count for a direct tri-tri intersection test */
	static final short MAX_TRI_TRI_COUNT = 2;

	/** The maximum triangle count for a direct ray-tri intersection test */
	static final short MAX_RAY_TRI_COUNT = 2;

	static final float[] DEFAULT_DIMENSIONS = { -1e5f, 1e5f, -1e5f, 1e5f,
			-1e5f, 1e5f };

	protected final OctreeNode root;

	public Octree(OctreeNode root) {
		this.root = root;
	}

	/**
	 * Attempt to insert a triangle into the octree
	 * 
	 * @param tri
	 *            the triangle to insert
	 * @throws Exception
	 */
	public abstract void insertTriangle(float[] tri) throws Exception;

	/**
	 * Attempt to insert a segment into the octree
	 * 
	 * @param seg
	 *            the segment to insert
	 * @throws Exception
	 */
	public abstract void insertSegment(float[] seg) throws Exception;

	/**
	 * Detect the intersection of a triangle with the elements in the tree
	 * 
	 * @param tri
	 *            the triangle as a nine float array
	 * @return a list of intersection segments
	 */
	public LinkedList<float[]> triIntersect(float[] tri) {
		float[] bb = Collision.triangleBoundingBox(tri);

		return root.triIntersect(bb, tri, 1);
	}

	/**
	 * Detect the first intersection of a ray with a triangle in the tree
	 * 
	 * @param r1
	 *            first point on the ray
	 * @param r2
	 *            second point on the ray
	 * @return the coordinates of the first triangle if there is an intersection
	 *         - otherwise null
	 * @throws Exception
	 */
	public abstract float[] rayFirstIntersect(float[] r1, float[] r2)
			throws Exception;

	/**
	 * Detect all intersections of a segment with objects in the tree
	 * 
	 * @param p0
	 *            first endpoint of segment
	 * @param p1
	 *            second endpoint of segment
	 * @return the coordinates of all intersections - otherwise null
	 * @throws Exception
	 */
	public LinkedList<float[]> segmentIntersect(float[] p0, float[] p1)
			throws Exception {

		float[] bb = Collision.segmentBoundingBox(p0, p1);

		return root.segmentIntersect(p0, p1, bb, 1);
	}
}

/**
 * A class representing
 * 
 * @author andre
 *
 */
abstract class OctreeNode {

	/** bounds of the node: {x_min, x_max, y_min, y_max, z_min, z_max} */
	protected final float[] bnds;

	/**
	 * coordinates of the three axis-aligned planes going through the center of
	 * the node - {x, y, z}
	 */
	protected final float[] cntr;

	/**
	 * pointers to the eight children of the node - in order: (xmin, ymin, zmin)
	 * (xmax, ymin, zmin) (xmin, ymax, zmin) (xmax, ymax, zmin) (xmin, ymin,
	 * zmax) (xmax, ymin, zmax) (xmin, ymax, zmax) (xmax, ymax, zmax)
	 */
	protected OctreeNode[] children;

	/** list of all the objects of the node */
	protected LinkedList<float[]> objects;

	public OctreeNode(float[] bounds) {
		bnds = bounds;
		cntr = new float[] { 0.5f * (bounds[0] + bounds[1]),
				0.5f * (bounds[2] + bounds[3]), 0.5f * (bounds[4] + bounds[5]) };
	}

	/**
	 * Recursively finds the first intersection of a ray with any object in the
	 * octree
	 * 
	 * @param entryPt
	 *            the point of entry of the ray into the octreeElement
	 * @param exitPt
	 *            the point where the ray exits the octreeElement
	 * @param tangent
	 *            the tangent of the ray
	 * @param level
	 *            the current level of recursion
	 * @return null if there is no intersection in this subtree, otherwise the
	 *         closest triangle
	 */
	abstract float[] firstRayIntersect(float[] entryPt, float[] exitPt,
			float[] tangent, int level);

	/**
	 * Finds the set of segments that constitute the intersection of a triangle
	 * with the mesh in the octree
	 * 
	 * @param boundingBox
	 *            a bounding box for the triangle
	 * @param tri
	 *            the actual vertices of the triangle
	 * @param level
	 *            the current recursion level in the octree
	 * @return the set of segments that constitute the intersection within the
	 *         bounds of this element
	 */
	abstract LinkedList<float[]> triIntersect(float[] boundingBox, float[] tri,
			int level);

	/**
	 * Finds the set of points that constitute the intersection of a segment
	 * with the mesh in the octree
	 * 
	 * @param boundingBox
	 *            a bounding box for the segment
	 * @param p0
	 *            the first end point of the segment
	 * @param p1
	 *            the second end point of the segment
	 * @param level
	 *            the current recursion level in the octree
	 * @return the set of segments that constitute the intersection within the
	 *         bounds of this element
	 */
	abstract LinkedList<float[]> segmentIntersect(float[] boundingBox,
			float[] p0, float[] p1, int level);

	/**
	 * Recursively inserts an object into the element
	 * 
	 * @param boundingBox
	 *            axis-aligned bounding box for the object
	 * @param object
	 *            the object
	 * @param i
	 *            the current depth in the octree
	 */
	abstract void insert(float[] boundingBox, float[] object, int i);
}
