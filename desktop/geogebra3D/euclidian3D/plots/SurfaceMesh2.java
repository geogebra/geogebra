package geogebra3D.euclidian3D.plots;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLevelOfDetail;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;
import geogebra3D.euclidian3D.BucketAssigner;
import geogebra3D.euclidian3D.Octree;
import geogebra3D.euclidian3D.OctreeCollection;
import geogebra3D.euclidian3D.TriList;
import geogebra3D.euclidian3D.TriListElem;
import geogebra3D.euclidian3D.TriangleOctree;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * An element in a CurveMesh.
 * 
 * @author André Eriksson
 */
class SurfaceDiamond2 extends DynamicMeshElement2 {
	// MISC
	/** error measure */
	double[] errors = new double[2];
	/** the area of the diamond (parameter space) */
	private double area;
	/** the triangles associated with the diamond */
	private TriListElem[] triangles = new TriListElem[2];

	// VERTEX
	/** the parameters values of the vertex */
	double[] params = new double[2];
	/** vertex position */
	Coords vertex;
	/** vertex normal */
	private Coords normal;

	Coords alt = null;
	
	double[] ancestorDiff;
	double[] originalParams;

	// OTHER DIAMONDS
	/** the other two corners */
	SurfaceDiamond2[] ancestors = new SurfaceDiamond2[2];
	/** the index of this diamond within each of its parents */
	int[] indices = new int[2];

	/**
	 * Constructor used when bootstrapping
	 * 
	 * @param mesh
	 *            The mesh this element belongs to
	 * @param level
	 *            The depth this element has in the tree
	 * @param pa1
	 *            First parameter value
	 * @param pa2
	 *            Second parameter value
	 * @param isClipped
	 *            Whether or not the diamond is clipped
	 * @param version
	 *            The current version of the mesh - changes when the function
	 *            changes
	 */
	public SurfaceDiamond2(SurfaceMesh2 mesh, int level, double pa1,
			double pa2, boolean isClipped, int version) {
		super(mesh, level, isClipped, version);

		params[0] = pa1;
		params[1] = pa2;
		vertex = calcVertex(pa1, pa2);
		normal = approxNormal(pa1, pa2);
	}

	/**
	 * Constructor used when refining
	 * 
	 * @param mesh
	 *            The mesh this element belongs to
	 * @param parent0
	 *            First parent
	 * @param index0
	 *            Index of this diamond among first parent's children
	 * @param parent1
	 *            Second parent
	 * @param index1
	 *            Index of this diamond among second parent's children
	 * @param ancestor0
	 *            First ancestor
	 * @param ancestor1
	 *            Second ancestor
	 * @param level
	 *            The depth this element has in the tree
	 * @param version
	 *            The current version of the mesh - changes when the function
	 *            changes
	 */
	SurfaceDiamond2(SurfaceMesh2 mesh, SurfaceDiamond2 parent0, int index0,
			SurfaceDiamond2 parent1, int index1, SurfaceDiamond2 ancestor0,
			SurfaceDiamond2 ancestor1, int level, int version) {
		super(mesh, level, ancestor0.ignoreFlag
				|| (parent0.ignoreFlag && parent1.ignoreFlag), version);

		parents[0] = parent0;
		parents[1] = parent1;
		indices[0] = index0;
		indices[1] = index1;
		ancestors[0] = ancestor0;
		ancestors[1] = ancestor1;
		params[0] = (ancestor0.params[0] + ancestor1.params[0]) * 0.5;
		params[1] = (ancestor0.params[1] + ancestor1.params[1]) * 0.5;
		calcMainVertex(params[0], params[1]);
		normal = approxNormal(params[0], params[1]);

		init();
	}

	/**
	 * Performs some necessary initialization tasks
	 */
	private void init() {
		setBoundingBox();
		setArea();
		generateError();
		// checkSingular();
	}

	/**
	 * Approximates the element normal at a specific point by means of a simple
	 * forward difference quotient.
	 * 
	 * @param u
	 *            Value of first parameter
	 * @param v
	 *            Value of second parameter
	 * @return An approximation of the normal
	 */
	private Coords approxNormal(double u, double v) {
		Coords dx = calcVertex(u + SurfaceMesh2.normalDelta, v);
		Coords dy = calcVertex(u, v + SurfaceMesh2.normalDelta);
		return dx.sub(vertex).crossProduct(dy.sub(vertex)).normalized();
	}

	void calcMainVertex(double u, double v) {
		final SurfaceEvaluable function = ((SurfaceMesh2) mesh).getFunction();
		Coords f = function.evaluatePoint(u, v);
		final SurfaceDiamond2 a0 = ancestors[0];
		final SurfaceDiamond2 a1 = ancestors[1];
		final boolean v2def = a0.getVertex(this).isDefined();
		final boolean v3def = a1.getVertex(this).isDefined();

		if (v2def != v3def) {
			// perform binary search for edge
			double ui = u;
			double vi = v;
			double du = (a1.params[0] - a0.params[0]) * 0.25;
			double dv = (a1.params[1] - a0.params[1]) * 0.25;
			final boolean dir = v2def;
			Coords lo = a0.getVertex(this);
			Coords hi = a1.getVertex(this);
			if (dir ^ f.isDefined()) {
				hi = f;
				ui -= du;
				vi -= dv;
			} else {
				lo = f;
				ui += du;
				vi += dv;
			}

			f = function.evaluatePoint(ui, vi);
			for (int i = 0; i < 30; i++) {
				du *= 0.5;
				dv *= 0.5;
				if (dir ^ f.isDefined()) {
					hi = f;
					ui -= du;
					vi -= dv;
				} else {
					lo = f;
					ui += du;
					vi += dv;
				}
				f = function.evaluatePoint(ui, vi);
			}
			alt = hi;
			f = lo;
			ancestorDiff = new double[] {a1.params[0]-a0.params[0], a1.params[1]-a0.params[1]};
			params[0] = ui;
			params[1] = vi;
			originalParams = new double[] {u, v};
		} else {

			// if infinite, attempt to move in some direction
			float d = 1e-6f;
			if (!f.isFinite() || !f.isDefined()) {
				f = function.evaluatePoint(u + d, v);
				if (!f.isFinite() || !f.isDefined()) {
					f = function.evaluatePoint(u, v + d);
					if (!f.isFinite() || !f.isDefined()) {
						f = function.evaluatePoint(u - d, v);
						if (!f.isFinite() || !f.isDefined())
							f = function.evaluatePoint(u, v - d);
					}
				}
			}
		}
		vertex = f;
	}

	/**
	 * Attempts to evaluate the function at a specific point. Tries nearby
	 * points if the specific point is singular or undefined.
	 * 
	 * @param u
	 *            Value of first parameter
	 * @param v
	 *            Value of second parameter
	 * @return
	 */
	private Coords calcVertex(double u, double v) {
		final SurfaceEvaluable function = ((SurfaceMesh2) mesh).getFunction();
		Coords f = function.evaluatePoint(u, v);

		// if infinite, attempt to move in some direction
		float d = 1e-6f;
		if (!f.isFinite() || !f.isDefined()) {
			f = function.evaluatePoint(u + d, v);
			if (!f.isFinite() || !f.isDefined()) {
				f = function.evaluatePoint(u, v + d);
				if (!f.isFinite() || !f.isDefined()) {
					f = function.evaluatePoint(u - d, v);
					if (!f.isFinite() || !f.isDefined())
						f = function.evaluatePoint(u, v - d);
				}
			}
		}
		return f;
	}

	/**
	 * @param i
	 *            index of a child
	 * @return false if child number i is null, otherwise true
	 */
	public boolean childCreated(int i) {
		return children[i] != null;
	}

	@Override
	protected void createChild(int i) {

		SurfaceDiamond2 parent = null;
		SurfaceDiamond2 otherParent = null;

		int index;
		if (i < 2) {
			parent = (SurfaceDiamond2) parents[0];
			if (i == 0)
				index = indices[0] + 1;
			else
				index = indices[0] - 1;
		} else {
			parent = (SurfaceDiamond2) parents[1];
			if (i == 2)
				index = indices[1] + 1;
			else
				index = indices[1] - 1;
		}

		if (parent != null)
			otherParent = (SurfaceDiamond2) parent.getChild(index & 3);

		int parentIndex = i / 2;
		int ancestorIndex = i == 1 || i == 2 ? 1 : 0;
		SurfaceDiamond2 a0 = (SurfaceDiamond2) parents[parentIndex];
		SurfaceDiamond2 a1 = ancestors[ancestorIndex];

		int otherIndex = i == 0 || i == 2 ? 1 : 0;
		if (otherParent != null && otherParent.parents[1] == parent)
			otherIndex |= 2;
		if (i == 1 || i == 3)
			children[i] = new SurfaceDiamond2((SurfaceMesh2) mesh, otherParent,
					otherIndex, this, i, a0, a1, level + 1, lastVersion);
		else
			children[i] = new SurfaceDiamond2((SurfaceMesh2) mesh, this, i,
					otherParent, otherIndex, a0, a1, level + 1, lastVersion);

		if (otherParent != null)
			(otherParent).setChild(otherIndex, children[i]);
	}

	@Override
	protected void cullChildren() {
		if (!isSplit()) {
			return;
		}

		// cull quadtree children
		for (int i = 0; i < 4; i += 2) {
			SurfaceDiamond2 child = (SurfaceDiamond2) getChild(i);
			if (child != null) {
				if (this == child.getParent(0)) {
					if (child.childCreated(0))
						child.getChild(0).updateCullInfo();
					if (child.childCreated(1))
						child.getChild(1).updateCullInfo();
				} else {
					if (child.childCreated(2))
						child.getChild(2).updateCullInfo();
					if (child.childCreated(3))
						child.getChild(3).updateCullInfo();
				}
			}
		}
	}

	/**
	 * Freed the j'th triangle
	 * 
	 * @param j
	 *            triangle index < 2
	 */
	public void freeTriangle(int j) {
		triangles[j] = null;
	}

	/**
	 * Computes the error for the diamond by means of a volume deviation
	 * approximation. If this fails because some point is singular - multiplies
	 * parameter area by a constant.
	 */
	void generateError() {
        Coords p0 = ((SurfaceDiamond2) parents[0]).getVertex(this);
        Coords p1 = ((SurfaceDiamond2) parents[1]).getVertex(this);
        Coords a0 = (ancestors[0]).getVertex(this);
        Coords a1 = (ancestors[1]).getVertex(this);

        Coords v0 = a0.sub(vertex);
        Coords v1 = a1.sub(vertex);
        Coords v2 = p0.sub(vertex);
        Coords v3 = p1.sub(vertex);

        double vol0 = Math.abs(v0.dotproduct(v3.crossProduct(v1)));
        double vol1 = Math.abs(v0.dotproduct(v2.crossProduct(v1)));

        if (vol0 == 0.0 && vol1 == 0.0) {
                // rotate
                vol0 = Math.abs(v2.dotproduct(v3.crossProduct(v1)));
                vol1 = Math.abs(v2.dotproduct(v0.crossProduct(v3)));
        }

        if (Double.isNaN(vol0) || Double.isInfinite(vol0))
                // use a different error measure for infinite points
                // namely the base area times some constant
                errors[0] = area * area * SurfaceMesh2.undefErrorConst;
        else
                errors[0] = vol0;
        if (Double.isNaN(vol1) || Double.isInfinite(vol1))
                errors[1] = area * area * SurfaceMesh2.undefErrorConst;
        else
                errors[1] = vol1;

        if (errors[0] == 0.0 || errors[1] == 0.0) {
                // sample a random point to see if we're flat
                final double alpha = 0.123456;
                double nu = alpha * params[0] + (1 - alpha)
                                * ancestors[0].params[0];
                double nv = alpha * params[1] + (1 - alpha)
                                * ancestors[0].params[1];
                nu = alpha * nu + (1 - alpha)
                                * ((SurfaceDiamond2) parents[0]).params[0];
                nv = alpha * nv + (1 - alpha)
                                * ((SurfaceDiamond2) parents[1]).params[1];
                Coords pt = calcVertex(nu, nv);
                if (pt.sub(vertex).dotproduct(a0.sub(pt)) < 0.99)
                        errors[0] = errors[1] = area * 0.1;
        }

        int fac = 0;
        if (!p0.isDefined())
                fac++;
        if (!p1.isDefined())
                fac++;
        if (!a0.isDefined())
                fac++;
        if (!a1.isDefined())
                fac++;
        if (fac == 4)
                errors[0] = errors[1] = 0;
        else if (fac > 2) {
                errors[0] *= 2.0;
                errors[1] *= 2.0;
        }
	}
	
	/**
	 * @return the area of the diamond
	 */
	public double getArea() {
		return area;
	}

	@Override
	protected double getError() {
		return Math.max(errors[0], errors[1]);
	}

	/**
	 * @return the surface normal at the center of the diamond
	 */
	public Coords getNormal() {
		return normal;
	}

	/**
	 * Given one parent, returns the other
	 */
	private DynamicMeshElement2 getOtherParent(DynamicMeshElement2 p) {
		if (p == parents[0])
			return parents[1];
		return parents[0];
	}

	/**
	 * @param j
	 *            triangle index < 2
	 * @return triangle number j
	 */
	public TriListElem getTriangle(int j) {
		return triangles[j];
	}

	/**
	 * @return the middle vertex of the diamond
	 */
//	public Coords getVertex() {
//		return vertex;
//	}

	public Coords getVertex(SurfaceDiamond2 o) {
		if (alt == null)
			return vertex;
		
		if(o.vertex == null) {
			//check dot product for side
			final double c = (o.params[0] - params[0]) * ancestorDiff[0] + (o.params[1] - params[1]) * ancestorDiff[1];
			return c < 0 ? vertex : alt;			
		}
		
		if(o.alt != null || o.vertex.isDefined())
			return alt.isDefined() ? alt : vertex;
		return alt.isDefined() ? vertex : alt;
	}

	/**
	 * Only move to merge if neither parent is split
	 */
	@Override
	public boolean readyForMerge(DynamicMeshElement2 activeParent) {
		return !getOtherParent(activeParent).isSplit();
	}

	@Override
	public boolean recalculate(int currentVersion, boolean recurse) {

		if (lastVersion == currentVersion)
			return false;

		lastVersion = currentVersion;
		updateInDrawList = true;

		// we need to reevalutate the vertices, normals, error and culling

		// make sure ancestors are updated
		parents[0].recalculate(currentVersion, false);
		parents[1].recalculate(currentVersion, false);
		ancestors[0].recalculate(currentVersion, false);
		ancestors[1].recalculate(currentVersion, false);

		calcMainVertex(params[0], params[1]);

		normal = approxNormal(params[0], params[1]);

		setBoundingBox();
		setArea();
		generateError();

		return true;
	}

	@Override
	protected void reinsertInQueue() {
		if (bucket_owner != null) {
			FastBucketPQ list = bucket_owner;
			list.remove(this);
			list.add(this);
		}
	}

	/**
	 * sets area to the base area of the diamond (parameter wise)
	 */
	public void setArea() {
		if (ancestors[0].params[0] - params[0] != 0)
			area = Math.abs((ancestors[0].params[0] - params[0])
					* (((SurfaceDiamond2) parents[0]).params[1] - params[1]));
		else
			area = Math
					.abs((((SurfaceDiamond2) parents[1]).params[0] - params[0])
							* (ancestors[0].params[1] - params[1]));
	}

	/**
	 * Computes an axis-aligned bounding box for the diamond by considering all
	 * five vertices.
	 */
	void setBoundingBox() {
		final Coords v1 = ancestors[0].getVertex(this);
		final Coords v2 = ancestors[1].getVertex(this);
		final Coords v3 = ((SurfaceDiamond2) parents[0]).getVertex(this);
		final Coords v4 = ((SurfaceDiamond2) parents[1]).getVertex(this);
		final Coords v5 = vertex;

		double x0, x1, y0, y1, z0, z1, x, y, z;
		final double[] xs = { v2.getX(), v3.getX(), v4.getX(), v5.getX() };
		final double[] ys = { v2.getY(), v3.getY(), v4.getY(), v5.getY() };
		final double[] zs = { v2.getZ(), v3.getZ(), v4.getZ(), v5.getZ() };

		x0 = x1 = v1.getX();
		y0 = y1 = v1.getY();
		z0 = z1 = v1.getZ();

		for (int i = 0; i < 4; i++) {
			x = xs[i];
			y = ys[i];
			z = zs[i];

			if (Double.isNaN(x) || Double.isInfinite(x)) {
				x0 = Double.NEGATIVE_INFINITY;
				x1 = Double.POSITIVE_INFINITY;
			} else {
				if (x0 > x)
					x0 = x;
				if (x1 < x)
					x1 = x;
			}
			if (Double.isNaN(y) || Double.isInfinite(y)) {
				y0 = Double.NEGATIVE_INFINITY;
				y1 = Double.POSITIVE_INFINITY;
			} else {
				if (y0 > y)
					y0 = y;
				if (y1 < y)
					y1 = y;
			}
			if (Double.isNaN(z) || Double.isInfinite(z)) {
				z0 = Double.NEGATIVE_INFINITY;
				z1 = Double.POSITIVE_INFINITY;
			} else {
				if (z0 > z)
					z0 = z;
				if (z1 < z)
					z1 = z;
			}
		}
		boundingBox = new double[] { x0, x1, y0, y1, z0, z1 };
	}

	/**
	 * Sets one of the children of the diamond
	 * 
	 * @param i
	 *            index of the child
	 * @param e
	 *            the element to set it to
	 */
	void setChild(int i, DynamicMeshElement2 e) {
		children[i] = e;
	}

	@Override
	protected void setHidden(boolean hide) {
		SurfaceTriList2 t = (SurfaceTriList2) mesh.drawList;

		if (hide) {
			t.hide(this, 0);
			t.hide(this, 1);
		} else {
			t.show(this, 0);
			t.show(this, 1);
		}
	}

	/**
	 * Sets triangle number j to e.
	 * 
	 * @param j
	 *            index < 2
	 * @param e
	 *            a valid triangle
	 */
	public void setTriangle(int j, TriListElem e) {
		triangles[j] = e;
	}
}

/**
 * A bucket assigner used for merge operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class SurfaceMergeBucketAssigner2 implements
		BucketAssigner<DynamicMeshElement2> {

	public int getBucketIndex(Object o, int bucketAmt) {
		SurfaceDiamond2 d = (SurfaceDiamond2) o;
		double e = d.getError();
		int f = (int) (Math.exp(1 - e) * 200);
		int ret = f > bucketAmt - 1 ? bucketAmt - 1 : f;
		if (ret < 0)
			ret = 0;
		return ret;
	}
}

/**
 * Mesh representing a function R^2->R^3
 * 
 * @author André Eriksson
 */
public class SurfaceMesh2 extends DynamicMesh2 implements OctreeCollection {

	private Octree octree;

	// DETAIL SETTINGS
	private double maxErrorCoeff = 1e-2;

	/** x/y difference used when estimating normals */
	public static final double normalDelta = 1e-6;

	/**
	 * scaling constant used for setting the error of diamonds where one or more
	 * vertices are undefined
	 */
	public static final double undefErrorConst = 100;

	/** Current level of detail setting */
	public double levelOfDetail = 0.1;

	/** the maximum level of refinement */
	private static final int maxLevel = 20;

	// PRIVATE VARIABLES
	/** a reference to the function being drawn */
	private SurfaceEvaluable function;

	private SurfaceDiamond2 root;

	/** desired maximum error */
	private double desiredMaxError;

	/**
	 * Creates a mesh for some function.
	 * 
	 * @param function
	 *            The function to be rendered
	 * @param cullingBox
	 *            Box to cull triangles against
	 * @param domain
	 *            Function domain as {x_min, x_max, y_min, y_max}
	 */
	public SurfaceMesh2(SurfaceEvaluable function, double[] cullingBox,
			double[] domain) {
		super(new FastBucketPQ(new SurfaceSplitBucketAssigner2(), true),
				new FastBucketPQ(new SurfaceSplitBucketAssigner2(), false),
				new SurfaceTriList2(100, 0), 2, 4, maxLevel);
		this.function = function;

		setLevelOfDetail(5);

		setCullingBox(cullingBox);

		initMesh(domain[0], domain[1], domain[2], domain[3]);

		splitQueue.add(root);
		drawList.add(root);

		for (int i = 0; i < 100; i++)
			split(splitQueue.forcePoll());
	}

	@Override
	protected String getDebugInfo(long time) {
		return function + ":\tupdate time: " + time + "ms" + "\t triangles: "
				+ drawList.getTriAmt() + "\t max error: "
				+ (float) splitQueue.peek().getError() + "\t error threshold: "
				+ (float) desiredMaxError;
	}

	/**
	 * @return the amount of visible segments
	 */
	public int getVisibleChunks() {
		return drawList.getChunkAmt();
	}

	/**
	 * @return the function being rendered
	 * */
	public SurfaceEvaluable getFunction() {
		return function;
	}

	/**
	 * Bootstraps a fairly complex mesh.
	 * 
	 * @param xMin
	 *            the minimum x coordinate
	 * @param xMax
	 *            the maximum x coordinate
	 * @param yMin
	 *            the minimum y caoordinate
	 * @param yMax
	 *            the maximum y coordinate
	 */
	private void initMesh(double xMin, double xMax, double yMin, double yMax) {
		int di, ix, jx;
		double x, y;
		SurfaceDiamond2 t;

		// base diamonds at level 0
		SurfaceDiamond2[][] base0 = new SurfaceDiamond2[4][4];
		// base diamonds at lower levels
		SurfaceDiamond2[][] base1 = new SurfaceDiamond2[4][4];

		double dx = (xMax - xMin);
		double dy = (yMax - yMin);

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				x = xMin + (i - 0.5) * dx;
				y = yMin + (j - 0.5) * dy;
				base0[j][i] = new SurfaceDiamond2(this, 0, x, y,
						!(i == 1 && j == 1), currentVersion);

				x = xMin + (i - 1) * dx;
				y = yMin + (j - 1) * dy;
				base1[j][i] = t = new SurfaceDiamond2(this,
						((i ^ j) & 1) != 0 ? -1 : -2, x, y, false,
						currentVersion);
				t.setSplit(true);
			}

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {

				t = base0[j][i];
				di = ((i ^ j) & 1) != 0 ? 1 : -1;
				ix = ((2 * i + 1 - di) >> 1) % 4;
				jx = (2 * j >> 1) % 4;
				t.parents[0] = base1[jx][ix];
				ix = ((2 * i + 1 + di) >> 1) % 4;
				jx = (2 * (j + 1) >> 1) % 4;
				t.parents[1] = base1[jx][ix];
				ix = (2 * i >> 1) % 4;
				jx = ((2 * j + 1 + di) >> 1) % 4;
				t.ancestors[0] = base1[jx][ix];
				ix = ((2 * (i + 1)) >> 1) % 4;
				jx = ((2 * j + 1 - di) >> 1) % 4;
				t.ancestors[1] = base1[jx][ix];

				ix = (di < 0 ? 0 : 3);
				((SurfaceDiamond2) t.parents[0]).setChild(ix, t);
				t.indices[0] = ix;
				ix = (di < 0 ? 2 : 1);
				((SurfaceDiamond2) t.parents[1]).setChild(ix, t);
				t.indices[1] = ix;
			}
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				t = base1[j][i];
				t.ancestors[1] = base1[(j + 3) % 4][i];
				t.ancestors[0] = base1[(j + 1) % 4][i];
				t.parents[0] = base1[j][(i + 3) % 4];
				t.parents[1] = base1[j][(i + 1) % 4];
			}
		root = base0[1][1];
		root.calcMainVertex(root.params[0], root.params[1]);
		root.setBoundingBox();
		root.setArea();
		root.generateError();
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
		if (((GeoElement) function).hasLevelOfDetail())
			setLevelOfDetail(((GeoLevelOfDetail) function)
					.getLevelOfDetail().getValue());
		desiredMaxError = maxErrorCoeff * maxWidth;
		noUpdate = false;
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
		//maxErrorCoeff = 1 / (Math.pow(10, 1.6 + l * 0.15));
		//maxErrorCoeff = 1 / (Math.pow(10, 3.7 + (l-5) * 0.4));
		maxErrorCoeff = 1 / (Math.pow(10, 1.7 + l * 0.4));
		
		
	}

	/**
	 * 
	 * @return current level of detail - typically in [0,1]
	 */
	public double getLevelOfDetail() {
		return levelOfDetail;
	}

	@Override
	protected Side tooCoarse() {
		if (splitQueue.peek().getError() > desiredMaxError)
			return Side.SPLIT;
		else if (mergeQueue.peek().getError() < desiredMaxError)
			return Side.MERGE;
		return Side.NONE;
	}

	@Override
	protected void updateCullingInfo() {
		root.updateCullInfo();

		if (root.childCreated(0))
			root.getChild(0).updateCullInfo();
		if (root.childCreated(1))
			root.getChild(1).updateCullInfo();
		if (root.childCreated(2))
			root.getChild(2).updateCullInfo();
		if (root.childCreated(3))
			root.getChild(3).updateCullInfo();
	}

	public Octree getObjectOctree() {
		if (octree == null) {
			octree = new TriangleOctree();
			// insert all elements into octree
			int n = drawList.getTriAmt();
			FloatBuffer buf = drawList.getTriangleBuffer();
			float[] temp = new float[9];
			for (int i = 0; i < n; i++) {
				buf.get(temp);
				try {
					octree.insertTriangle(temp);
				} catch (Exception e) {
					System.err.println(e);
					return null;
				}
			}
		}
		return octree;
	}

	public Octree getVisibleTriangleOctree() {
		return getObjectOctree();
	}
}

/**
 * A bucket assigner used for split operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class SurfaceSplitBucketAssigner2 implements
		BucketAssigner<DynamicMeshElement2> {

	public int getBucketIndex(Object o, int bucketAmt) {
		SurfaceDiamond2 d = (SurfaceDiamond2) o;
		int bucket;
		double e = d.getError();
		int f = (int) (Math.exp(e + 1) * 200) + 3;
		int alt = (int) (Math.log(1 + Math.log(1 + Math.log(1 + 10000 * e))) * 1000);
		f = alt;
		if (e == 0.0)
			bucket = 1;
		bucket = f > bucketAmt - 1 || f < 0 ? bucketAmt - 1 : f;
		return bucket;
	}
}

/**
 * Triangle list used for curves
 * 
 * @author André Eriksson
 */
class SurfaceTriList2 extends TriList implements DynamicMeshTriList2 {

	private int currentVersion;

	static final private double asymptoteThreshold = 0;

	// FLAGS
	/** the element contains at least one singular vertex - do not draw */
	static final char FLAGS_SINGULAR = 0x1;
	/** the element contains an asymptote - do not draw */
	static final char FLAGS_ASYMPTOTE = FLAGS_SINGULAR << 1;

	static final char MASK_IGNORE = FLAGS_SINGULAR | FLAGS_ASYMPTOTE;

	/**
	 * @param capacity
	 *            the goal amount of triangles available
	 * @param marigin
	 *            extra triangle amount
	 */
	SurfaceTriList2(int capacity, int marigin) {
		super(capacity, marigin, 9, true);
	}

	public void add(DynamicMeshElement2 e) {
		add(e, 0);
		add(e, 1);
	}

	/**
	 * Adds a triangle to the list.
	 * 
	 * @param e
	 *            The parent diamond of the triangle
	 * @param j
	 *            The index of the triangle within the diamond
	 */
	public void add(DynamicMeshElement2 e, int j) {
		SurfaceDiamond2 s = (SurfaceDiamond2) e;

		// handle clipping
		if (s.ignoreFlag || ((SurfaceDiamond2) s.parents[j]).ignoreFlag)
			return;

		float[] v = new float[9];
		float[] n = new float[9];

		calcFloats(s, j, v, n);
		char flags = getFlags(v, n);

		boolean ignore = (flags & MASK_IGNORE) != 0;

		TriListElem lm = ignore ? new TriListElem(flags) : add(v, n);

		lm.setOwner(s);
		s.setTriangle(j, lm);

		if (!ignore && e.cullInfo == CullInfo2.OUT) {
			hide(s, j);
		}
	}

	private char getFlags(float[] v, float[] n) {

		char ret = 0;

		if (Double.isNaN(v[0]) || Double.isInfinite(v[0]) || Double.isNaN(v[1])
				|| Double.isInfinite(v[1]) || Double.isNaN(v[2])
				|| Double.isInfinite(v[2]) || Double.isNaN(v[3])
				|| Double.isInfinite(v[3]) || Double.isNaN(v[4])
				|| Double.isInfinite(v[4]) || Double.isNaN(v[5])
				|| Double.isInfinite(v[5]) || Double.isNaN(v[6])
				|| Double.isInfinite(v[6]) || Double.isNaN(v[7])
				|| Double.isInfinite(v[7]) || Double.isNaN(v[8])
				|| Double.isInfinite(v[8])) {
			ret |= FLAGS_SINGULAR;
		}

		double x1 = v[3] - v[0];
		double y1 = v[4] - v[1];
		double z1 = v[5] - v[2];
		double x2 = v[6] - v[0];
		double y2 = v[7] - v[1];
		double z2 = v[8] - v[2];

		// triangle normal
		double nx = y1 * z2 - y2 * z1;
		double ny = x2 * z1 - x1 * z2;
		double nz = x1 * y2 - x2 * y1;

		if (true // Math.abs(v[2]) > 4.0 && Math.abs(v[5]) > 4.0 &&
					// Math.abs(v[8]) > 4.0
				&& nx * n[0] + ny * n[1] + nz * n[2] < asymptoteThreshold
				&& nx * n[3] + ny * n[4] + nz * n[5] < asymptoteThreshold
				&& nx * n[6] + ny * n[7] + nz * n[8] < asymptoteThreshold) {
			ret |= FLAGS_ASYMPTOTE;
		}

		return ret;
	}

	private void calcFloats(SurfaceDiamond2 d, int j, float[] v, float[] n) {
		SurfaceDiamond2 t[] = new SurfaceDiamond2[3];
		t[1] = (SurfaceDiamond2) d.getParent(j);
		if (j == 0) {
			t[0] = d.ancestors[0];
			t[2] = d.ancestors[1];
		} else {
			t[0] = d.ancestors[1];
			t[2] = d.ancestors[0];
		}
		for (int i = 0, c = 0; i < 3; i++, c += 3) {
			Coords vertex = t[i].getVertex(d);
			Coords normal = t[i].getNormal();
			v[c] = (float) vertex.getX();
			v[c + 1] = (float) vertex.getY();
			v[c + 2] = (float) vertex.getZ();
//			if (Double.isNaN(v[c + 2]))
//				v[c + 2] = 0;
			n[c] = (float) normal.getX();
			n[c + 1] = (float) normal.getY();
			n[c + 2] = (float) normal.getZ();
			if (Double.isNaN(n[c]) || Double.isNaN(n[c + 1])
					|| Double.isNaN(n[c + 2])) {
				n[c] = n[c + 1] = 0;
				n[c + 2] = 1;
			}
		}
	}

	public boolean hide(DynamicMeshElement2 t) {
		throw new UnsupportedOperationException();
	}

	/**
	 * removes a triangle from the list, but does not erase it
	 * 
	 * @param d
	 *            the diamond
	 * @param j
	 *            the triangle index
	 * @return true if successful, otherwise false
	 */
	public boolean hide(SurfaceDiamond2 d, int j) {
		TriListElem t = d.getTriangle(j);
		return (t != null && (t.flags & MASK_IGNORE) != 0) || hide(t);
	}

	public boolean show(DynamicMeshElement2 t) {
		throw new UnsupportedOperationException();
	}

	/**
	 * shows a triangle that has been hidden
	 * 
	 * @param e
	 *            the diamond
	 * @param j
	 *            the index of the triangle
	 * @return true if successful, otherwise false
	 */
	public boolean show(DynamicMeshElement2 e, int j) {
		SurfaceDiamond2 d = (SurfaceDiamond2) e;

		reinsert(d, currentVersion);

		TriListElem t = d.getTriangle(j);
		return (t != null && (t.flags & MASK_IGNORE) != 0) || show(t);
	}

	/**
	 * Recalculates and reinserts an element into the list.
	 * 
	 * @param a
	 *            the element to reinsert
	 */
	public void reinsert(DynamicMeshElement2 a, int version) {
		SurfaceDiamond2 s = (SurfaceDiamond2) a;
		s.recalculate(version, true);

		if (s.updateInDrawList) {
			s.updateInDrawList = false;
			TriListElem e0 = s.getTriangle(0);
			TriListElem e1 = s.getTriangle(1);
			if (e0 != null) {
				float[] v0 = new float[9];
				float[] n0 = new float[9];
				calcFloats(s, 0, v0, n0);
				char flags = getFlags(v0, n0);
				// TODO: handle flags
				if (e0.getIndex() != -1) {
					setVertices(e0, v0);
					setNormals(e0, n0);
				} else {
					e0.pushVertices(v0);
					e0.pushNormals(n0);
				}
			}
			if (e1 != null) {
				float[] v1 = new float[9];
				float[] n1 = new float[9];
				calcFloats(s, 1, v1, n1);
				char flags = getFlags(v1, n1);
				// TODO: handle flags
				if (e1.getIndex() != -1) {
					setVertices(e1, v1);
					setNormals(e1, n1);
				} else {
					e1.pushVertices(v1);
					e1.pushNormals(n1);
				}
			}

			s.reinsertInQueue();
		}
	}

	public boolean remove(DynamicMeshElement2 e) {
		boolean b = false;
		b |= remove(e, 0);
		b |= remove(e, 1);
		return b;
	}

	/**
	 * Removes a segment if it is part of the function.
	 * 
	 * @param e
	 *            the segment to remove
	 * @return true if the segment was removed, false if it wasn't in the
	 *         function in the first place
	 */

	public boolean remove(DynamicMeshElement2 e, int j) {
		SurfaceDiamond2 d = (SurfaceDiamond2) e;

		// handle clipping
		if (d.ignoreFlag || ((SurfaceDiamond2) d.parents[j]).ignoreFlag)
			return false;

		boolean ret = hide(d, j);

		// free triangle
		d.freeTriangle(j);
		return ret;
	}

	public void recalculate(int version) {
		this.currentVersion = version;
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
