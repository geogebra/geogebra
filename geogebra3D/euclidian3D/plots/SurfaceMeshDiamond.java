//package geogebra3D.euclidian3D.plots;
//
//import geogebra.Matrix.Coords;
//import geogebra.kernel.GeoFunctionNVar;
//import geogebra3D.euclidian3D.TriListElem;
//
///**
// * A class representing a diamond.
// * 
// * @author André Eriksson
// */
//public class SurfaceMeshDiamond {
//
//	// VERTEX
//	/** the parameters values of the vertex */
//	private double param1, param2;
//	/** vertex position */
//	Coords v;
//	/** vertex normal */
//	Coords normal;
//
//	// OTHER DIAMONDS
//	/** diamond's parents (two of its corners) */
//	SurfaceMeshDiamond[] parents = new SurfaceMeshDiamond[2];
//	/** the other two corners */
//	SurfaceMeshDiamond[] ancestors = new SurfaceMeshDiamond[2];
//	/** children */
//	private SurfaceMeshDiamond[] children = new SurfaceMeshDiamond[4];
//	/** the index of this diamond within each of its parents */
//	int[] indices = new int[2];
//
//	// QUEUE STUFF
//	/** a reference to the merge queue */
//	SurfaceMeshBucketPQ mergeQueue;
//	/** a reference to the split queue */
//	SurfaceMeshBucketPQ splitQueue;
//	/** flag indicating if the diamond is in the merge queue */
//	boolean inMergeQueue = false;
//	/** flag indicating if the diamond is in the split queue */
//	boolean inSplitQueue = false;
//	/** pointer to the next element in a priority queue */
//	SurfaceMeshDiamond nextInQueue;
//	/** pointer to the previous element in a priority queue */
//	SurfaceMeshDiamond prevInQueue;
//	/** index of bucket in priority queue, set to -1 if not a member of a queue */
//	int bucketIndex = -1;
//
//	// CULLING
//	/** (approximately) the minimum distance from the origin to the diamond */
//	double minRadSq;
//	/** (approximately) the maximum distance from the origin to the diamond */
//	double maxRadSq;
//	/** culling info for the diamond */
//	CullInfo cullInfo;
//
//	// FLAGS
//	/** flag indicating if the diamond has been split */
//	private boolean split = false;
//	/** flag used to discern triangles outside domain of defininition */
//	boolean isClipped = false;
//
//	// MISC
//	/** error measure */
//	double[] errors = new double[2];
//	/** level of resolution */
//	final int level;
//	/** the area of the diamond (parameter wise) */
//	public double area;
//	/** a reference to the function used */
//	GeoFunctionNVar func;
//	/** the triangles associated with the diamond */
//	private TriListElem[] triangles = new TriListElem[2];
//
//	// CONSTRUCTORS
//	/**
//	 * A simple constructor. Use only when bootstrapping a mesh.
//	 * 
//	 * @param func
//	 *            a reference to the function being drawn
//	 * @param p1
//	 *            the first parameter value at which to evaluate the function
//	 * @param p2
//	 *            the second parameter value at which to evaluate the function
//	 * @param level
//	 *            the level of the diamond
//	 * @param spQ
//	 *            a reference to the split queue used for the mesh
//	 * @param merQ
//	 *            a reference to the merge queue used for the mesh
//	 */
//	SurfaceMeshDiamond(GeoFunctionNVar func, double p1, double p2, int level,
//			SurfaceMeshBucketPQ spQ, SurfaceMeshBucketPQ merQ) {
//		this.level = level;
//		this.func = func;
//		v = func.evaluatePoint(p1, p2);
//		param1 = p1;
//		param2 = p2;
//		estimateNormal(func);
//		splitQueue = spQ;
//		mergeQueue = merQ;
//	}
//
//	/**
//	 * @param func
//	 *            a reference to the function being drawn
//	 * @param parent0
//	 *            a reference to the diamond's first parent
//	 * @param index0
//	 *            the index of this diamond in the first parents children
//	 * @param parent1
//	 *            a reference to the diamond's second parent
//	 * @param index1
//	 *            the index of this diamond in the second parents children
//	 * @param a0
//	 *            a reference to the first ancestor
//	 * @param a1
//	 *            a reference to the second ancestor
//	 * @param level
//	 *            the diamond's level
//	 * @param spQ
//	 *            a reference to the split queue used for the mesh
//	 * @param merQ
//	 *            a reference to the merge queue used for the mesh
//	 */
//	SurfaceMeshDiamond(GeoFunctionNVar func, SurfaceMeshDiamond parent0,
//			int index0, SurfaceMeshDiamond parent1, int index1,
//			SurfaceMeshDiamond a0, SurfaceMeshDiamond a1, int level,
//			SurfaceMeshBucketPQ spQ, SurfaceMeshBucketPQ merQ) {
//		splitQueue = spQ;
//		mergeQueue = merQ;
//		this.level = level;
//		this.func = func;
//		parents[0] = parent0;
//		parents[1] = parent1;
//		indices[0] = index0;
//		indices[1] = index1;
//		ancestors[0] = a0;
//		ancestors[1] = a1;
//		param1 = (ancestors[0].param1 + ancestors[1].param1) * 0.5;
//		param2 = (ancestors[0].param2 + ancestors[1].param2) * 0.5;
//		v = func.evaluatePoint(param1, param2);
//		estimateNormal(func);
//		setBoundingRadii();
//		setArea();
//		setError();
//
//		// set clipping flag
//		if (a0.isClipped || (parent0.isClipped && parent1.isClipped))
//			isClipped = true;
//	}
//
//	/**
//	 * @return true if the diamond is split, otherwise false
//	 */
//	public boolean isSplit() {
//		return split;
//	}
//
//	/**
//	 * @return true iff at least one child is split
//	 */
//	public boolean childrenSplit() {
//		return (children[0] != null ? children[0].split : false)
//				|| (children[1] != null ? children[1].split : false)
//				|| (children[2] != null ? children[2].split : false)
//				|| (children[3] != null ? children[3].split : false);
//	}
//
//	/**
//	 * sets the split flag to the specified value
//	 * 
//	 * @param val
//	 */
//	public void setSplit(boolean val) {
//		split = val;
//	}
//
//	/**
//	 * @param j
//	 *            either 0 or 1.
//	 * @return the triangle with index j or null if that triangle does not
//	 *         exist.
//	 */
//	public TriListElem getTriangle(int j) {
//		return triangles[j];
//	}
//
//	/**
//	 * sets triangles[j] to t.
//	 * 
//	 * @param j
//	 *            either 0 or 1.
//	 * @param t
//	 */
//	public void setTriangle(int j, TriListElem t) {
//		triangles[j] = t;
//	}
//
//	/**
//	 * releases the triangle with index j
//	 * 
//	 * @param j
//	 *            either 0 or 1.
//	 */
//	public void freeTriangle(int j) {
//		triangles[j] = null;
//	}
//
//	/**
//	 * sets area to the base area of the diamond (parameter wise)
//	 */
//	public void setArea() {
//		if (ancestors[0].param1 - param1 != 0)
//			area = Math.abs((ancestors[0].param1 - param1)
//					* (parents[0].param2 - param2));
//		else
//			area = Math.abs((parents[1].param1 - param1)
//					* (ancestors[0].param2 - param2));
//
//	}
//
//	/**
//	 * Estimates a normal at the current point by evaluating the curve at two
//	 * nearby locations and taking the cross product.
//	 * 
//	 * @param func
//	 *            the function to be evaluated
//	 */
//	private void estimateNormal(GeoFunctionNVar func) {
//		Coords dx = func
//				.evaluatePoint(param1 + OldSurfaceMesh.normalDelta, param2);
//		Coords dy = func
//				.evaluatePoint(param1, param2 + OldSurfaceMesh.normalDelta);
//		normal = dx.sub(v).crossProduct(dy.sub(v)).normalized();
//	}
//
//	/**
//	 * Computes the error for the diamond.
//	 */
//	public void setError() {
//		Coords v0 = ancestors[1].v.sub(parents[0].v);
//		Coords v1 = ancestors[0].v.sub(parents[0].v);
//		Coords v2 = ancestors[0].v.sub(parents[1].v);
//		Coords v3 = ancestors[1].v.sub(parents[1].v);
//
//		Coords n0 = v0.crossProduct(v1);
//		Coords n1 = v2.crossProduct(v3);
//
//		double a0 = n0.norm(); // proportional to area
//		double a1 = n1.norm();
//
//		n0.normalize();
//		n1.normalize();
//
//		Coords o0 = v.sub(parents[0].v);
//		Coords o1 = v.sub(parents[1].v);
//
//		double d0 = Math.abs(n0.dotproduct(o0));
//		double d1 = Math.abs(n1.dotproduct(o1));
//
//		// vol is proportional to actual volume
//		double vol1 = d0 * a0;
//		double vol2 = d1 * a1;
//
//		if (Double.isNaN(vol1) || Double.isInfinite(vol1))
//			// use a different error measure for infinite points
//			// namely the base area times some constant
//			errors[0] = area * OldSurfaceMesh.undefErrorConst;
//		else
//			errors[0] = vol1;
//		if (Double.isNaN(vol2) || Double.isInfinite(vol1))
//			errors[1] = area * OldSurfaceMesh.undefErrorConst;
//		else
//			errors[1] = vol2;
//
//		int fac = 0;
//		if (!parents[0].v.isDefined())
//			fac++;
//		if (!parents[1].v.isDefined())
//			fac++;
//		if (!ancestors[0].v.isDefined())
//			fac++;
//		if (!ancestors[1].v.isDefined())
//			fac++;
//		if (fac == 4)
//			errors[0] = errors[1] = 0;
//		else if (fac > 2)
//			errors[0] *= 2.0;
//		errors[1] *= 2.0;
//	}
//
//	/**
//	 * Sets the (squared) bounding radius of the triangle based on the distances
//	 * from its midpoint to its corner vertices.
//	 */
//	public void setBoundingRadii() {
//		minRadSq = maxRadSq = ancestors[0].v.squareNorm();
//		boolean isNaN = Double.isNaN(minRadSq);
//
//		double r = ancestors[1].v.squareNorm();
//		if (r > maxRadSq)
//			maxRadSq = r;
//		else if (r < minRadSq)
//			minRadSq = r;
//		isNaN |= Double.isNaN(r);
//
//		r = parents[0].v.squareNorm();
//		if (r > maxRadSq)
//			maxRadSq = r;
//		else if (r < minRadSq)
//			minRadSq = r;
//		isNaN |= Double.isNaN(r);
//
//		r = parents[1].v.squareNorm();
//		if (r > maxRadSq)
//			maxRadSq = r;
//		else if (r < minRadSq)
//			minRadSq = r;
//		isNaN |= Double.isNaN(r);
//
//		r = v.squareNorm();
//		if (r > maxRadSq)
//			maxRadSq = r;
//		else if (r < minRadSq)
//			minRadSq = r;
//		isNaN |= Double.isNaN(r);
//
//		if (Double.isInfinite(minRadSq))
//			minRadSq = 0;
//		if (isNaN || Double.isInfinite(maxRadSq)) {
//			maxRadSq = Double.POSITIVE_INFINITY;
//			minRadSq = 0;
//		}
//	}
//
//	/**
//	 * @param ix
//	 *            index of child
//	 * @param t
//	 *            reference to child
//	 */
//	public void setChild(int ix, SurfaceMeshDiamond t) {
//		children[ix] = t;
//	}
//
//	/**
//	 * note: this assumes that p isn't a copy of a parent, or some other diamond
//	 * 
//	 * @param p
//	 *            a reference to a parent
//	 * @return the other parent
//	 */
//	public SurfaceMeshDiamond getOtherParent(SurfaceMeshDiamond p) {
//		if (p == parents[0])
//			return parents[1];
//		return parents[0];
//	}
//
//	/**
//	 * A function for getting a reference to a specific child. If the child
//	 * doesn't already exist, it is created.
//	 * 
//	 * @param i
//	 *            the index of the child
//	 * @return a reference to the child
//	 */
//	public SurfaceMeshDiamond getChild(int i) {
//		if (children[i] == null) {
//
//			SurfaceMeshDiamond parent = null;
//			SurfaceMeshDiamond otherParent = null;
//
//			int index;
//			if (i < 2) {
//				parent = parents[0];
//				if (i == 0)
//					index = indices[0] + 1;
//				else
//					index = indices[0] - 1;
//			} else {
//				parent = parents[1];
//				if (i == 2)
//					index = indices[1] + 1;
//				else
//					index = indices[1] - 1;
//			}
//
//			if (parent != null)
//				otherParent = parent.getChild(index & 3);
//
//			int parentIndex = i / 2;
//			int ancestorIndex = i == 1 || i == 2 ? 1 : 0;
//			SurfaceMeshDiamond a0 = parents[parentIndex];
//			SurfaceMeshDiamond a1 = ancestors[ancestorIndex];
//
//			int otherIndex = i == 0 || i == 2 ? 1 : 0;
//			if (otherParent != null && otherParent.parents[1] == parent)
//				otherIndex |= 2;
//			if (i == 1 || i == 3)
//				children[i] = new SurfaceMeshDiamond(func, otherParent,
//						otherIndex, this, i, a0, a1, level + 1, splitQueue,
//						mergeQueue);
//			else
//				children[i] = new SurfaceMeshDiamond(func, this, i,
//						otherParent, otherIndex, a0, a1, level + 1, splitQueue,
//						mergeQueue);
//
//			if (otherParent != null)
//				otherParent.setChild(otherIndex, children[i]);
//		}
//		return children[i];
//	}
//
//	/**
//	 * Checks if a child has been created.
//	 * 
//	 * @param i
//	 *            the index of the child
//	 * @return false if the child is null, otherwise true.
//	 */
//	public boolean childCreated(int i) {
//		return children[i] != null;
//	}
//
//	/**
//	 * @param radSq
//	 * @param drawList
//	 */
//	public void updateCullInfo(double radSq, SurfTriList drawList) {
//		// ignore clipped diamonds
//		if (isClipped)
//			return;
//		CullInfo parentCull = ancestors[1].cullInfo;
//		CullInfo oldCull = cullInfo;
//
//		if (parentCull == CullInfo.ALLIN || parentCull == CullInfo.OUT)
//			cullInfo = parentCull;
//		else {
//			if (maxRadSq < radSq)
//				cullInfo = CullInfo.ALLIN;
//			else if (minRadSq < radSq)
//				cullInfo = CullInfo.SOMEIN;
//			else
//				cullInfo = CullInfo.OUT;
//		}
//
//		if (oldCull != cullInfo || cullInfo == CullInfo.SOMEIN) {
//			if (cullInfo == CullInfo.OUT) {
//				if (triangles[0] != null && triangles[0].getIndex() != -1)
//					drawList.hide(this, 0);
//				if (triangles[1] != null && triangles[1].getIndex() != -1)
//					drawList.hide(this, 1);
//			} else if (cullInfo == CullInfo.ALLIN
//					|| cullInfo == CullInfo.SOMEIN) {
//				if (triangles[0] != null && triangles[0].getIndex() == -1)
//					drawList.show(this, 0);
//				if (triangles[1] != null && triangles[1].getIndex() == -1)
//					drawList.show(this, 1);
//			}
//			// reinsert into priority queue
//			if (oldCull == CullInfo.OUT && cullInfo != CullInfo.OUT
//					|| oldCull != CullInfo.OUT && cullInfo == CullInfo.OUT) {
//				if (inMergeQueue) {
//					mergeQueue.remove(this);
//					mergeQueue.add(this);
//				} else if (inSplitQueue) {
//					splitQueue.remove(this);
//					splitQueue.add(this);
//				}
//			}
//		}
//
//		if (split)
//			for (int i = 0; i < 4; i += 2)
//				if (childCreated(i)) {
//					SurfaceMeshDiamond c = children[i];
//					if (c.parents[0] == this) {
//						if (c.childCreated(0))
//							c.children[0].updateCullInfo(radSq, drawList);
//						if (c.childCreated(1))
//							c.children[1].updateCullInfo(radSq, drawList);
//					} else {
//						if (c.childCreated(2))
//							c.children[2].updateCullInfo(radSq, drawList);
//						if (c.childCreated(3))
//							c.children[3].updateCullInfo(radSq, drawList);
//					}
//				}
//	}
//
//	/** inserts the diamond into the split queue */
//	public void addToSplitQueue() {
//		if (!inSplitQueue) {
//			splitQueue.add(this);
//			inSplitQueue = true;
//		}
//	}
//
//	/** removes the diamond from the split queue */
//	public void removeFromSplitQueue() {
//		splitQueue.remove(this);
//		inSplitQueue = false;
//	}
//
//	/** adds the diamond to the merge queue */
//	public void addToMergeQueue() {
//		if (!inMergeQueue) {
//			mergeQueue.add(this);
//			inMergeQueue = true;
//		}
//	}
//
//	/** removes the diamond from the merge queue */
//	public void removeFromMergeQueue() {
//		if (inMergeQueue) {
//			mergeQueue.remove(this);
//			inMergeQueue = false;
//		}
//	}
//
//	@Override
//	public String toString() {
//		return "[l=" + level + ", e=(" + errors[0] + ", " + errors[1] + ") ("
//				+ v.getX() + "," + v.getY() + ")]";
//	}
//};