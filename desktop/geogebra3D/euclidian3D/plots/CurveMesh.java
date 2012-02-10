package geogebra3D.euclidian3D.plots;

import geogebra.common.kernel.Matrix.Coords;
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

	/** error value associated with the segment */
	double error;

	/** length of the segment */
	double length;

	private float scale;

	/** parameter values at the start, middle and end of the segment */
	double[] params = new double[3];

	/** positions at the start/end of the sement */
	Coords[] vertices = new Coords[3];

	/** tangents at start, middle and end positions */
	public Coords[] tangents = new Coords[3];

	/** triangle list element */
	public TriListElem triListElem;

	// we keep a linked list of visible elements in order to avoid drastically
	// changing level of detail

	/** next element in linked list */
	CurveSegment nextInList = null;

	/** previous element in linked list */
	CurveSegment prevInList = null;

	/**
	 * @param mesh
	 *            a reference to the mesh
	 * @param level
	 *            the level in the tree
	 * @param pa1
	 *            parameter value at first endpoint
	 * @param pa2
	 *            parameter value at second endpoint
	 * @param version
	 *            the current version of the object
	 */
	public CurveSegment(CurveMesh mesh, int level, double pa1, double pa2,
			int version) {
		super(mesh, level, false, version);

		Coords v1 = mesh.curve.evaluateCurve(pa1);
		Coords v2 = mesh.curve.evaluateCurve(pa2);
		Coords t1 = approxTangent(pa1, v1);
		Coords t2 = approxTangent(pa2, v2);

		init(pa1, pa2, v1, v2, t1, t2);
	}

	private CurveSegment(CurveMesh mesh, int level, double pa1, double pa2,
			Coords v1, Coords v2, Coords t1, Coords t2, CurveSegment parent,
			int version) {
		super(mesh, level, false, version);
		parents[0] = parent;
		init(pa1, pa2, v1, v2, t1, t2);
	}

	private void init(double pa1, double pa2, Coords v1, Coords v2, Coords t1,
			Coords t2) {

		params[0] = pa1;
		params[2] = pa2;

		vertices[0] = v1;
		vertices[2] = v2;

		tangents[0] = t1;
		tangents[2] = t2;

		length = Math.abs(pa2 - pa1);

		// generate middle point
		params[1] = (pa1 + pa2) * 0.5;
		vertices[1] = calcMainVertex(params[1]);
		tangents[1] = approxTangent(params[1], vertices[1]);

		setBoundingBox();
		generateError();
	}
	
	private Coords calcMainVertex(double u) {
		GeoCurveCartesian3D curve = ((CurveMesh) mesh).curve;
		Coords f = curve.evaluateCurve(u);
		
		// if segment appears partly undefined, project vertex onto border
		final boolean v0def = vertices[0].isDefined();
		final boolean v2def = vertices[2].isDefined();
		if((v0def != v2def) && (level > 5)) {
			// perform binary search for edge
			double p = u;
			double diff = (params[2]-params[0])*0.25;
			final boolean dir = v0def;
			Coords t;
			p += (f.isDefined() ^ dir ? -diff : diff); 
			for (int i = 0; i < 10; i++) {
				diff *= 0.5;
				t = curve.evaluateCurve(p);
				if(t.isDefined())
					f = t;
				p += (t.isDefined() ^ dir ? -diff : diff);
			}
			params[1]=p;
		} else {
			// if infinite, attempt to move in some direction
			double d = 1e-8;
			if (!f.isFinite() || !f.isDefined()) {
				f = curve.evaluateCurve(u + d);
				if (!f.isFinite() || !f.isDefined()) {
					f = curve.evaluateCurve(u - d);
				}
			}
		}

		return f;
	}

	private Coords calcVertex(double u) {
		GeoCurveCartesian3D curve = ((CurveMesh) mesh).curve;
		Coords f = curve.evaluateCurve(u);

		// if infinite, attempt to move in some direction
		double d = 1e-8;
		if (!f.isFinite() || !f.isDefined()) {
			f = curve.evaluateCurve(u + d);
			if (f.isSingular()) {
				f = curve.evaluateCurve(u - d);
			}
		}

		return f;
	}

	/**
	 * Calculates an axis-aligned bounding box based on the three vertices.
	 */
	private void setBoundingBox() {
		final Coords v1 = vertices[0];
		final Coords v2 = vertices[1];
		final Coords v3 = vertices[2];

		double x0, x1, y0, y1, z0, z1, x, y, z;
		final double[] xs = { v2.getX(), v3.getX() };
		final double[] ys = { v2.getY(), v3.getY() };
		final double[] zs = { v2.getZ(), v3.getZ() };

		x0 = x1 = v1.getX();
		y0 = y1 = v1.getY();
		z0 = z1 = v1.getZ();

		for (int i = 0; i < 2; i++) {
			x = xs[i];
			y = ys[i];
			z = zs[i];

			if (Double.isNaN(x) || Double.isInfinite(x)) {
				x0 = Double.NEGATIVE_INFINITY;
				x1 = Double.POSITIVE_INFINITY;
				isSingular = true;
			} else {
				if (x0 > x)
					x0 = x;
				if (x1 < x)
					x1 = x;
			}
			if (Double.isNaN(y) || Double.isInfinite(y)) {
				y0 = Double.NEGATIVE_INFINITY;
				y1 = Double.POSITIVE_INFINITY;
				isSingular = true;
			} else {
				if (y0 > y)
					y0 = y;
				if (y1 < y)
					y1 = y;
			}
			if (Double.isNaN(z) || Double.isInfinite(z)) {
				z0 = Double.NEGATIVE_INFINITY;
				z1 = Double.POSITIVE_INFINITY;
				isSingular = true;
			} else {
				if (z0 > z)
					z0 = z;
				if (z1 < z)
					z1 = z;
			}
		}
		boundingBox = new double[] { x0, x1, y0, y1, z0, z1 };
	}

	private void generateError() {
		// Heron's formula:
		double a = vertices[2].distance(vertices[0]);
		double b = vertices[1].distance(vertices[0]);
		double c = vertices[2].distance(vertices[1]);

		// coefficient based on endpoint tangent difference
		double d = 0;// a * (1 - tangents[0].dotproduct(tangents[2]));

		Coords tan = vertices[2].sub(vertices[0]);
//		d += a
//				* (tan.dotproduct(tangents[0]) + tan.dotproduct(tangents[1]) + tan
//						.dotproduct(tangents[2]));

		double s = 0.5 * (a + b + c);
		error = Math.sqrt(s * (s - a) * (s - b) * (s - c)) + d;
//		System.err.println("tot: " + error + "\td: " + d);
		// alternative error measure for singular segments
		if (isSingular)
			error = CurveMesh.undefErrorConst * length;
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
		Coords d = calcVertex(param + CurveMesh.deltaParam);
		return d.sub(v).normalized();
	}

	@Override
	protected void setHidden(boolean val) {
		if (val)
			mesh.drawList.hide(this);
		else
			mesh.drawList.show(this);
	}

	@Override
	protected void reinsertInQueue() {
		if (mesh.mergeQueue.remove(this))
			mesh.mergeQueue.add(this);
		else if (mesh.splitQueue.remove(this))
			mesh.splitQueue.add(this);
	}

	@Override
	protected void cullChildren() {
		if (!isSplit())
			return;

		if (children[0] != null)
			getChild(0).updateCullInfo();
		if (children[1] != null)
			getChild(1).updateCullInfo();
	}

	@Override
	protected void createChild(int i) {
		// generate both children at once
		children[0] = new CurveSegment((CurveMesh) mesh, level + 1, params[0],
				params[1], vertices[0], vertices[1], tangents[0], tangents[1],
				this, lastVersion);
		children[1] = new CurveSegment((CurveMesh) mesh, level + 1, params[1],
				params[2], vertices[1], vertices[2], tangents[1], tangents[2],
				this, lastVersion);
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
		updateInDrawList = true;

		// we need to reevalutate the vertices, normals, error and culling
		GeoCurveCartesian3D curve = ((CurveMesh) mesh).curve;
		vertices[0] = curve.evaluateCurve(params[0]);
		vertices[1] = curve.evaluateCurve((params[0] + params[2]) * .5);
		vertices[2] = curve.evaluateCurve(params[2]);
		tangents[0] = approxTangent(params[0], vertices[0]);
		tangents[1] = approxTangent(params[1], vertices[1]);
		tangents[2] = approxTangent(params[2], vertices[2]);

		length = Math.abs(params[2] - params[1]);

		setBoundingBox();
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

	private int currentVersion;

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
	 * @param t
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
		lm.setOwner(s);

		return;
	}

	/**
	 * Removes a segment if it is part of the curve.
	 * 
	 * @param t
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

		reinsert(s, currentVersion);

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

		if (s.updateInDrawList) {
			s.updateInDrawList = false;
			TriListElem l = s.triListElem;
			if (l != null) {
				if (l.getIndex() != -1) {
					remove(s);
					TriListElem lm = add(s.vertices[0], s.vertices[2],
							s.tangents[0], s.tangents[2],
							s.cullInfo != CullInfo2.OUT);
					s.triListElem = lm;
					lm.setOwner(s);
				} else {
					CullInfo2 c = s.cullInfo;
					s.cullInfo = CullInfo2.ALLIN;
					TriListElem lm = add(s.vertices[0], s.vertices[2],
							s.tangents[0], s.tangents[2],
							s.cullInfo != CullInfo2.OUT);
					s.triListElem = lm;
					lm.setOwner(s);
					s.cullInfo = c;
				}
				s.reinsertInQueue();
			}
		}
	}

	public void add(DynamicMeshElement2 e, int i) {
		add(e);
	}

	public boolean remove(DynamicMeshElement2 e, int i) {
		return remove(e);
	}

	public void recalculate(int version) {
		currentVersion = version;
		TriListElem e = front;
		LinkedList<DynamicMeshElement2> list = new LinkedList<DynamicMeshElement2>();
		DynamicMeshElement2 el;
		while (e != null) {
			el = (DynamicMeshElement2) e.getOwner();
			if (el.lastVersion != currentVersion)
				list.add(el);
			e = e.getNext();
		}
		Iterator<DynamicMeshElement2> it = list.iterator();
		while (it.hasNext()) {
			DynamicMeshElement2 elem = it.next();
			reinsert(elem, currentVersion);
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

	// DETAIL SETTINGS
	private double maxErrorCoeff = 0.02;

	/** Current level of detail setting */
	public double levelOfDetail = 0.1;

	/**
	 * scaling constant used for setting the error of segments where one or more
	 * vertices are undefined
	 */
	public static final double undefErrorConst = 100;

	/** desired maximum error */
	private double desiredMaxError;

	private CurveSegment root;

	/** reference to the curve being drawn */
	GeoCurveCartesian3D curve;

	/**
	 * @param curve
	 *            The curve to render
	 * @param cullingBox
	 *            Axis-aligned box to cull segments against
	 * @param scale
	 *            How zoomed out things are - used to set width
	 */
	public CurveMesh(GeoCurveCartesian3D curve, double[] cullingBox, float scale) {
		super(new FastBucketPQ(new CurveSplitBucketAssigner(), true),
				new FastBucketPQ(new CurveSplitBucketAssigner(), false),
				new CurveMeshTriList(100, 0, scale * scalingFactor), 1, 2,
				maxLevel);

		setCullingBox(cullingBox);

		this.curve = curve;

		initCurve();
	}

	/**
	 * generates the first few segments
	 */
	private void initCurve() {
		root = new CurveSegment(this, 0, curve.getMinParameter(),
				curve.getMaxParameter(), currentVersion);

		root.updateCullInfo();

		splitQueue.add(root);
		drawList.add(root);

		// split the first few elements in order to avoid problems
		// with periodic funtions
		for (int i = 0; i < 800; i++)
			split(splitQueue.forcePoll());
	}

	@Override
	protected void split(DynamicMeshElement2 t) {

		CurveSegment s = (CurveSegment) t;
		if (s == null) {
			return;
		}

		boolean prev = s.isSplit();
		super.split(s);
		if (!prev && s.isSplit()) {
			CurveSegment left = s.prevInList;
			CurveSegment right = s.nextInList;
			CurveSegment c1 = (CurveSegment) s.children[0];
			CurveSegment c2 = (CurveSegment) s.children[1];

			c1.prevInList = left;
			c1.nextInList = c2;
			c2.prevInList = c1;
			c2.nextInList = right;
			s.nextInList = s.prevInList = null;

			if (left != null) {
				left.nextInList = c1;
				if (c1.level - left.level > 1)
					split(left);
			}

			if (right != null) {
				right.prevInList = c2;
				if (c2.level - right.level > 1) {
					split(right);
				}
			}
		}
	}

	@Override
	protected void merge(DynamicMeshElement2 t) {

		CurveSegment s = (CurveSegment) t;
		if (s == null)
			return;
		boolean prev = s.isSplit();
		super.merge(s);
		if (!prev && s.isSplit()) {
			CurveSegment c1 = (CurveSegment) s.children[0];
			CurveSegment c2 = (CurveSegment) s.children[1];
			CurveSegment left = c1.prevInList;
			CurveSegment right = c2.nextInList;

			c1.prevInList = c1.nextInList = c2.prevInList = c2.nextInList = null;

			s.prevInList = left;
			s.nextInList = right;
			if (left != null)
				left.nextInList = s;
			if (right != null)
				right.prevInList = s;
		}
	}

	@Override
	public void setCullingBox(double[] bb) {
		this.cullingBox = bb;
		double maxWidth, wx, wy, wz;
		wx = bb[1] - bb[0];
		wy = bb[5] - bb[4];
		wz = bb[3] - bb[2];
		maxWidth = wx > wy ? (wx > wz ? wx : wz) : (wy > wz ? wy : wz);
		// update maxErrorCoeff
		desiredMaxError = maxErrorCoeff * Math.pow(maxWidth,1.15);
	}

	/**
	 * Sets the desired level of detail
	 * 
	 * @param l
	 *            any value greater than or equal to zero, typically less than
	 *            one
	 */
	public void setLevelOfDetail(double l) {
		if (l < 0)
			throw new RuntimeException();

		levelOfDetail = l;
		maxErrorCoeff = 1 / (Math.pow(10, 1.6 + l * 0.15));
	}

	/**
	 * 
	 * @return current level of detail - typically in [0,1]
	 */
	public double getLevelOfDetail() {
		return levelOfDetail;
	}

	@Override
	protected void updateCullingInfo() {
		root.updateCullInfo();
	}

	@Override
	protected Side tooCoarse() {
		return splitQueue.peek().getError() > desiredMaxError ? Side.SPLIT
				: Side.MERGE;
	}

	@Override
	protected String getDebugInfo(long time) {
		return curve + ":\tupdate time: " + time + "ms\ttriangles: "
				+ ((CurveTriList) drawList).getTriAmt() + "\t max error: "
				+ splitQueue.peek().getError() + "\t error limit: "
				+ desiredMaxError;
	}

	/**
	 * @return the amount of visible segments
	 */
	public int getVisibleChunks() {
		return ((CurveTriList) drawList).getChunkAmt();
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
