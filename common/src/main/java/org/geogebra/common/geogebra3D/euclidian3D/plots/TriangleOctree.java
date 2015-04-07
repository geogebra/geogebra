package org.geogebra.common.geogebra3D.euclidian3D.plots;

import java.util.LinkedList;

/**
 * An octree that is optimized to only contain triangles
 * 
 * @author André Eriksson
 */
public class TriangleOctree extends Octree {

	/**
	 * Constructor that uses default octree dimensions
	 * (Octree.DEFAULT_DIMENSIONS)
	 */
	public TriangleOctree() {
		super(new TriangleOctreeNode(Octree.DEFAULT_DIMENSIONS));
	}

	/**
	 * Constructor that uses custom octree dimensions
	 * 
	 * @param dim
	 *            the dimensions of the octree as
	 *            {x_min,x_max,y_min,y_max,z_min,z_max}
	 */
	public TriangleOctree(float[] dim) {
		super(new TriangleOctreeNode(dim));
	}

	/**
	 * Inserts a triangle into the octree
	 * 
	 * @param tri
	 *            the triangle as a set of nine floats
	 */
	@Override
	public void insertTriangle(float[] tri) throws Exception {
		float[] bb = Collision.triangleBoundingBox(tri);

		root.insert(bb, tri, 1);
	}

	/**
	 * Attempts to insert a segment into the octree - always throws an
	 * exception.
	 */
	@Override
	public void insertSegment(float[] seg) throws Exception {
		throw new Exception("Segments not handled.");
	}

	@Override
	public float[] rayFirstIntersect(float[] r1, float[] r2) throws Exception {
		float[] tangent = new float[] { r2[0] - r1[0], r2[1] - r1[1],
				r2[2] - r1[2] };

		float[] entry = new float[6];
		float[] exit = new float[6];

		boolean intersect = Collision.rayBoxIntersect(root.bnds, r1, tangent,
				entry, exit);

		if (!intersect)
			throw new Exception("Ray does not intersect bounding box");

		return root.firstRayIntersect(entry, exit, tangent, 1);
	}

}

/**
 * A node in a triangle octree - contains a set of triangles and methods for
 * computing triangle intersections
 * 
 * @author André Eriksson
 * 
 */
class TriangleOctreeNode extends OctreeNode {

	public TriangleOctreeNode(float[] bounds) {
		super(bounds);
	}

	/**
	 * Recursively finds the first intersection of a ray with any triangle in
	 * the octree
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
	float[] firstRayIntersect(float[] entryPt, float[] exitPt, float[] tangent,
			int level) {

		// if the maximum depth has been reached or there are few triangles in
		// this volume,
		// perform triangle-ray intersection tests to find the closest triangle
		if (level == Octree.MAX_LEVEL
				|| objects.size() <= Octree.MAX_RAY_TRI_COUNT) {
			Float param = new Float(0f);
			float min = Float.MAX_VALUE;
			float[] minPt = null;

			int size = objects.size();
			for (int i = 0; i < size; i++) {
				float[] intersect = new float[3];
				if (Collision.rayTriIntersect(entryPt, exitPt, objects.get(i),
						param, intersect) == 1) {
					if (param < min) {
						min = param;
						minPt = intersect;
					}
				}
			}
			return minPt;
		}
		int entryIndex = getCall(entryPt);

		// parameters of plane intersections
		float[] pp = new float[] { Float.MAX_VALUE, Float.MAX_VALUE,
				Float.MAX_VALUE };
		// intersection order
		int[] io = { 2, 2 };
		// number of intersections
		int ni = 0;

		int i = 0;

		for (i = 0; i < 3; i++) {
			if ((entryPt[i] - cntr[i] < 0) != (exitPt[i] - cntr[i] < 0)) {
				// x/y/z center plane is crossed - grab the param value of
				// the intersection point
				pp[i] = (cntr[i] - entryPt[i]) / tangent[i];

				// sort intersection points
				if (pp[i] < pp[io[0]]) {
					io[1] = io[0];
					io[0] = i;
				} else if (pp[i] < pp[io[1]])
					io[1] = i;

				ni++;
			}
		}

		// recursive calls
		float[] nextIntersect = null;
		float[] prevIntersect = entryPt;
		int currIndex = entryIndex;
		int n;
		float[] temp;
		for (i = 0; i <= ni; i++) {

			n = io[i];

			if (nextIntersect != null)
				prevIntersect = nextIntersect;

			if (i == ni) {
				temp = children[currIndex].firstRayIntersect(prevIntersect,
						exitPt, tangent, level + 1);
			} else {
				float k = pp[n];
				nextIntersect = new float[] { entryPt[0] + tangent[0] * k,
						entryPt[1] + tangent[1] * k,
						entryPt[2] + tangent[2] * k };
				temp = children[currIndex].firstRayIntersect(prevIntersect,
						nextIntersect, tangent, level + 1);
			}

			if (temp != null)
				return temp;

			currIndex = reflectIndex(currIndex, n);
		}
		return null;
	}

	/**
	 * returns the index of the child obtained by reflecting the given child
	 * along the midplane crossing the given axis
	 * 
	 * @param index
	 *            index of the child (0,2,...,7)
	 * @param axis
	 *            index of the axis (0 is x, 1 is y, anything else is z)
	 * @return the index of a child
	 */
	private int reflectIndex(int index, int axis) {
		switch (axis) {
		case 0:
			return index % 2 == 0 ? index + 1 : index - 1;
		case 1:
			return index % 4 < 2 ? index + 2 : index - 2;
		default:
			return index < 4 ? index + 4 : index - 4;
		}
	}

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
	LinkedList<float[]> triIntersect(float[] boundingBox, float[] tri, int level) {

		LinkedList<float[]> ret = null;

		// we're at max level or have reached a threshold - recurse no further
		if (level == Octree.MAX_LEVEL
				|| objects.size() <= Octree.MAX_TRI_TRI_COUNT) {
			for (float[] tri2 : objects) {
				Boolean intersect = false, coplanar = false;
				float[] temp = Collision.triTriIntersect(tri, tri2, coplanar,
						intersect);
				if (temp != null) {
					if (ret == null)
						ret = new LinkedList<float[]>();
					ret.add(temp);
				}
			}
			return ret;
		} else {
			// find out which children intersect the bounding box
			short call = getCalls(boundingBox);

			for (int i = 0; i <= 0x80; i++)
				if ((call & 1 << i) != 0) {
					if (children[i] == null)
						continue;
					LinkedList<float[]> temp = children[i].triIntersect(
							boundingBox, tri, level + 1);
					if (temp != null) {
						if (ret == null)
							ret = new LinkedList<float[]>();
						ret.addAll(temp);
					}
				}
		}
		return ret;
	}

	/**
	 * Tests which children intersect a given bounding box
	 * 
	 * @param bb
	 *            an axis-aligned bounding box
	 * @return a short with the first eight bits b_0, ..., b_8 set to 1 if the
	 *         corresponding child intersects the bounding box and 0 otherwise
	 */
	private short getCalls(float[] bb) {
		short r = 0xFF;
		if (bb[0] > cntr[0])
			r &= 0x55;
		if (bb[1] < cntr[0])
			r &= 0xAA;
		if (bb[2] > cntr[1])
			r &= 0x33;
		if (bb[3] < cntr[1])
			r &= 0xCC;
		if (bb[4] < cntr[2])
			r &= 0x0F;
		if (bb[5] > cntr[2])
			r &= 0xF0;
		return r;
	}

	/**
	 * Given a point in the octreeElement, computes which child it is in
	 * 
	 * @param pt
	 *            a point as an x/y/z triple
	 * @return the index of the
	 */
	private short getCall(float[] pt) {
		short r = 0xFF;
		r &= (pt[0] > cntr[0] ? 0x55 : 0xAA);
		r &= (pt[1] > cntr[1] ? 0x33 : 0xCC);
		r &= (pt[2] > cntr[2] ? 0x0F : 0xF0);
		switch (r) {
		case 0:
			return 0;
		case 1:
			return 1;
		case 2:
			return 2;
		case 4:
			return 3;
		case 8:
			return 4;
		case 16:
			return 5;
		case 32:
			return 6;
		case 64:
			return 7;
		default:
			return -1;
		}
	}

	public void insert(float[] boundingBox, float[] tri, int level) {
		objects.add(tri);

		if (level >= Octree.MAX_LEVEL)
			return;

		short call = getCalls(boundingBox);

		for (int i = 0; i <= 0x80; i++)
			if ((call & 1 << i) != 0) {
				if (children[i] != null)
					createChild(i);
				children[i].insert(boundingBox, tri, level + 1);
			}
	}

	/**
	 * creates a child of this node
	 * 
	 * @param i
	 *            the index of the child
	 */
	private void createChild(int i) {
		if (children == null)
			children = new OctreeNode[8];

		boolean x = (i & 0x55) != 0;
		boolean y = (i & 0x33) != 0;
		boolean z = (i & 0x0F) != 0;
		float[] bounds = new float[] { x ? bnds[0] : cntr[0],
				x ? cntr[0] : bnds[1], y ? bnds[2] : cntr[1],
				y ? cntr[1] : bnds[3], z ? bnds[4] : cntr[2],
				z ? cntr[2] : bnds[5] };
		children[i] = new TriangleOctreeNode(bounds);
	}

	/**
	 * Recursively finds the set of intersections of a segment with the objects
	 * in the element.
	 * 
	 * @param boundingBox
	 *            the axis-aligned bounding box of the segment
	 * @param p0
	 *            the first end point of the segment
	 * @param p1
	 *            the second end point of the segment
	 * @param level
	 *            the current level in the octree
	 */
	@Override
	public LinkedList<float[]> segmentIntersect(float[] boundingBox,
			float[] p0, float[] p1, int level) {

		LinkedList<float[]> ret = null;

		// we're at max level or have reached a threshold - recurse no further
		if (level == Octree.MAX_LEVEL
				|| objects.size() <= Octree.MAX_TRI_TRI_COUNT) {
			float[] intersection = new float[3];
			for (float[] tri : objects) {
				if (Collision.segmentTriIntersect(p0, p1, tri, intersection)) {
					if (ret == null)
						ret = new LinkedList<float[]>();
					ret.add(intersection);
				}
			}
			return ret;
		} else {
			// find out which children intersect the bounding box
			short call = getCalls(boundingBox);

			for (int i = 0; i <= 0x80; i++)
				if ((call & 1 << i) != 0) {
					if (children[i] == null)
						continue;
					LinkedList<float[]> temp = children[i].segmentIntersect(
							boundingBox, p0, p1, level + 1);
					if (temp != null) {
						if (ret == null)
							ret = new LinkedList<float[]>();
						ret.addAll(temp);
					}
				}
		}
		return ret;
	}
}