package org.geogebra.common.geogebra3D.euclidian3D.plots;

import java.util.Date;

import org.geogebra.common.geogebra3D.euclidian3D.plots.java.nio.FloatBuffer;

/**
 * An enumeration for describing the culling status of a diamond
 * 
 * @author Andre Eriksson
 */
enum CullInfo2 {
	/** the entire diamond is in the viewing sphere */
	ALLIN,
	/** part of the diamond is in the viewing sphere */
	SOMEIN,
	/** the entire diamond is outside the viewing sphere */
	OUT;
}

/**
 * Abstract class representing an element to be used in a dynamic mesh.
 * 
 * @author Andre Eriksson
 */
abstract class DynamicMeshElement2 {
	private boolean isSplit;
	/** children of the element */
	protected DynamicMeshElement2[] children;
	/** parents of the element */
	protected DynamicMeshElement2[] parents;

	/** relative level of the element */
	protected final int level;

	/** set to true if the element should be ignored when drawing/updating */
	final boolean ignoreFlag;

	/** the mesh the element belongs to */
	protected final DynamicMesh2 mesh;

	/** true if any evaluated point of the segment is singular */
	protected boolean isSingular;

	/** axis-aligned bounding box {x_min, x_max, y_min, y_max, z_min, z_max} */
	double[] boundingBox;

	/** Culling status of the element */
	public CullInfo2 cullInfo;

	/** previous version of the element - changes when the function changes */
	protected int lastVersion;

	boolean updateInDrawList = false;

	// bucket stuff//
	/** previous element in bucket */
	DynamicMeshElement2 bucket_prev;
	/** next element in bucket */
	DynamicMeshElement2 bucket_next;
	/** index of object in bucket */
	int bucket_index;
	/** bucket the element belongs to */
	FastBucketPQ bucket_owner;

	/**
	 * 
	 * @param mesh
	 *            the mesh this element belongs to
	 * @param level
	 *            the relative level of the element
	 * @param ignoreFlag
	 *            true if the element shouldn't be updated or drawn
	 * @param version
	 *            current version of the element
	 */
	public DynamicMeshElement2(DynamicMesh2 mesh, int level,
			boolean ignoreFlag, int version) {
		this.level = level;
		this.ignoreFlag = ignoreFlag;
		this.lastVersion = version;
		this.mesh = mesh;
		children = new DynamicMeshElement2[mesh.nChildren];
		parents = new DynamicMeshElement2[mesh.nParents];
	}

	/**
	 * merg
	 * 
	 * @return the level of the element
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return true if the element has been split, otherwise false.
	 */
	public boolean isSplit() {
		return isSplit;
	}

	/**
	 * @param b
	 *            true if the element has been split, false if it has been
	 *            merged
	 */
	public void setSplit(boolean b) {
		isSplit = b;
	}

	/**
	 * @param i
	 *            child index < nChildren
	 * @return the child at index i
	 */
	public DynamicMeshElement2 getChild(int i) {
		if (i >= mesh.nChildren)
			throw new IndexOutOfBoundsException();
		if (children[i] == null) {
			createChild(i);
		}

		if (children[i].lastVersion != lastVersion)
			children[i].recalculate(lastVersion, true);

		return children[i];
	}

	/**
	 * Generates one or more children
	 * 
	 * @param i
	 *            the child needed
	 */
	protected abstract void createChild(int i);

	/**
	 * @return the error value associated with the segment
	 */
	protected abstract double getError();

	/**
	 * @param i
	 *            parent index < nParents
	 * @return the parent at index i
	 */
	public DynamicMeshElement2 getParent(int i) {
		if (parents[i] != null && parents[i].lastVersion != lastVersion)
			parents[i].recalculate(lastVersion, true);

		return parents[i];
	}

	/**
	 * Sets the culling flags of the element, based on the culling box. Also
	 * handles drawing list and queue when the culling status changes.
	 */
	public void updateCullInfo() {

		if (this.lastVersion != mesh.currentVersion) {
			mesh.drawList.reinsert(this, mesh.currentVersion);
		}

		if (ignoreCull() || ignoreFlag)
			return;

		final CullInfo2 prev = cullInfo;

		// update cull flag
		cullInfo = getCullInfo();

		// handle new culling info
		if (prev != cullInfo || cullInfo == CullInfo2.SOMEIN) {

			// hide/show the element
			setHidden(cullInfo == CullInfo2.OUT);

			// reinsert into priority queue
			if (prev == CullInfo2.OUT
					|| (cullInfo == CullInfo2.OUT && bucket_owner != null))
				reinsertInQueue();
		}

		// update children
		cullChildren();
	}

	/**
	 * Culls the element based on its bounding box
	 * 
	 * @param bb
	 *            The box to cull against
	 * @return OUT if there's no overlap, ALLIN if the element is contained in
	 *         the culling box, OUT otherwise
	 */
	private CullInfo2 getCullInfo() {
		final double[] cc = boundingBox;
		final double[] bb = mesh.cullingBox;
		if (cc[0] <= bb[1] && cc[2] <= bb[3] && cc[4] <= bb[5]
				&& cc[1] >= bb[0] && cc[3] >= bb[2] && cc[5] >= bb[4]) {
			// we have intersection - check containment
			if (cc[0] >= bb[0] && cc[2] >= bb[2] && cc[4] >= bb[4]
					&& cc[1] <= bb[1] && cc[3] <= bb[3] && cc[5] <= bb[5]) {
				return CullInfo2.ALLIN;
			}
			return CullInfo2.SOMEIN;
		}
		return CullInfo2.OUT;
	}

	/**
	 * Override if culling is to be ignored in certain cases. Always returns
	 * false by default.reinsertInQueue
	 * 
	 * @return whether culling should be ignored or not
	 */
	protected boolean ignoreCull() {
		return false;
	}

	/**
	 * @return true if any vertex in the segment is singular, otherwise false.
	 */
	public boolean isSingular() {
		return isSingular;
	}

	/**
	 * Hides/shows the element
	 * 
	 * @param val
	 *            true if the element should be hidden, otherwise false
	 */
	abstract protected void setHidden(boolean val);

	/**
	 * Reinsert the element into whichever queue it's in
	 **/
	abstract protected void reinsertInQueue();

	/**
	 * Recursively culls children.
	 */
	abstract protected void cullChildren();

	/**
	 * @return true if all children have been split
	 */
	public boolean childrenSplit() {
		boolean ret = false;
		for (int i = 0; i < mesh.nChildren; i++)
			ret = ret || (children[i] != null ? children[i].isSplit() : false);
		return ret;
	}

	/**
	 * Checks if the element is ready to be moved from the split to the merge
	 * queue.
	 * 
	 * @param activeParent
	 *            the parent that is trying to initiate the move
	 * @return true if the element can be moved, otherwise false
	 */
	public boolean readyForMerge(DynamicMeshElement2 activeParent) {
		return true;
	}

	/**
	 * Reevaluates the element vertices, error, etc. - called when the function
	 * has changed.
	 * 
	 * @param currentVersion
	 *            The current version of the function
	 * @param recurse
	 *            Whether or not to recurse down the tree
	 * @return true if and only if the element was recalculated
	 */
	public abstract boolean recalculate(int currentVersion, boolean recurse);

}

/**
 * An abstract class representing a mesh that can be dynamically refined.
 * Refines the mesh based on two priority queues sorted by a user-defined error
 * measure. One priority queue handles merge operations and another handles
 * split operations.
 * 
 * @author Andre Eriksson
 */
public abstract class DynamicMesh2 {

	/** the queue used for merge operations */
	protected FastBucketPQ mergeQueue;
	/** the queue used for split operations */
	protected FastBucketPQ splitQueue;

	/** controls if debug info is displayed or not */
	protected static final boolean debugInfo = false;

	/** box to cull elements against */
	protected double[] cullingBox;

	/** the triangle list used by the mesh */
	protected DynamicMeshTriList2 drawList;

	/** the maximum amount of operations to perform in one update */
	private int stepRefinement = 100;

	/** The number of children of each node */
	final int nChildren;

	/** The number of parents of each node */
	final int nParents;

	/** current version of the mesh - increments when the function is changed */
	protected int currentVersion = 0;

	protected boolean noUpdate = false;

	/** used in optimizeSub() */
	protected enum Side {
		/** indicates that elements should be merged */
		MERGE,
		/** indicates that elements should be split */
		SPLIT,
		/** indicates that no action should be taken */
		NONE
	}

	/**
	 * @param mergeQueue
	 *            the PQ used for merge operations
	 * @param splitQueue
	 *            the PQ used for split operations
	 * @param drawList
	 *            the list used for drawing
	 * @param nParents
	 *            number of parents of each node
	 * @param nChildren
	 *            number of children of each node
	 * @param maxLevel
	 *            maximum refinement depth
	 */
	DynamicMesh2(FastBucketPQ mergeQueue, FastBucketPQ splitQueue,
			DynamicMeshTriList2 drawList, int nParents, int nChildren,
			int maxLevel) {
		this.mergeQueue = mergeQueue;
		this.splitQueue = splitQueue;
		this.drawList = drawList;
		this.nParents = nParents;
		this.nChildren = nChildren;
	}

	/**
	 * Performs a set number (stepRefinement) of splits/merges
	 * 
	 * @return false if no more updates are needed
	 */
	public boolean optimize() {
		return optimizeSub(stepRefinement);
	}

	/**
	 * @param cullingBox
	 *            new culling box
	 */
	public void setCullingBox(double[] cullingBox) {
		this.cullingBox = cullingBox;
		noUpdate = false;
	}

	/**
	 * @return Returns a FloatBuffer containing the current mesh as a triangle
	 *         list. Each triangle is represented as 9 consecutive floats. The
	 *         FloatBuffer will probably contain extra floats - use
	 *         getTriangleCount() to find out how many floats are valid.
	 */
	public FloatBuffer getVertices() {
		return drawList.getTriangleBuffer();
	}

	/**
	 * @return Returns a FloatBuffer containing the current mesh as a triangle
	 *         list.
	 */
	public FloatBuffer getNormals() {
		return drawList.getNormalBuffer();
	}

	/**
	 * @return the amount of triangles in the current mesh.
	 */
	public int getTriangleCount() {
		return drawList.getTriAmt();
	}

	/**
	 * Contains the logic for split/merge operations.
	 * 
	 * @param maxCount
	 *            maximum amount of operations to be performed
	 */
	private boolean optimizeSub(int maxCount) {
		int count = 0;

		long t1 = new Date().getTime();

		updateCullingInfo();

		if (noUpdate)
			return false;

		Side side = tooCoarse();
		Side prevSide = null;

		boolean switched = false;

		do {
			if (side == Side.MERGE) {
				merge(mergeQueue.poll());
			} else {
				split(splitQueue.poll());
			}

			if (prevSide != side) {
				if (switched) {
					// noUpdate = true;
					// break;
				}
				switched = true;
			}

			prevSide = side;
			side = tooCoarse();
			count++;
		} while (side != Side.NONE && count < maxCount);

		if (debugInfo)
			System.out.println(getDebugInfo(new Date().getTime() - t1));

		return count <= 4; // why 4?
	}

	/**
	 * updates the culling info of each element
	 */
	protected abstract void updateCullingInfo();

	/**
	 * @param time
	 *            the time of the last update
	 * @return a string with the desired debug info
	 */
	protected abstract String getDebugInfo(long time);

	/**
	 * @return true if the mesh should be refined, otherwise false
	 */
	protected abstract Side tooCoarse();

	/**
	 * Perform a merge operation on the target element.
	 * 
	 * @param t
	 *            the target element
	 */
	protected void merge(DynamicMeshElement2 t) {
		// skip if null, if already merged or if below level 1
		if (t == null || t.getLevel() < 1 || !t.isSplit())
			return;

		// force update
		if (t.lastVersion != currentVersion) {
			t.recalculate(currentVersion, false);
		}

		// switch queues
		mergeQueue.remove(t);
		splitQueue.add(t);

		// mark as merged
		t.setSplit(false);

		// handle children
		for (int i = 0; i < nChildren; i++) {
			DynamicMeshElement2 c = t.getChild(i);
			if (c.readyForMerge(t)) {
				splitQueue.remove(c);

				if (c.isSplit())
					mergeQueue.add(c);
			}

			// remove children from draw list
			drawList.remove(c, (c.parents[0] == t ? 0 : 1));
		}

		// handle parents
		for (int i = 0; i < nParents; i++) {
			DynamicMeshElement2 p = t.getParent(i);
			if (!p.childrenSplit()) {
				p.updateCullInfo();
				mergeQueue.add(p);
			}
		}

		// add to draw list
		drawList.add(t);
		return;
	}

	/**
	 * Perform a split operation on the target element.
	 * 
	 * @param t
	 *            the target element
	 */
	protected void split(DynamicMeshElement2 t) {
		if (t == null || t.ignoreFlag)
			return;

		// don't split an element that has already been split
		if (t.isSplit())
			return;

		// switch queues
		splitQueue.remove(t);
		mergeQueue.add(t);

		// if(((CurveSegment)t).vertices[0].getX()==7.03125)
		// System.err.print("");

		// mark as split
		t.setSplit(true);

		// handle parents
		for (int i = 0; i < nParents; i++) {
			DynamicMeshElement2 p = t.getParent(i);
			if (p != null) {
				split(p);

				mergeQueue.remove(p);
			}
		}

		// handle children
		for (int i = 0; i < nChildren; i++) {
			DynamicMeshElement2 c = t.getChild(i);
			if (c.lastVersion != currentVersion) {
				c.recalculate(currentVersion, false);
			}

			if (!c.ignoreFlag) {

				c.updateCullInfo();

				// add child to drawing list
				if (!c.isSplit()) {
					drawList.add(c, (c.parents[0] == t ? 0 : 1));
					splitQueue.add(c);
				}
			}
		}

		// remove from drawing list
		drawList.remove(t);
	}

	/**
	 * Reevaluates vertices, errors, etc. for all elements
	 */
	public void updateParameters() {
		currentVersion++;

		noUpdate = false;

		// update all elements currently in draw list
		drawList.recalculate(currentVersion);

		updateCullingInfo();

		// update elements in queues
		splitQueue.recalculate(currentVersion, drawList);
		mergeQueue.recalculate(currentVersion, drawList);

		updateCullingInfo();
	}
}
