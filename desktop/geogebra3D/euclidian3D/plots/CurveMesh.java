package geogebra3D.euclidian3D.plots;

import geogebra.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.BucketAssigner;
import geogebra3D.euclidian3D.CurveTriList;
import geogebra3D.euclidian3D.TriListElem;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * An element in a CurveMesh.
 * 
 * @author André Eriksson
 */
class CurveSegment extends DynamicMeshElement2 {

	/** a reference to the curve being drawn */
	private GeoCurveCartesian3D curve;

	/** error value associated with the segment */
	double error;

	/** length of the segment */
	double length;

	private float scale;

	/** parameter values at the start and end of the segment */
	double[] params = new double[3];

	/** positions at the start/end of the sement */
	Coords[] vertices = new Coords[3];

	/** tangents at start and end positions */
	public Coords[] tangents = new Coords[3];

	/** triangle list element */
	public TriListElem triListElem;

	/**
	 * @param curve
	 *            a reference to the curve
	 * @param level
	 *            the level in the tree
	 * @param pa1
	 *            parameter value at first endpoint
	 * @param pa2
	 *            parameter value at second endpoint
	 * @param version
	 *            the current version of the object
	 * @param splitQueue
	 *            a reference to the split queue
	 * @param mergeQueue
	 *            a reference to the merge queue
	 */
	public CurveSegment(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, int version, FastBucketPQ splitQueue,
			FastBucketPQ mergeQueue) {
		super(2, 1, level, false, version, splitQueue, mergeQueue);

		this.curve = curve;

		Coords v1 = curve.evaluateCurve(pa1);
		Coords v2 = curve.evaluateCurve(pa2);
		Coords t1 = approxTangent(pa1, v1);
		Coords t2 = approxTangent(pa2, v2);

		init(curve, level, pa1, pa2, v1, v2, t1, t2);
	}

	private CurveSegment(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, Coords v1, Coords v2, Coords t1, Coords t2,
			CurveSegment parent, int version, FastBucketPQ splitQueue,
			FastBucketPQ mergeQueue) {
		super(2, 1, level, false, version, splitQueue, mergeQueue);
		parents[0] = parent;
		init(curve, level, pa1, pa2, v1, v2, t1, t2);
	}

	private void init(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, Coords v1, Coords v2, Coords t1, Coords t2) {
		this.curve = curve;

		params[0] = pa1;
		params[2] = pa2;

		vertices[0] = v1;
		vertices[2] = v2;

		tangents[0] = t1;
		tangents[2] = t2;

		length = v1.distance(v2);

		// generate middle point
		params[1] = (pa1 + pa2) * 0.5;
		vertices[1] = curve.evaluateCurve(params[1]);
		tangents[1] = approxTangent(params[1], vertices[1]);

		setBoundingRadii();
		generateError();
	}

	private void setBoundingRadii() {

		minRadSq = maxRadSq = vertices[0].squareNorm();
		boolean isNaN = Double.isNaN(minRadSq);

		// might as well use the information in the midpoint vertex
		double r = vertices[1].squareNorm();
		if (r > maxRadSq)
			maxRadSq = r;
		else if (r < minRadSq)
			minRadSq = r;
		isNaN |= Double.isNaN(r);

		r = vertices[2].squareNorm();
		if (r > maxRadSq)
			maxRadSq = r;
		else if (r < minRadSq)
			minRadSq = r;
		isNaN |= Double.isNaN(r);

		if (isNaN || Double.isInfinite(maxRadSq)) {
			maxRadSq = Double.POSITIVE_INFINITY;
			isSingular = true;

			if (Double.isNaN(minRadSq))
				minRadSq = 0;
		} else if (maxRadSq > 1e4)
			maxRadSq = Double.POSITIVE_INFINITY;
	}

	private void generateError() {
		// Heron's formula:
		double a = vertices[2].distance(vertices[0]);
		double b = vertices[1].distance(vertices[0]);
		double c = vertices[2].distance(vertices[1]);

		// coefficient based on endpoint tangent difference
		double d = a * (1 - tangents[0].dotproduct(tangents[2]));

		double s = 0.5 * (a + b + c);
		error = Math.sqrt(s * (s - a) * (s - b) * (s - c)) + d;

		// alternative error measure for singular segments
		if (Double.isInfinite(maxRadSq))
			error = 1e10;
		else if (Double.isNaN(error)) {
			// TODO: investigate whether it would be a good idea to
			// attempt to calculate an error from any non-singular
			// dimensions
			d = params[1] - params[0];
			d /= 2;
			d *= d;
			error = d * 1.5;
		}
	}

	/**
	 * Approximates the tangent by a simple forward difference quotient. Should
	 * only be called in the constructor.
	 */
	private Coords approxTangent(double param, Coords v) {
		Coords d = curve.evaluateCurve(param + CurveMesh.deltaParam);
		return d.sub(v).normalized();
	}

	@Override
	protected CullInfo2 getParentCull() {
		if (parents[0] != null)
			return parents[0].cullInfo;
		return null;
	}

	@Override
	protected void setHidden(DynamicMeshTriList2 drawList, boolean val) {
		if (val)
			drawList.hide(this);
		else
			drawList.show(this);
	}

	@Override
	protected void reinsertInQueue() {
		if (mergeQueue.remove(this))
			mergeQueue.add(this);
		else if (splitQueue.remove(this))
			splitQueue.add(this);
	}

	@Override
	protected void cullChildren(double radSq, DynamicMeshTriList2 drawList) {
		if (!isSplit())
			return;

		if (children[0] != null)
			children[0].updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
		if (children[1] != null)
			children[1].updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
	}

	@Override
	protected void createChild(int i) {
		// generate both children at once
		children[0] = new CurveSegment(curve, level + 1, params[0], params[1],
				vertices[0], vertices[1], tangents[0], tangents[1], this,
				lastVersion, splitQueue, mergeQueue);
		children[1] = new CurveSegment(curve, level + 1, params[1], params[2],
				vertices[1], vertices[2], tangents[1], tangents[2], this,
				lastVersion, splitQueue, mergeQueue);
	}

	@Override
	protected double getError() {
		return error;
	}

	/**
	 * sets the scale of the segment
	 * 
	 * @param newScale
	 *            the scale to use
	 */
	public void setScale(float newScale) {
		scale = newScale;
	}

	/**
	 * @return the scale last associated with the segment
	 */
	public float getScale() {
		return scale;
	}

	@Override
	public boolean recalculate(int currentVersion, boolean recurse) {
		if (lastVersion == currentVersion)
			return false;

		lastVersion = currentVersion;

		// we need to reevalutate the vertices, normals, error and culling
		vertices[0] = curve.evaluateCurve(params[0]);
		vertices[1] = curve.evaluateCurve((params[0] + params[2]) * .5);
		vertices[2] = curve.evaluateCurve(params[2]);
		tangents[0] = approxTangent(params[0], vertices[0]);
		tangents[1] = approxTangent(params[1], vertices[1]);
		tangents[2] = approxTangent(params[2], vertices[2]);

		length = vertices[0].distance(vertices[2]);

		setBoundingRadii();
		generateError();

		return true;
	}
}

/**
 * Triangle list used for curves
 * 
 * @author André Eriksson
 */
class CurveMeshTriList extends CurveTriList implements DynamicMeshTriList2 {

	/**
	 * @param capacity
	 *            the goal amount of triangles available
	 * @param marigin
	 *            extra triangle amount
	 * @param scale
	 *            the scale for the segment
	 */
	CurveMeshTriList(int capacity, int marigin, float scale) {
		super(capacity, marigin, scale);
	}

	/**
	 * Adds a segment to the curve. If the segment vertices are unspecified,
	 * these are created.
	 * 
	 * @param s
	 *            the segment to add
	 */
	public void add(DynamicMeshElement2 t) {
		CurveSegment s = (CurveSegment) t;

		if (s.isSingular()) {
			// create an empty TriListElem to show that
			// the element has been 'added' to the list

			s.triListElem = new TriListElem();
			s.triListElem.setOwner(s);
			s.triListElem.setIndex(1);

			if (s.cullInfo == CullInfo2.OUT)
				hide(s);

			return;
		}

		TriListElem lm = add(s.vertices[0], s.vertices[2], s.tangents[0],
				s.tangents[2], s.cullInfo != CullInfo2.OUT);
		
		s.triListElem = lm;
		
		return;
	}

	/**
	 * Removes a segment if it is part of the curve.
	 * 
	 * @param s
	 *            the segment to remove
	 * @return true if the segment was removed, false if it wasn't in the curve
	 *         in the first place
	 */
	public boolean remove(DynamicMeshElement2 t) {
		CurveSegment s = (CurveSegment) t;
		
		boolean ret = hide(s);

		// free triangle
		s.triListElem = null;
		return ret;
	}

	public boolean hide(DynamicMeshElement2 t) {
		CurveSegment s = (CurveSegment) t;

		if (s.isSingular() && s.triListElem != null
				&& s.triListElem.getIndex() != -1) {
			s.triListElem.setIndex(-1);
			return true;
		} else if (hide(s.triListElem)) {
			return true;
		}

		return false;
	}

	public boolean show(DynamicMeshElement2 t) {
		CurveSegment s = (CurveSegment) t;

		if (s.isSingular() && s.triListElem != null
				&& s.triListElem.getIndex() == -1) {
			s.triListElem.setIndex(1);
			return true;
		} else if (show(s.triListElem)) {
			return true;
		}

		return false;
	}

	public void reinsert(DynamicMeshElement2 a, int currentVersion) {

		CurveSegment s = (CurveSegment) a;

		s.recalculate(currentVersion, true);

		float[] vertices = getVertices(s.triListElem);
		float[] normals = getNormals(s.triListElem);

		TriListElem es = s.triListElem;

		setVertices(es, vertices);
		setNormals(es, normals);
	}

	public void add(DynamicMeshElement2 e, int i) {
		add(e);
	}
	
	public boolean remove(DynamicMeshElement2 e, int i) {
		return remove(e);
	}

	public void recalculate(int currentVersion) {
		TriListElem e = front;
		LinkedList<DynamicMeshElement2> list = new LinkedList<DynamicMeshElement2>();
		DynamicMeshElement2 el;
		while (e != null) {
			el = (DynamicMeshElement2) e.getOwner();
			if(el.lastVersion!=currentVersion)
				list.add(el);
			e=e.getNext();
		}
		Iterator<DynamicMeshElement2> it = list.iterator();
		while(it.hasNext()){
			DynamicMeshElement2 elem = it.next();
			reinsert(elem,currentVersion);
		}
	}
}

/**
 * A bucket assigner used for split operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class CurveSplitBucketAssigner implements BucketAssigner<DynamicMeshElement2> {

	public int getBucketIndex(Object o, int bucketAmt) {
		CurveSegment d = (CurveSegment) o;
		double e = d.error;
		int bucket = (int) (Math.pow(e / 100, 0.3) * bucketAmt);
		if (bucket >= bucketAmt)
			bucket = bucketAmt - 1;
		if (bucket <= 0)
			bucket = 1;
		return bucket;
	}
}

/**
 * @author André Eriksson Tree representing a parametric curve
 */
public class CurveMesh extends DynamicMesh2 {

	private static final int maxLevel = 20;

	/** the parameter difference used to approximate tangents */
	public static double deltaParam = 1e-3;

	private static final float scalingFactor = .8f;

	/** the amount of vertices at the end of each segment */
	static public final int nVerts = 4;

	/** relative radius of the segments */
	static public final float radiusFac = 0.1f;

	private CurveSegment root;

	private GeoCurveCartesian3D curve;

	/**
	 * @param curve
	 * @param rad
	 * @param scale
	 */
	public CurveMesh(GeoCurveCartesian3D curve, double rad, float scale) {
		super(new FastBucketPQ(new CurveSplitBucketAssigner(), true),
				new FastBucketPQ(new CurveSplitBucketAssigner(), false),
				(DynamicMeshTriList2)new CurveMeshTriList(100, 0, scale * scalingFactor), 1, 2, maxLevel);

		setRadius(rad);

		this.curve = curve;

		initCurve();
	}

	/**
	 * generates the first few segments
	 */
	private void initCurve() {
		root = new CurveSegment(curve, 0, curve.getMinParameter(),
				curve.getMaxParameter(), currentVersion, splitQueue, mergeQueue);

		root.updateCullInfo(radSq, drawList, splitQueue, mergeQueue);

		splitQueue.add(root);
		drawList.add(root);

		// split the first few elements in order to avoid problems
		// with periodic funtions
		for (int i = 0; i < 30; i++)
			split(splitQueue.poll());

	}

	protected void updateCullingInfo() {
		root.updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
	}

	@Override
	protected Side tooCoarse() {
		// only care about local error
		double minError = 1e-3;
		if (splitQueue.peek().getError() > minError)
			return Side.SPLIT;
		return Side.MERGE;
	}

	@Override
	protected String getDebugInfo(long time) {
		return curve + ":\tupdate time: " + time + "ms\ttriangles: "
				+ ((CurveTriList)drawList).getTriAmt() + "\t max error: "
				+ splitQueue.peek().getError();
	}

	/**
	 * @return the amount of visible segments
	 */
	public int getVisibleChunks() {
		return ((CurveTriList)drawList).getChunkAmt();
	}

	/**
	 * @return the amount of vertices per segment
	 */
	public int getVerticesPerChunk() {
		return 2 * (nVerts + 1);
	}

	/**
	 * rescales the mesh
	 * 
	 * @param scale
	 *            the desired scale
	 */
	public void updateScale(float scale) {
		((CurveTriList) drawList).rescale(scale * scalingFactor);
	}
}
