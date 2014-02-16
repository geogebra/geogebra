package geogebra3D.geogebra.common.geogebra3D.euclidian3D.plots;
//package geogebra3D.euclidian3D.plots;
//
//import java.util.Iterator;
//import java.util.LinkedList;
//
//import geogebra.common.kernel.Matrix.Coords;
//import geogebra3D.euclidian3D.BucketAssigner;
//import geogebra3D.euclidian3D.SurfaceTriList;
//import geogebra3D.euclidian3D.TriList;
//import geogebra3D.euclidian3D.TriListElem;
//import geogebra3D.kernel3D.GeoSurfaceCartesian3D;
//
///**
// * An element in a CurveMesh.
// * 
// * @author André Eriksson
// */
//class ParametricSurfaceDiamond extends DynamicMeshElement2 {
//	// MISC
//	/** error measure */
//	double[] errors = new double[2];
//	/** the area of the diamond (parameter wise) */
//	private double area;
//	/** a reference to the function used */
//	GeoSurfaceCartesian3D function;
//	/** the triangles associated with the diamond */
//	private TriListElem[] triangles = new TriListElem[2];
//
//	// VERTEX
//	/** the parameters values of the vertex */
//	double[] params = new double[2];
//	/** vertex position */
//	Coords vertex;
//	/** vertex normal */
//	private Coords normal;
//
//	// OTHER DIAMONDS
//	/** the other two corners */
//	ParametricSurfaceDiamond[] ancestors = new ParametricSurfaceDiamond[2];
//	/** the index of this diamond within each of its parents */
//	int[] indices = new int[2];
//
//	/**
//	 * the vertices of the segment addressed in the order [start/end][vertex
//	 * num][x/y/z]
//	 */
//	float[][][] points = new float[2][][];
//	/** normals for the vertices */
//	float[][][] normals = new float[2][][];
//
//	/**
//	 * @param function
//	 * @param level
//	 * @param pa1
//	 *            parameter value at first endpoint
//	 * @param pa2
//	 *            parameter value at second endpoint
//	 * @param isClipped
//	 */
//	public ParametricSurfaceDiamond(GeoSurfaceCartesian3D function, int level, double pa1,
//			double pa2, boolean isClipped, int version,
//			FastBucketPQ splitQueue, FastBucketPQ mergeQueue) {
//		super(ParametricSurfaceMesh.nChildren, ParametricSurfaceMesh.nParents, level, isClipped,
//				version, splitQueue, mergeQueue);
//
//		this.function = function;
//		params[0] = pa1;
//		params[1] = pa2;
//		vertex = calcVertex(pa1, pa2);
//		
//		normal = approxNormal(function, pa1, pa2);
//	}
//
//	/**
//	 * @param function
//	 * @param parent0
//	 * @param index0
//	 * @param parent1
//	 * @param index1
//	 * @param a0
//	 * @param a1
//	 * @param level
//	 */
//	ParametricSurfaceDiamond(GeoSurfaceCartesian3D function, ParametricSurfaceDiamond parent0,
//			int index0, ParametricSurfaceDiamond parent1, int index1,
//			ParametricSurfaceDiamond a0, ParametricSurfaceDiamond a1, int level, int version,
//			FastBucketPQ splitQueue, FastBucketPQ mergeQueue) {
//		super(ParametricSurfaceMesh.nChildren, ParametricSurfaceMesh.nParents, level,
//				a0.ignoreFlag || (parent0.ignoreFlag && parent1.ignoreFlag),
//				version, splitQueue, mergeQueue);
//
//		this.function = function;
//		parents[0] = parent0;
//		parents[1] = parent1;
//		indices[0] = index0;
//		indices[1] = index1;
//		ancestors[0] = a0;
//		ancestors[1] = a1;
//		params[0] = (a0.params[0] + a1.params[0]) * 0.5;
//		params[1] = (a0.params[1] + a1.params[1]) * 0.5;
//		vertex = calcVertex(params[0], params[1]);
//		normal = approxNormal(function, params[0], params[1]);
//
//		init();
//	}
//	
//	public String debugCoords() {
//		return "("+vertex.getX()+","+vertex.getY()+","+vertex.getZ()+")";
//	}
//
//	private void init() {
//		setBoundingRadii();
//		setArea();
//		generateError();
//	}
//
//	private Coords calcVertex(double u, double v) {
//		Coords f = function.evaluatePoint(u, v);
//
//		// if infinite, attempt to move in some direction
//		float d = 1e-6f;
//		if (!f.isFinite() || !f.isDefined()) {
//			f = function.evaluatePoint(u + d, v + d);
//			if (f.isSingular()) {
//				f = function.evaluatePoint(u, v + d);
//				if (f.isSingular()) {
//					f = function.evaluatePoint(u - d, v);
//					if (f.isSingular())
//						f = function.evaluatePoint(u, v - d);
//				}
//			}
//		}
//
//		return f;
//	}
//
//	/**
//	 * sets area to the base area of the diamond (parameter wise)
//	 */
//	public void setArea() {
//		if (ancestors[0].params[0] - params[0] != 0)
//			area = Math.abs((ancestors[0].params[0] - params[0])
//					* (((ParametricSurfaceDiamond) parents[0]).params[1] - params[1]));
//		else
//			area = Math
//					.abs((((ParametricSurfaceDiamond) parents[1]).params[0] - params[0])
//							* (ancestors[0].params[1] - params[1]));
//
//	}
//
//	/**
//	 * Sets the (squared) bounding radius of the triangle based on the distances
//	 * from its midpoint to its corner vertices.
//	 */
//	void setBoundingRadii() {
//
//		minRadSq = maxRadSq = ancestors[0].vertex.squareNorm();
//		boolean isNaN = Double.isNaN(minRadSq);
//
//		double r = ancestors[1].vertex.squareNorm();
//		if (r > maxRadSq)
//			maxRadSq = r;
//		else if (r < minRadSq)
//			minRadSq = r;
//		isNaN |= Double.isNaN(r);
//
//		r = ((ParametricSurfaceDiamond) parents[0]).vertex.squareNorm();
//		if (r > maxRadSq)
//			maxRadSq = r;
//		else if (r < minRadSq)
//			minRadSq = r;
//		isNaN |= Double.isNaN(r);
//
//		r = ((ParametricSurfaceDiamond) parents[1]).vertex.squareNorm();
//		if (r > maxRadSq)
//			maxRadSq = r;
//		else if (r < minRadSq)
//			minRadSq = r;
//		isNaN |= Double.isNaN(r);
//
//		r = vertex.squareNorm();
//		if (r > maxRadSq)
//			maxRadSq = r;
//		else if (r < minRadSq)
//			minRadSq = r;
//		isNaN |= Double.isNaN(r);
//
//		if (isNaN || Double.isInfinite(maxRadSq)) {
//			maxRadSq = Double.POSITIVE_INFINITY;
//			isSingular = true;
//
//			if (Double.isNaN(minRadSq))
//				minRadSq = 0;
//		}
//	}
//
//	/**
//	 * Approximates the tangent by a simple forward difference quotient. Should
//	 * only be called in the constructor.
//	 */
//	private Coords approxNormal(GeoSurfaceCartesian3D func, double param1,
//			double param2) {
//		Coords dx = calcVertex(param1 + ParametricSurfaceMesh.normalDelta, param2);
//		Coords dy = calcVertex(param1, param2 + ParametricSurfaceMesh.normalDelta);
//		return dx.sub(vertex).crossProduct(dy.sub(vertex)).normalized();
//	}
//
//	/**
//	 * Computes the error for the diamond.
//	 */
//	void generateError() {
//		Coords p0 = ((ParametricSurfaceDiamond) parents[0]).vertex;
//		Coords p1 = ((ParametricSurfaceDiamond) parents[0]).vertex;
//		Coords a0 = ((ParametricSurfaceDiamond) ancestors[0]).vertex;
//		Coords a1 = ((ParametricSurfaceDiamond) ancestors[0]).vertex;
//
//		Coords v0 = a1.sub(p0);
//		Coords v1 = a0.sub(p0);
//		Coords v2 = a0.sub(p1);
//		Coords v3 = a1.sub(p1);
//
//		Coords n0 = v0.crossProduct(v1);
//		Coords n1 = v2.crossProduct(v3);
//
//		n0.normalize();
//		n1.normalize();
//
//		Coords o0 = vertex.sub(p0);
//		Coords o1 = vertex.sub(p1);
//
//		double d0 = Math.abs(n0.dotproduct(o0));
//		double d1 = Math.abs(n1.dotproduct(o1));
//
//		// vol is proportional to actual volume
//		double vol1 = d0 * n0.norm();
//		double vol2 = d1 * n1.norm();
//
//		if (Double.isNaN(vol1) || Double.isInfinite(vol1))
//			// use a different error measure for infinite points
//			// namely the base area times some constant
//			errors[0] = area * ParametricSurfaceMesh.undefErrorConst;
//		else
//			errors[0] = vol1;
//		if (Double.isNaN(vol2) || Double.isInfinite(vol1))
//			errors[1] = area * ParametricSurfaceMesh.undefErrorConst;
//		else
//			errors[1] = vol2;
//
//		int fac = 0;
//		if (!p0.isDefined())
//			fac++;
//		if (!p1.isDefined())
//			fac++;
//		if (!a0.isDefined())
//			fac++;
//		if (!a1.isDefined())
//			fac++;
//		if (fac == 4)
//			errors[0] = errors[1] = 0;
//		else if (fac > 2)
//			errors[0] *= 2.0;
//		errors[1] *= 2.0;
//	}
//
//	@Override
//	protected CullInfo2 getParentCull() {
//		if (parents[0] != null)
//			return parents[0].cullInfo;
//		return null;
//	}
//
//	@Override
//	protected void setHidden(DynamicMeshTriList2 drawList, boolean val) {
//		ParametricSurfaceTriList t = (ParametricSurfaceTriList) drawList;
//
//		if (val) {
//			t.hide(this, 0);
//			t.hide(this, 1);
//		} else {
//			t.show(this, 0);
//			t.show(this, 1);
//		}
//	}
//
//	@Override
//	protected void reinsertInQueue() {
//		if (mergeQueue.remove(this))
//			mergeQueue.add(this);
//		else if (splitQueue.remove(this))
//			splitQueue.add(this);
//	}
//
//	@Override
//	protected void cullChildren(double radSq, DynamicMeshTriList2 drawList) {
//		if (!isSplit())
//			return;
//
//		if (children[0] != null)
//			children[0].updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
//		if (children[1] != null)
//			children[1].updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
//	}
//
//	@Override
//	protected void createChild(int i) {
//
//		ParametricSurfaceDiamond parent = null;
//		ParametricSurfaceDiamond otherParent = null;
//
//		int index;
//		if (i < 2) {
//			parent = (ParametricSurfaceDiamond) parents[0];
//			if (i == 0)
//				index = indices[0] + 1;
//			else
//				index = indices[0] - 1;
//		} else {
//			parent = (ParametricSurfaceDiamond) parents[1];
//			if (i == 2)
//				index = indices[1] + 1;
//			else
//				index = indices[1] - 1;
//		}
//		
//		if (parent != null)
//			otherParent = (ParametricSurfaceDiamond) parent.getChild(index & 3);
//
//		int parentIndex = i / 2;
//		int ancestorIndex = i == 1 || i == 2 ? 1 : 0;
//		ParametricSurfaceDiamond a0 = (ParametricSurfaceDiamond) parents[parentIndex];
//		ParametricSurfaceDiamond a1 = ancestors[ancestorIndex];
//
//		int otherIndex = i == 0 || i == 2 ? 1 : 0;
//		if (otherParent != null && otherParent.parents[1] == parent)
//			otherIndex |= 2;
//		if (i == 1 || i == 3)
//			children[i] = new ParametricSurfaceDiamond(function, otherParent,
//					otherIndex, this, i, a0, a1, level + 1, lastVersion,
//					splitQueue, mergeQueue);
//		else
//			children[i] = new ParametricSurfaceDiamond(function, this, i, otherParent,
//					otherIndex, a0, a1, level + 1, lastVersion, splitQueue,
//					mergeQueue);
//
//		if (otherParent != null)
//			((ParametricSurfaceDiamond) otherParent).setChild(otherIndex, children[i]);
//	}
//
//	/**
//	 * Sets one of the children of the diamond
//	 * 
//	 * @param i
//	 *            index of the child
//	 * @param e
//	 *            the element to set it to
//	 */
//	void setChild(int i, DynamicMeshElement2 e) {
//		children[i] = e;
//	}
//
//	@Override
//	protected double getError() {
//		return Math.max(errors[0], errors[1]);
//	}
//
//	/**
//	 * @param i
//	 * @return false if child number i is null, otherwise true
//	 */
//	public boolean childCreated(int i) {
//		return children[i] != null;
//	}
//
//	/**
//	 * @return the area of the diamond
//	 */
//	public double getArea() {
//		return area;
//	}
//
//	/**
//	 * @param j
//	 * @return triangle number j
//	 */
//	public TriListElem getTriangle(int j) {
//		return triangles[j];
//	}
//
//	/**
//	 * Freed the j'th triangle
//	 * 
//	 * @param j
//	 */
//	public void freeTriangle(int j) {
//		triangles[j] = null;
//	}
//
//	/**
//	 * @return the middle vertex of the diamond
//	 */
//	public Coords getVertex() {
//		return vertex;
//	}
//
//	/**
//	 * @return the surface normal at the center of the diamond
//	 */
//	public Coords getNormal() {
//		return normal;
//	}
//
//	/**
//	 * Sets triangle number j to e.
//	 * 
//	 * @param j
//	 * @param e
//	 */
//	public void setTriangle(int j, TriListElem e) {
//		triangles[j] = e;
//	}
//
//	private DynamicMeshElement2 getOtherParent(DynamicMeshElement2 p) {
//		if (p == parents[0])
//			return parents[1];
//		return parents[0];
//	}
//
//	/**
//	 * Only move to merge if neither parent is split
//	 */
//	@Override
//	public boolean readyForMerge(DynamicMeshElement2 activeParent) {
//		return !getOtherParent(activeParent).isSplit();
//	}
//
//	@Override
//	public boolean recalculate(int currentVersion, boolean recurse) {
//		if (lastVersion == currentVersion)
//			return false;
//
//		lastVersion = currentVersion;
//
//		// we need to reevalutate the vertices, normals, error and culling
//
//		// make sure ancestors are updated
//		if (true) {
//			parents[0].recalculate(currentVersion, false);
//			parents[1].recalculate(currentVersion, false);
//			ancestors[0].recalculate(currentVersion, false);
//			ancestors[1].recalculate(currentVersion, false);
//		}
//		vertex = calcVertex(params[0], params[1]);
//
//		normal = approxNormal(function, params[0], params[1]);
//
//		setBoundingRadii();
//		setArea();
//		generateError();
//
//		return true;
//	}
//}
//
///**
// * Triangle list used for curves
// * 
// * @author André Eriksson
// */
//class ParametricSurfaceTriList extends TriList implements DynamicMeshTriList2 {
//	// private double totalError = 0;
//	// private double totalArea = 0;
//
//	/**
//	 * @param capacity
//	 *            the goal amount of triangles available
//	 * @param marigin
//	 *            extra triangle amount
//	 */
//	ParametricSurfaceTriList(int capacity, int marigin) {
//		super(capacity, marigin, 9, true);
//	}
//	
//	public void add(DynamicMeshElement2 e) {
//		add(e, 0);
//		add(e, 1);
//	}
//
//	/**
//	 * Adds a triangle to the list.
//	 * 
//	 * @param e
//	 *            The parent diamond of the triangle
//	 * @param j
//	 *            The index of the triangle within the diamond
//	 */
//
//	public void add(DynamicMeshElement2 e, int j) {
//		ParametricSurfaceDiamond s = (ParametricSurfaceDiamond) e;
//		// handle clipping
//		if (s.ignoreFlag || ((ParametricSurfaceDiamond) s.parents[j]).ignoreFlag)
//			return;
//
//		if (s.isSingular()) {
//			// create an empty TriListElem to show that
//			// the element has been 'added' to the list
//			TriListElem g = new TriListElem();
//			g.setOwner(s);
//			s.setTriangle(j, g);
//			return;
//		}
//
//		float[] v = new float[9];
//		float[] n = new float[9];
//
//		calcFloats(s, j, v, n);
//		TriListElem lm = add(v, n);
//		lm.setOwner(s);
//		s.setTriangle(j, lm);
//		return;
//	}
//
//	private void calcFloats(ParametricSurfaceDiamond d, int j, float[] v, float[] n) {
//		ParametricSurfaceDiamond t[] = new ParametricSurfaceDiamond[3];
//		t[1] = (ParametricSurfaceDiamond) d.getParent(j);
//		if (j != 0) {
//			t[0] = d.ancestors[1];
//			t[2] = d.ancestors[0];
//		} else {
//			t[0] = d.ancestors[0];
//			t[2] = d.ancestors[1];
//		}
//		for (int i = 0, c = 0; i < 3; i++, c += 3) {
//			Coords vertex = t[i].getVertex();
//			Coords normal = t[i].getNormal();
//			v[c] = (float) vertex.getX();
//			v[c + 1] = (float) vertex.getY();
//			v[c + 2] = (float) vertex.getZ();
//			n[c] = (float) normal.getX();
//			n[c + 1] = (float) normal.getY();
//			n[c + 2] = (float) normal.getZ();
//		}
//	}
//
//	public boolean hide(DynamicMeshElement2 t) {
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * removes a triangle from the list, but does not erase it
//	 * 
//	 * @param d
//	 *            the diamond
//	 * @param j
//	 *            the triangle index
//	 * @return true if successful, otherwise false
//	 */
//	public boolean hide(ParametricSurfaceDiamond d, int j) {
//
//		if (d.isSingular() && d.getTriangle(j) != null
//				&& d.getTriangle(j).getIndex() != -1) {
//			return true;
//		} else if (hide(d.getTriangle(j))) {
//			return true;
//		}
//
//		return false;
//	}
//
//	public void reinsert(DynamicMeshElement2 a, int currentVersion) {
//		ParametricSurfaceDiamond s = (ParametricSurfaceDiamond) a;
//
//		s.recalculate(currentVersion, true);
//
//		TriListElem e0 = s.getTriangle(0);
//		TriListElem e1 = s.getTriangle(1);
//		if (e0 != null && e0.getIndex() != -1) {
//			float[] v0 = new float[9];
//			float[] n0 = new float[9];
//			calcFloats(s, 0, v0, n0);
//			setVertices(e0, v0);
//			setNormals(e0, n0);
//		}
//		if (e1 != null && e1.getIndex() != -1) {
//			float[] v1 = new float[9];
//			float[] n1 = new float[9];
//			calcFloats(s, 1, v1, n1);
//			setVertices(e1, v1);
//			setNormals(e1, n1);
//		}
//
//		s.reinsertInQueue();
//	}
//
//	public boolean remove(DynamicMeshElement2 e) {
//		boolean b = false;
//		b |= remove(e, 0);
//		b |= remove(e, 1);
//		return b;
//	}
//
//	/**
//	 * Removes a segment if it is part of the function.
//	 * 
//	 * @param e
//	 *            the segment to remove
//	 * @return true if the segment was removed, false if it wasn't in the
//	 *         function in the first place
//	 */
//
//	public boolean remove(DynamicMeshElement2 e, int j) {
//		ParametricSurfaceDiamond d = (ParametricSurfaceDiamond) e;
//
//		// handle clipping
//		if (d.ignoreFlag || ((ParametricSurfaceDiamond) d.parents[j]).ignoreFlag)
//			return false;
//
//		boolean ret = hide(d, j);
//
//		// free triangle
//		d.freeTriangle(j);
//		return ret;
//	}
//
//	public boolean show(DynamicMeshElement2 t) {
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * shows a triangle that has been hidden
//	 * 
//	 * @param e
//	 *            the diamond
//	 * @param j
//	 *            the index of the triangle
//	 * @return true if successful, otherwise false
//	 */
//	public boolean show(DynamicMeshElement2 e, int j) {
//		ParametricSurfaceDiamond d = (ParametricSurfaceDiamond) e;
//		if (d.isSingular() && d.getTriangle(j) != null
//				&& d.getTriangle(j).getIndex() == -1) {
//			return true;
//		} else if (show(d.getTriangle(j))) {
//			return true;
//		}
//
//		return false;
//	}
//
//	public void recalculate(int currentVersion) {
//		TriListElem e = front;
//		LinkedList<DynamicMeshElement2> list = new LinkedList<DynamicMeshElement2>();
//		DynamicMeshElement2 el;
//		while (e != null) {
//			el = (DynamicMeshElement2) e.getOwner();
//			if (el.lastVersion != currentVersion)
//				list.add(el);
//			e = e.getNext();
//		}
//		Iterator<DynamicMeshElement2> it = list.iterator();
//		while (it.hasNext()) {
//			DynamicMeshElement2 elem = it.next();
//			reinsert(elem, currentVersion);
//		}
//	}
//}
//
///**
// * A bucket assigner used for split operations. Sorts based on
// * SurfaceMeshDiamond.error.
// * 
// * @author André Eriksson
// */
//class ParametricSurfaceSplitBucketAssigner implements BucketAssigner<DynamicMeshElement2> {
//
//	public int getBucketIndex(Object o, int bucketAmt) {
//		ParametricSurfaceDiamond d = (ParametricSurfaceDiamond) o;
//		double e = d.getError();
//		int f = (int) (Math.exp(e + 1) * 200) + 3;
//		if (e == 0.0)
//			return 1;
//		return f > bucketAmt - 1 || f < 0 ? bucketAmt - 1 : f;
//	}
//}
//
///**
// * Mesh representing a function in two variables
// * 
// * @author André Eriksson
// */
//public class ParametricSurfaceMesh extends DynamicMesh2 {
//
//	/** number of children of each element */
//	static final int nChildren = 4;
//
//	/** number of parents of each element */
//	static final int nParents = 2;
//
//	// DETAIL SETTINGS
//
//	/**
//	 * used in setRadius() to set the desired error per (visible) area unit
//	 * according to a second degree polynomial with erroCoeffs as coefficients
//	 */
//	private final double[] errorCoeffs = { 0.0015, 0, 0, 0.00012 };
//	private double maxErrorCoeff = 1e-7;
//	
//	/** x/y difference used when estimating normals */
//	public static final double normalDelta = 1e-4;
//
//	/**
//	 * a proportionality constant used for setting the error of diamonds where
//	 * one or more vertices are undefined
//	 */
//	public static final double undefErrorConst = 0.001;
//
//	/** the maximum level of refinement */
//	private static final int maxLevel = 20;
//
//
//	// PRIVATE VARIABLES
//
//	/** a reference to the function being drawn */
//	private GeoSurfaceCartesian3D function;
//
//	private ParametricSurfaceDiamond root;
//
//	/** desired error per visible area unit */
//	private double desiredErrorPerAreaUnit;
//
//	/** desired maximum error */
//	private double desiredMaxError;
//
//	@Override
//	public void setRadius(double r) {
//		radSq = r * r;
//		desiredMaxError = maxErrorCoeff * r;
//		desiredErrorPerAreaUnit = errorCoeffs[0] + errorCoeffs[1] * r
//				+ errorCoeffs[2] * radSq + Math.sqrt(r) * errorCoeffs[3];
//	}
//
//	/**
//	 * 
//	 * @param function
//	 * @param rad
//	 * @param unlimitedRange
//	 */
//	public ParametricSurfaceMesh(GeoSurfaceCartesian3D function, double rad,
//			boolean unlimitedRange) {
//		super(new FastBucketPQ(new ParametricSurfaceSplitBucketAssigner(), true),
//				new FastBucketPQ(new ParametricSurfaceSplitBucketAssigner(), false),
//				new ParametricSurfaceTriList(100, 0), nParents, nChildren, maxLevel);
//		this.function = function;
//
//		setRadius(rad);
//
//		if (unlimitedRange)
//			initMesh(-radSq, radSq, -radSq, radSq);
//		else
//			initMesh(function.getMinParameter(0), function.getMaxParameter(0),
//					function.getMinParameter(1), function.getMaxParameter(1));
//		splitQueue.add(root);
//		drawList.add(root);
//
//		for (int i = 0; i < 100; i++)
//			split((ParametricSurfaceDiamond)splitQueue.forcePoll());
//	}
//
//	/**
//	 * Bootstraps a fairly complex mesh.
//	 * 
//	 * @param xMin
//	 *            the minimum x coordinate
//	 * @param xMax
//	 *            the maximum x coordinate
//	 * @param yMin
//	 *            the minimum y caoordinate
//	 * @param yMax
//	 *            the maximum y coordinate
//	 */
//	private void initMesh(double xMin, double xMax, double yMin, double yMax) {
//		int di, ix, jx;
//		double x, y;
//		ParametricSurfaceDiamond t;
//
//		// base diamonds at level 0
//		ParametricSurfaceDiamond[][] base0 = new ParametricSurfaceDiamond[4][4];
//		// base diamonds at lower levels
//		ParametricSurfaceDiamond[][] base1 = new ParametricSurfaceDiamond[4][4];
//
//		double dx = (xMax - xMin);
//		double dy = (yMax - yMin);
//
//		for (int i = 0; i < 4; i++)
//			for (int j = 0; j < 4; j++) {
//				x = xMin + (i - 0.5) * dx;
//				y = yMin + (j - 0.5) * dy;
//				base0[j][i] = new ParametricSurfaceDiamond(function, 0, x, y,
//						!(i == 1 && j == 1), currentVersion, splitQueue,
//						mergeQueue);
//
//				x = xMin + (i - 1) * dx;
//				y = yMin + (j - 1) * dy;
//				base1[j][i] = t = new ParametricSurfaceDiamond(function,
//						((i ^ j) & 1) != 0 ? -1 : -2, x, y, false,
//						currentVersion, splitQueue, mergeQueue);
//				t.setSplit(true);
//			}
//
//		for (int i = 0; i < 4; i++)
//			for (int j = 0; j < 4; j++) {
//
//				t = base0[j][i];
//				di = ((i ^ j) & 1) != 0 ? 1 : -1;
//				ix = ((2 * i + 1 - di) >> 1) % 4;
//				jx = (2 * j >> 1) % 4;
//				t.parents[0] = base1[jx][ix];
//				ix = ((2 * i + 1 + di) >> 1) % 4;
//				jx = (2 * (j + 1) >> 1) % 4;
//				t.parents[1] = base1[jx][ix];
//				ix = (2 * i >> 1) % 4;
//				jx = ((2 * j + 1 + di) >> 1) % 4;
//				t.ancestors[0] = base1[jx][ix];
//				ix = ((2 * (i + 1)) >> 1) % 4;
//				jx = ((2 * j + 1 - di) >> 1) % 4;
//				t.ancestors[1] = base1[jx][ix];
//				
//				ix = (di < 0 ? 0 : 3);
//				((ParametricSurfaceDiamond) t.parents[0]).setChild(ix, t);
//				t.indices[0] = ix;
//				ix = (di < 0 ? 2 : 1);
//				((ParametricSurfaceDiamond) t.parents[1]).setChild(ix, t);
//				t.indices[1] = ix;
//			}
//		for (int i = 0; i < 4; i++)
//			for (int j = 0; j < 4; j++) {
//				t = base1[j][i];
//				t.ancestors[1] = base1[(j + 3) % 4][i];
//				t.ancestors[0] = base1[(j + 1) % 4][i];
//				t.parents[0] = base1[j][(i + 3) % 4];
//				t.parents[1] = base1[j][(i + 1) % 4];
//			}
//		root = base0[1][1];
//		root.setBoundingRadii();
//		root.setArea();
//		root.generateError();
//	}
//
//	protected void updateCullingInfo() {
//		root.updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
//
//		if (root.childCreated(0))
//			root.getChild(0).updateCullInfo(radSq, drawList, splitQueue,
//					mergeQueue);
//		if (root.childCreated(1))
//			root.getChild(1).updateCullInfo(radSq, drawList, splitQueue,
//					mergeQueue);
//		if (root.childCreated(2))
//			root.getChild(2).updateCullInfo(radSq, drawList, splitQueue,
//					mergeQueue);
//		if (root.childCreated(3))
//			root.getChild(3).updateCullInfo(radSq, drawList, splitQueue,
//					mergeQueue);
//	}
//
//	@Override
//	protected Side tooCoarse() {
//
//		double maxError = splitQueue.peek().getError();
//		if (maxError > desiredMaxError)
//			return Side.SPLIT;
//
//		return Side.MERGE;
//	}
//
//	@Override
//	protected String getDebugInfo(long time) {
//		return function + ":\tupdate time: " + time + "ms\ttriangles: "
//				+ drawList.getTriAmt();
//	}
//
//	/**
//	 * @return the amount of visible segments
//	 */
//	public int getVisibleChunks() {
//		return drawList.getChunkAmt();
//	}
//}
