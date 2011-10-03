//package geogebra3D.euclidian3D.plots;
//
//import geogebra.kernel.GeoFunctionNVar;
//import geogebra3D.euclidian3D.BucketAssigner;
//import geogebra3D.euclidian3D.BucketPQ;
//import geogebra3D.euclidian3D.TriList;
//import geogebra3D.euclidian3D.TriListElem;
//
//import java.nio.FloatBuffer;
//import java.util.Date;
//
///**
// * @author Andr� Eriksson
// */
//public class OldSurfaceMesh {
//
//	// DETAIL SETTINGS
//
//	/**
//	 * used in setRadius() to set the desired error per (visible) area unit
//	 * according to a second degree polynomial with erroCoeffs as coefficients
//	 */
//	private final double[] errorCoeffs = { 0.0015, 0, 0, 0.00012 };
//
//	/**
//	 * a proportionality constant used for setting the error of diamonds where
//	 * one or more vertices are undefined
//	 */
//	public static final double undefErrorConst = 0.001;
//
//	/** the minimum triangle count */
//	private final int minTriCount = 5000;
//
//	/** the desired triangle count */
//	private final int triGoal = 40000;
//
//	/** the maximum triangle count */
//	private final int maxTriangles = 100000;
//
//	/** buffer space for extra triangles */
//	private final int triBufMarigin = 10000;
//
//	/** number of merges/splits per step */
//	private final int stepRefinement = 100;
//
//	/** the maximum level of refinement */
//	private static final int maxLevel = 20;
//
//	/** x/y difference used when estimating normals */
//	public static final double normalDelta = 1e-4;
//
//	// PRIVATE VARIABLES
//
//	/** a reference to the function being drawn */
//	private GeoFunctionNVar function;
//
//	/** the squared radius of the viewing sphere */
//	private double radSq;
//
//	/** references to the priority queues used for splits/merges */
//	private SurfaceMeshBucketPQ splitQueue = new SurfaceMeshBucketPQ(
//			new SplitBucketAssigner());
//	private SurfaceMeshBucketPQ mergeQueue = new SurfaceMeshBucketPQ(
//			new MergeBucketAssigner());
//
//	private SurfaceMeshDiamond baseDiamond;
//
//	private SurfTriList drawList;
//
//	/** desired error per visible area unit */
//	private double desiredErrorPerAreaUnit;
//
//	/** if true error, triangle count and update time is printed each update */
//	private static final boolean printInfo = false;
//
//	/** if false, no updates of the mesh are done */
//	private boolean doUpdates = true;
//	
//	/**
//	 * A bucket assigner used for split operations. Sorts based on
//	 * SurfaceMeshDiamond.error.
//	 * 
//	 * @author Andr� Eriksson
//	 */
//	class SplitBucketAssigner implements BucketAssigner<SurfaceMeshDiamond> {
//
//		public int getBucketIndex(Object o, int bucketAmt) {
//			SurfaceMeshDiamond d = (SurfaceMeshDiamond) o;
//			double e = d.errors[0] + d.errors[1];
//			int f = (int) (Math.exp(e + 1) * 200) + 3;
//			if (e == 0.0)
//				return 1;
//			return f > bucketAmt - 1 || f < 0 ? bucketAmt - 1 : f;
//		}
//	}
//
//	/**
//	 * A bucket assigner used for merge operations. Sorts based on
//	 * SurfaceMeshDiamond.error.
//	 * 
//	 * @author Andr� Eriksson
//	 */
//	class MergeBucketAssigner implements BucketAssigner<SurfaceMeshDiamond> {
//
//		public int getBucketIndex(Object o, int bucketAmt) {
//			SurfaceMeshDiamond d = (SurfaceMeshDiamond) o;
//			double e = d.errors[0] + d.errors[1];
//			int f = (int) (Math.exp(1 - e) * 200);
//			int ret = f > bucketAmt - 1 ? bucketAmt - 1 : f;
//			if (ret < 0)
//				ret = 0;
//			return ret;
//		}
//	}
//
//	/**
//	 * @param function
//	 * @param radSq
//	 * @param unlimitedRange
//	 */
//	public OldSurfaceMesh(GeoFunctionNVar function, double radSq,
//			boolean unlimitedRange) {
//		this.function = function;
//		setRadius(radSq);
//		drawList = new SurfTriList(maxTriangles, triBufMarigin);
//		if (unlimitedRange)
//			initMesh(-radSq, radSq, -radSq, radSq);
//		else
//			initMesh(function.getMinParameter(0), function.getMaxParameter(0),
//					function.getMinParameter(1), function.getMaxParameter(1));
//		baseDiamond.addToSplitQueue();
//	}
//
//	/**
//	 * @param r
//	 *            the bounding radius of the viewing volume
//	 */
//	public void setRadius(double r) {
//		radSq = r * r;
//		desiredErrorPerAreaUnit = errorCoeffs[0] + errorCoeffs[1] * r
//				+ errorCoeffs[2] * radSq + Math.sqrt(r) * errorCoeffs[3];
//	}
//
//	/**
//	 * forces the mesh to resume updates if it has stopped
//	 */
//	public void turnOnUpdates() {
//		doUpdates = true;
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
//		SurfaceMeshDiamond t;
//
//		// base diamonds at level 0
//		SurfaceMeshDiamond[][] base0 = new SurfaceMeshDiamond[4][4];
//		// base diamonds at lower levels
//		SurfaceMeshDiamond[][] base1 = new SurfaceMeshDiamond[4][4];
//
//		double dx = (xMax - xMin);
//		double dy = (yMax - yMin);
//
//		for (int i = 0; i < 4; i++)
//			for (int j = 0; j < 4; j++) {
//				x = xMin + (i - 0.5) * dx;
//				y = yMin + (j - 0.5) * dy;
//				base0[j][i] = new SurfaceMeshDiamond(function, x, y, 0,
//						splitQueue, mergeQueue);
//				if (!(i == 1 && j == 1))
//					base0[j][i].isClipped = true;
//
//				x = xMin + (i - 1) * dx;
//				y = yMin + (j - 1) * dy;
//				base1[j][i] = t = new SurfaceMeshDiamond(function, x, y,
//						((i ^ j) & 1) != 0 ? -1 : -2, splitQueue, mergeQueue);
//				t.setSplit(true);
//			}
//
//		for (int i = 0; i < 4; i++)
//			for (int j = 0; j < 4; j++) {
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
//				ix = (di < 0 ? 0 : 3);
//				t.parents[0].setChild(ix, t);
//				t.indices[0] = ix;
//				ix = (di < 0 ? 2 : 1);
//				t.parents[1].setChild(ix, t);
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
//		baseDiamond = base0[1][1];
//		baseDiamond.setBoundingRadii();
//		baseDiamond.setArea();
//		baseDiamond.setError();
//	}
//
//	private void updateCullingInfo() {
//		baseDiamond.updateCullInfo(radSq, drawList);
//
//		if (baseDiamond.childCreated(0))
//			baseDiamond.getChild(0).updateCullInfo(radSq, drawList);
//		if (baseDiamond.childCreated(1))
//			baseDiamond.getChild(1).updateCullInfo(radSq, drawList);
//		if (baseDiamond.childCreated(2))
//			baseDiamond.getChild(2).updateCullInfo(radSq, drawList);
//		if (baseDiamond.childCreated(3))
//			baseDiamond.getChild(3).updateCullInfo(radSq, drawList);
//	}
//
//	/** used in optimizeSub() */
//	private enum Side {
//		MERGE, SPLIT, NONE
//	};
//
//	/**
//	 * Performs a set number (stepRefinement) of splits/merges
//	 */
//	public void optimize() {
//		if (doUpdates)
//			optimizeSub(stepRefinement);
//	}
//
//	private void optimizeSub(int maxCount) {
//		int count = 0;
//
//		updateCullingInfo();
//
//		long t1 = new Date().getTime();
//
//		Side side = tooCoarse() ? Side.SPLIT : Side.MERGE;
//		while (side != Side.NONE && count < maxCount) {
//			if (side == Side.MERGE) {
//				merge(mergeQueue.poll());
//				if (tooCoarse())
//					side = Side.NONE;
//			} else {
//				split(splitQueue.poll());
//				if (!tooCoarse())
//					side = Side.NONE;
//			}
//			count++;
//		}
//
//		if (count < maxCount) // this only happens if the LoD
//			doUpdates = false; // is at the desired level
//
//		if (printInfo)
//			System.out.println(this.function + ":\tupdate time: "
//					+ (new Date().getTime() - t1) + "ms\ttriangles: "
//					+ drawList.getTriAmt() + "\terror: "
//					+ (float) drawList.getError() + "\tgoal:"
//					+ (float) (desiredErrorPerAreaUnit * drawList.getArea()));
//	}
//
//	/**
//	 * @return true if the mesh should be refined, otherwise false
//	 */
//	private boolean tooCoarse() {
//		double areaGoal = desiredErrorPerAreaUnit * drawList.getArea();
//		double error = drawList.getError();
//		int count = drawList.getTriAmt();
//		if (count < minTriCount)
//			return true;
//		if (drawList.isFull())
//			return false;
//		if (error < areaGoal || count > triGoal)
//			return false;
//		return true;
//	}
//
//	/**
//	 * Recursively splits the target diamond
//	 * 
//	 * @param t
//	 *            the target diamond
//	 */
//	private void split(SurfaceMeshDiamond t) {
//		if (t == null)
//			return;
//
//		// dont split a diamond that has already been split
//		if (t.isSplit()) {
//			return;
//		}
//
//		SurfaceMeshDiamond temp;
//
//		t.removeFromSplitQueue();
//
//		t.addToMergeQueue(); // add to merge queue
//
//		t.setSplit(true); // mark as split
//
//		// recursively split parents
//		for (int i = 0; i < 2; i++) {
//			temp = t.parents[i];
//			if (temp == null)
//				continue;
//			split(temp);
//			temp.removeFromMergeQueue();
//		}
//
//		// get kids, insert into priority queue
//		for (int i = 0; i < 4; i++) {
//			temp = t.getChild(i);
//			if (!temp.isClipped) {
//
//				temp.updateCullInfo(radSq, drawList);
//
//				if (t.level <= maxLevel && !temp.isSplit())
//					temp.addToSplitQueue();
//
//				// add child to drawing list
//				drawList.add(temp, (temp.parents[1] == t ? 1 : 0));
//			}
//		}
//
//		// remove from drawing list
//		drawList.remove(t, 0);
//		drawList.remove(t, 1);
//	}
//
//	/**
//	 * Merges the triangles in the target diamond
//	 * 
//	 * @param t
//	 *            the target diamond
//	 */
//	private void merge(SurfaceMeshDiamond t) {
//		if (t == null)
//			return;
//
//		SurfaceMeshDiamond temp;
//
//		// if already merged, skip
//		if (!t.isSplit())
//			return;
//		t.setSplit(false); // mark as not split
//
//		// handle kids
//		for (int i = 0; i < 4; i++) {
//			temp = t.getChild(i);
//			if (!temp.getOtherParent(t).isSplit()) {
//				temp.removeFromSplitQueue(); // remove from split queue
//				if (temp.isSplit())
//					temp.addToMergeQueue();
//			}
//
//			// remove children from draw list
//			drawList.remove(temp, (temp.parents[1] == t ? 1 : 0));
//		}
//		t.removeFromMergeQueue();
//
//		for (int i = 0; i < 2; i++) {
//			SurfaceMeshDiamond p = t.parents[i];
//			if (!p.childrenSplit()) {
//				p.updateCullInfo(radSq, drawList);
//				p.addToMergeQueue();
//			}
//		}
//
//		t.addToSplitQueue(); // add to split queue
//
//		// add parent triangles to draw list
//		drawList.add(t, 0);
//		drawList.add(t, 1);
//	}
//
//	/**
//	 * @return Returns a FloatBuffer containing the current mesh as a triangle
//	 *         list. Each triangle is represented as 9 consecutive floats. The
//	 *         FloatBuffer will probably contain extra floats - use
//	 *         getTriangleCount() to find out how many floats are valid.
//	 */
//	public FloatBuffer getVertices() {
//		return drawList.getTriangleBuffer();
//	}
//
//	/**
//	 * @return Returns a FloatBuffer containing the current mesh as a triangle
//	 *         list.
//	 */
//	public FloatBuffer getNormals() {
//		return drawList.getNormalBuffer();
//	}
//
//	/**
//	 * @return the amount of triangles in the current mesh.
//	 */
//	public int getTriangleCount() {
//		return drawList.getTriAmt();
//	}
//}
//
///**
// * A list of triangles representing the current mesh.
// * 
// * @author Andr� Eriksson
// */
//class SurfTriList extends TriList {
//
//	private double totalError = 0;
//	private double totalArea = 0;
//
//	/**
//	 * @param capacity
//	 * @param marigin
//	 */
//	SurfTriList(int capacity, int marigin) {
//		super(capacity, marigin,9,true);
//	}
//
//	/**
//	 * @return the total error for all visible triangles
//	 */
//	public double getError() {
//		return totalError;
//	}
//
//	/**
//	 * @return the total area (parameter wise) for all visible triangles
//	 */
//	public double getArea() {
//		return totalArea;
//	}
//
//	/**
//	 * Adds a triangle to the list.
//	 * 
//	 * @param d
//	 *            The parent diamond of the triangle
//	 * @param j
//	 *            The index of the triangle within the diamond
//	 */
//	public void add(SurfaceMeshDiamond d, int j) {
//		// handle clipping
//		if (d.isClipped || d.parents[j].isClipped)
//			return;
//
//		totalError += d.errors[j];
//		totalArea += d.area;
//
//		float[] v = new float[9];
//		float[] n = new float[9];
//
//		calcFloats(d, j, v, n);
//
//		TriListElem t = super.add(v, n);
//
//		if (t == null)
//			return;
//
//		d.setTriangle(j, t);
//
//		if (d.cullInfo == CullInfo.OUT)
//			hide(d, j);
//	}
//
//	private void calcFloats(SurfaceMeshDiamond d, int j, float[] v, float[] n) {
//		SurfaceMeshDiamond t[] = new SurfaceMeshDiamond[3];
//		t[1] = d.parents[j];
//		if (j != 0) {
//			t[0] = d.ancestors[1];
//			t[2] = d.ancestors[0];
//		} else {
//			t[0] = d.ancestors[0];
//			t[2] = d.ancestors[1];
//		}
//		for (int i = 0, c = 0; i < 3; i++, c += 3) {
//			v[c] = (float) t[i].v.getX();
//			v[c + 1] = (float) t[i].v.getY();
//			v[c + 2] = (float) t[i].v.getZ();
//			n[c] = (float) t[i].normal.getX();
//			n[c + 1] = (float) t[i].normal.getY();
//			n[c + 2] = (float) t[i].normal.getZ();
//		}
//	}
//
//	/**
//	 * Removes a triangle from the queue.
//	 * 
//	 * @param d
//	 *            The parent diamond of the triangle
//	 * @param j
//	 *            The index of the triangle within the diamond
//	 */
//	public void remove(SurfaceMeshDiamond d, int j) {
//		// handle clipping
//		if (d.isClipped || d.parents[j].isClipped)
//			return;
//
//		hide(d, j);
//
//		// free triangle
//		d.freeTriangle(j);
//	}
//
//	/**
//	 * removes a triangle from the list, but does not erase it
//	 * 
//	 * @param d
//	 *            the diamond
//	 * @param j
//	 *            the triangle index
//	 */
//	public void hide(SurfaceMeshDiamond d, int j) {
//		if (super.hide(d.getTriangle(j))) {
//			totalError -= d.errors[j];
//			totalArea -= d.area;
//		}
//	}
//
//	/**
//	 * shows a triangle that has been hidden
//	 * 
//	 * @param d
//	 *            the diamond
//	 * @param j
//	 *            the index of the triangle
//	 */
//	public void show(SurfaceMeshDiamond d, int j) {
//		if (super.show(d.getTriangle(j))) {
//			totalError += d.errors[j];
//			totalArea += d.area;
//		}
//	}
//}
//
///**
// * A specific bucket priority queue for SurfaceMeshDiamond. Inserts culled
// * diamonds into the zeroth bucket.
// * 
// * @author André Eriksson
// */
//class SurfaceMeshBucketPQ extends BucketPQ<SurfaceMeshDiamond> {
//
//	/**
//	 * @param ba
//	 *            the bucket assigner to use
//	 */
//	SurfaceMeshBucketPQ(BucketAssigner<SurfaceMeshDiamond> ba) {
//		super(ba,false);
//	}
//
//
//	public boolean add(SurfaceMeshDiamond object) {
//		if (findLink(object) != null) // already in queue
//			return false;
//
//		// put invisible diamonds in first bucket
//		if (object.cullInfo == CullInfo.OUT)
//			return addToZeroBucket(object);
//
//		return super.add(object);
//	}
//
//	private boolean addToZeroBucket(SurfaceMeshDiamond object) {
//		if (null == object)
//			throw new NullPointerException();
//
//		Link<SurfaceMeshDiamond> elem = findLink(object);
//
//		// ignore element if already in queue
//		if (elem != null)
//			return false;
//
//		int bucketIndex = bucketAssigner.getBucketIndex(object, bucketAmt);
//
//		elem = new Link<SurfaceMeshDiamond>(object);
//
//		// update pointers
//		elem.prev = backs[bucketIndex];
//		if (backs[bucketIndex] != null)
//			backs[bucketIndex].next = elem;
//		backs[bucketIndex] = elem;
//		if (buckets[bucketIndex] == null)
//			buckets[bucketIndex] = elem;
//
//		// update max bucket index if needed
//		if (bucketIndex > maxBucket)
//			maxBucket = bucketIndex;
//
//		elem.bucketIndex = bucketIndex;
//
//		count++;
//
//		linkAssociations.put(object, elem);
//
//		return true;
//	}
//
//}