package geogebra3D.euclidian3D.plots;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.CurveEvaluable3D;
import geogebra3D.euclidian3D.BucketAssigner;
import geogebra3D.euclidian3D.CurveTriList;
import geogebra3D.euclidian3D.TriListElem;

import java.util.HashMap;
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
	double param;

	/** positions at the start/end of the sement */
	Coords vertex;
	Coords alt = null;
	Coords altDer = null;
	double altParam;

	/** tangents at start, middle and end positions */
	public Coords deriv;

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
	CurveSegment(CurveMesh mesh, int level, double pa,
			int version) {
		super(mesh, level, true, version);
		param = pa;
		setSplit(true);
		vertex = calcVertex(pa);
		deriv = approxDeriv(pa, vertex);
	}

	CurveSegment(CurveMesh mesh, int level, CurveSegment p0, CurveSegment p1, int version) {
		super(mesh, level, false, version);
		parents[0] = p0;
		parents[1] = p1;
		init();
	}

	private void init() {
		double pa0 = ((CurveSegment)parents[0]).getParam(0);
		double pa1 = ((CurveSegment)parents[1]).getParam(1);
		length = Math.abs(pa0 - pa1);

		alt = altDer = null;
		
		// generate middle point
		param = (pa0 + pa1) * 0.5;
		vertex = calcMainVertex(param);

		setBoundingBox();
		generateError();
	}
	
	private static final double discontThreshold = 0.5;
	private static final double warpedDiscontThreshold = Math.cos(Math.atan(discontThreshold));
	
	private Coords calcMainVertex(double u) {
		final CurveSegment p0 = (CurveSegment) parents[0];
		final CurveSegment p1 = (CurveSegment) parents[1];
		Coords f = calcVertex(u);
		
		// if segment appears partly undefined, project vertex onto border
		final boolean v0def = p0.getVertex(0).isDefined();
		final boolean v2def = p1.getVertex(1).isDefined();
		if(v0def != v2def) {
			// perform binary search for edge
			double ui = u;
			final boolean dir = v0def;
			double lop = p0.getParam(0);
			double hip = p1.getParam(1);
			double delta = (hip-lop) * 0.25;
			Coords lo = p0.getVertex(0);
			Coords hi = p1.getVertex(1);
			if (dir ^ f.isDefined()) {
				hi = f;
				hip = ui;
				ui -= delta;
			} else {
				lo = f;
				lop = ui;
				ui += delta;
			}

			f = calcVertex(ui);
			for (int i = 0; i < 30; i++) {
				
				delta *= 0.5;
				if (dir ^ f.isDefined()) {
					hi = f;
					hip = ui;
					ui -= delta;
				} else {
					lo = f;
					lop = ui;
					ui += delta;
				}
				f = calcVertex(ui);
			}
			alt = hi;
			f = lo;
			param = lop;
			altParam = hip;
		} else {
			// if infinite, attempt to move in some direction
			final double d = 1e-8;
			Coords der;
			if (!f.isFinite() || !f.isDefined()) {
				f = calcVertex(u + d);
				param = u+d;
				if (!f.isFinite() || !f.isDefined()) {
					f = calcVertex(u - d);
					param = u-d;
					der = f.sub(calcVertex(u - d - CurveMesh.deltaParam)).mul(CurveMesh.invDeltaParam);
				} else {
					der = calcVertex(u + d + CurveMesh.deltaParam).sub(f).mul(CurveMesh.invDeltaParam);
				}
			} else {
				der = calcVertex(u + CurveMesh.deltaParam).sub(f).mul(CurveMesh.invDeltaParam);
			}
			
			//perform discontinuity check
			Coords f0 = f;
			Coords der0 = der;
			
			boolean discontinuous = false;
			Coords lo = p0.getVertex(0);	// point at start of interval
			Coords hi = p1.getVertex(1);	// point at end of interval
			Coords loder = p0.getDerivative(0);	// derivative at start of interval
			Coords hider = p1.getDerivative(1);	// derivative at end of interval
			double lop = p0.getParam(0);		// parameter at start of interval
			double hip = p1.getParam(1);		// parameter at end of interval
			double ui = u;				// current parameter
			Coords expl = der.add(loder).mul(0.5*(ui-lop)); // projected difference left
			Coords expr = der.add(hider).mul(0.5*(hip-ui)); // projected difference right
			Coords tll = f.sub(lo);				// actual difference left
			Coords trr = hi.sub(f);				// actual difference right
			double ldot = tll.dotproduct(expl);	// dot product precomputed for efficiency
			double rdot = trr.dotproduct(expr); // dot product precomputed for efficiency
			boolean c1, c2;				// whether or not left and right segments appear continuous
			
			
			// attempt to estimate continuity by comparing angle or vector difference
			if (ldot < expl.squareNorm())
				c1 = ldot/(expl.norm() * tll.norm()) > warpedDiscontThreshold;
			else
				c1 = tll.sub(expl).norm()/expl.norm() < discontThreshold;
			if (rdot < expr.squareNorm())
				c2 = rdot/(expr.norm() * trr.norm()) > warpedDiscontThreshold;
			else
				c2 = trr.sub(expr).norm()/expr.norm() < discontThreshold;
			
			if (c1 ^ c2) {
				discontinuous = true;
				//probable discontinuity detected - perform binary search
				double delta = (hip - lop) * 0.25;
				if (c2) {
					if (f.isFinite()) {
						hi = f;
						hip = ui;
						hider = der;
						ui -= delta;
					}
				} else {
					if (f.isFinite()) {
						lo = f;
						lop = ui;
						loder = der;
						ui += delta;
					}
				}

				f = calcVertex(ui);
				for (int i = 0; i < 15; i++) {
					tll = f.sub(lo);
					trr = hi.sub(f);
					
					der = calcVertex(ui + CurveMesh.deltaParam).sub(f).mul(CurveMesh.invDeltaParam);
					
					expl = der.add(loder).mul(0.5*(ui-lop)); // projected difference left
					expr = der.add(hider).mul(0.5*(hip-ui)); // projected difference right
					ldot = tll.dotproduct(expl);			   // actual difference left
					rdot = trr.dotproduct(expr);			   // actual difference right
					
					if (ldot < expl.squareNorm())
						c1 = ldot/(expl.norm() * tll.norm()) > warpedDiscontThreshold; 
					else
						c1 = tll.sub(expl).norm()/expl.norm() < discontThreshold;
					if (rdot < expr.squareNorm())
						c2 = rdot/(expr.norm() * trr.norm()) > warpedDiscontThreshold;
					else
						c2 = trr.sub(expr).norm()/expr.norm() < discontThreshold;
					
					delta *= 0.5;
					if(c2 && c1) {
						discontinuous = false;
						break;
					}
					
					if (c2) {
						if(f.isFinite()){
							hi = f;
							hip = ui;
							hider = der;
							ui -= delta;
						}
					} else {
						if(f.isFinite()){
							lo = f;
							lop = ui;
							loder = der;
							ui += delta;
						}
					}
					f = calcVertex(ui);
				}
			}
			
			if(discontinuous) {
				alt = hi;
				f = lo;
				deriv = loder;
				altDer=hider;
				param = lop;
				altParam = hip;
			} else {
				f = f0;
				deriv = der0;
			}
		}
		return f;
	}

	private Coords calcVertex(double u) {
		final CurveMesh m = (CurveMesh) mesh;
		final CurveEvaluable3D curve = m.curve;
		if(m.precalcVertices.containsKey(u))
			return m.precalcVertices.get(u);

		Coords f = curve.evaluateCurve3D(u);

		// if infinite, attempt to move in some direction
		double d = 1e-8;
		if (!f.isFinite() || !f.isDefined()) {
			f = curve.evaluateCurve3D(u + d);
			if (f.isSingular()) {
				f = curve.evaluateCurve3D(u - d);
			}
		}
		m.precalcVertices.put(u, f);
		return f;
	}
	
	Coords getVertex(final int i) {
		if(i == 0 && alt != null)
			return alt;
		return vertex;
	}
	
	Coords getDerivative(final int i) {
		if(i == 0 && alt != null)
			return altDer;
		return deriv;
	}
	
	double getParam(final int i) {
		if(i == 0 && alt != null)
			return altParam;
		return param;
	}

	/**
	 * Calculates an axis-aligned bounding box based on the three vertices.
	 */
	private void setBoundingBox() {
		final Coords v1 = ((CurveSegment)parents[0]).getVertex(0);
		final Coords v2 = vertex;
		final Coords v3 = ((CurveSegment)parents[1]).getVertex(1);

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
		CurveSegment p0 = (CurveSegment)parents[0];
		CurveSegment p1 = (CurveSegment)parents[1];
		final double p0p = p0.getParam(0);
		final double p1p = p1.getParam(1);
		final Coords p0v = p0.getVertex(0);
		final Coords p1v = p1.getVertex(1);
		
		// use Heron's formula twice:
		final Coords v0 = calcVertex(0.5*(p0p+param));
		final Coords v1 = calcVertex(0.5*(param+p1p));
		final Coords v2 = p1v.add(p0v).mul(0.5);
		
		double a = v2.distance(p0v);
		double b = v0.distance(p0v);
		double c = v2.distance(v0);

		double s = 0.5 * (a + b + c);
		error = Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		a = p1v.distance(v2);
		b = v1.distance(v2);
		c = p1v.distance(v1);

		s = 0.5 * (a + b + c);
		error += Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		a = v2.distance(vertex);
		b = v0.distance(v2);
		c = vertex.distance(v0);
		
		s = 0.5 * (a + b + c);
		error += Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		a = v2.distance(vertex);
		b = v1.distance(v2);
		c = vertex.distance(v1);
		
		s = 0.5 * (a + b + c);
		error += Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		if (error==0) {
			// the error should only be zero if the vertices are in line - verify this
			if(Math.abs(p1v.sub(p0v).normalized().dotproduct(vertex.sub(p0v).normalized())) < 0.99) {
				//otherwise use longest distance
				error = a > b ? a > c ? a : c : b > c ? b : c;
			}
		}

		// alternative error measure for singular segments
		if (isSingular) {
			if(p0v.isDefined() || vertex.isDefined() || p1v.isDefined())
				error = CurveMesh.undefErrorConst * length;
			else
				error = 0;
		}
		else if (Double.isNaN(error)) {
			//shouldn't happen
			error = (p1p - p0p)*0.75;
			error = error*error;
		}
	}
	
	/**
	 * Approximates the tangent by a simple forward difference quotient. Should
	 * only be called in the constructor.
	 */
	private Coords approxDeriv(double param, Coords v) {
		
		//forwards difference quotient 
		Coords d = calcVertex(param + CurveMesh.deltaParam);
		d = d.sub(v).mul(CurveMesh.invDeltaParam);
		
		if(!d.isDefined()) {
			//backwards difference quotient
			d = calcVertex(param - CurveMesh.deltaParam);
			d = v.sub(d).mul(CurveMesh.invDeltaParam);
		}
		
		return d;
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
		children[0] = new CurveSegment((CurveMesh) mesh, level + 1, (CurveSegment)parents[0], this, lastVersion);
		children[1] = new CurveSegment((CurveMesh) mesh, level + 1, this, (CurveSegment)parents[1], lastVersion);
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
		
		if(parents[0] != null)
			parents[0].recalculate(currentVersion, false);
		if(parents[1] != null)
			parents[1].recalculate(currentVersion, false);
		
		if (lastVersion == currentVersion)
			return false;

		lastVersion = currentVersion;
		
		if(level >= 0) {
			updateInDrawList = true;
			init();
		} else {
			vertex = calcVertex(param);
			deriv = approxDeriv(param, vertex);
		}

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

		TriListElem lm = add(s, s.cullInfo != CullInfo2.OUT);

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
					TriListElem lm = add(s, s.cullInfo != CullInfo2.OUT);
					s.triListElem = lm;
					lm.setOwner(s);
				} else {
					CullInfo2 c = s.cullInfo;
					s.cullInfo = CullInfo2.ALLIN;
					TriListElem lm = add(s, s.cullInfo != CullInfo2.OUT);
					s.triListElem = lm;
					lm.setOwner(s);
					s.cullInfo = c;
				}
				s.reinsertInQueue();
			}
		}
	}
	
	public TriListElem add(CurveSegment s, boolean visible) {
		final CurveSegment p0 = (CurveSegment)s.parents[0];
		final CurveSegment p1 = (CurveSegment)s.parents[1];
		return add(p0.getVertex(0), p1.getVertex(1), p0.getDerivative(0), p1.getDerivative(1), visible);
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
	public static double deltaParam = 1e-10;
	public static double invDeltaParam = 1/deltaParam;

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
	CurveEvaluable3D curve;
	
	HashMap<Double, Coords> precalcVertices = new HashMap<Double, Coords>();

	/**
	 * @param curve
	 *            The curve to render
	 * @param cullingBox
	 *            Axis-aligned box to cull segments against
	 * @param scale
	 *            How zoomed out things are - used to set width
	 */
	public CurveMesh(CurveEvaluable3D curve, double[] cullingBox, float scale) {
		super(new FastBucketPQ(new CurveSplitBucketAssigner(), true),
				new FastBucketPQ(new CurveSplitBucketAssigner(), false),
				new CurveMeshTriList(100, 0, scale * scalingFactor), 2, 2,
				maxLevel);

		setCullingBox(cullingBox);

		this.curve = curve;

		initCurve();
	}

	/**
	 * generates the first few segments
	 */
	private void initCurve() {
		double min = curve.getMinParameter();
		double max = curve.getMaxParameter();
		CurveSegment a0 = new CurveSegment(this, -1, min, currentVersion);
		CurveSegment a1 = new CurveSegment(this, -1, max, currentVersion);
		root = new CurveSegment(this, 0, a0, a1, currentVersion);

		root.updateCullInfo();

		splitQueue.add(root);
		drawList.add(root);
		
		setLevelOfDetail(10);

		// split the first few elements in order to avoid problems
		// with periodic funtions
		for (int i = 0; i < 2; i++) {
			if(i==100)
				System.err.print("");
			split(splitQueue.forcePoll());
		}
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
		maxErrorCoeff = 1 / (Math.pow(10, 1.5 + l * 0.15));
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
		if (splitQueue.peek().getError() > desiredMaxError)
			return Side.SPLIT;
		else if (mergeQueue.peek() != null && mergeQueue.peek().getError() < desiredMaxError)
			return Side.MERGE;
		return Side.NONE;
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

	@Override
	public void updateParameters() {
		precalcVertices.clear();
		super.updateParameters();
	}
}
