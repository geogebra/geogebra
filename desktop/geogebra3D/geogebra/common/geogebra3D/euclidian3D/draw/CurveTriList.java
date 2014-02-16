package geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.plots.CurveMesh;

/**
 * A triangle list for handling collections of segments
 * 
 * @author andre
 */
public class CurveTriList extends TriList {

	/** the most recently set scale */
	private float currScale;

	/** number of vertices to place around each end of a segment */
	private static final short nVerts = 4;

	/** values precomputed to speed up calculations */
	private static final float[] cosines = new float[nVerts];
	private static final float[] sines = new float[nVerts];
	private static boolean initialized = false;

	/**
	 * @param capacity
	 *            the goal amount of triangles available
	 * @param marigin
	 *            extra triangle amount
	 * @param scale
	 *            the scale for the segment
	 */
	public CurveTriList(int capacity, int marigin, float scale) {
		super(capacity, marigin, (nVerts + 1) * 2 * 3, true);
		currScale = scale;

		if (!initialized) {
			// generate sines and cosines
			double fac = 2 * Math.PI / nVerts;
			for (int i = 0; i < nVerts; i++) {
				cosines[i] = (float) Math.cos(i * fac);
				sines[i] = (float) Math.sin(i * fac);
			}
			initialized = true;
		}
	}

	/**
	 * Adds a segment to the curve.
	 * 
	 * @param v0
	 *            the first vertex
	 * @param v1
	 *            the second vertex
	 * @param visible
	 *            whether or not the segment is initially visible
	 * 
	 * @return a CurveTriListElem for the segment
	 */
	public TriListElem add(float[] v0, float[] v1, boolean visible) {

		CurveTriListElem elem = new CurveTriListElem(v0, v1, nVerts, cosines,
				sines);

		float[] vertices = calcVertices(elem);
		float[] normals = calcNormals(elem);

		add(elem, vertices, normals);

		if (!visible)
			hide(elem);

		return elem;
	}
	
	/**
	 * Adds a segment to the curve.
	 * 
	 * @param v0
	 *            the first vertex
	 * @param v1
	 *            the second vertex
	 * @param visible
	 *            whether or not the segment is initially visible
	 * 
	 * @return a CurveTriListElem for the segment
	 */
	public TriListElem add(float[] v0, float[] v1, float[] t0, float[] t1, boolean visible) {

		CurveTriListElem elem = new CurveTriListElem(v0, v1, t0, t1, nVerts, cosines,
				sines);

		float[] vertices = calcVertices(elem);
		float[] normals = calcNormals(elem);

		add(elem, vertices, normals);

		if (!visible)
			hide(elem);

		return elem;
	}
	
	public TriListElem add(Coords v0, Coords v1, Coords t0, Coords t1, boolean visible) {
		
		float[] v0v = new float[]{(float)v0.getX(),(float)v0.getY(),(float)v0.getZ()};
		float[] v1v = new float[]{(float)v1.getX(),(float)v1.getY(),(float)v1.getZ()};
		float[] t0v = new float[]{(float)t0.getX(),(float)t0.getY(),(float)t0.getZ()};
		float[] t1v = new float[]{(float)t1.getX(),(float)t1.getY(),(float)t1.getZ()};
		return add(v0v,v1v,t0v,t1v,visible);
	}

	/**
	 * Adds a segment to the curve.
	 * 
	 * @param v
	 *            the two vertices
	 * @param visible
	 *            whether or not the segment is initially visible
	 * 
	 * @return a CurveTriListElem for the segment
	 */
	public TriListElem add(float[][] v, boolean visible) {
		return add(v[0], v[1], visible);
	}
	
	public TriListElem add(float[] v, boolean visible) {
		return add(new float[]{v[0],v[1],v[2]}, new float[]{v[3],v[4],v[5]}, visible);
	}
	
	public TriListElem add(float[][] v, float[][] t, boolean visible) {
		return add(v[0], v[1], t[0], t[1], visible);
	}

	/**
	 * Grabs a triangle strip for the segment.
	 * 
	 * @param e
	 * @return a set of 2*(nVerts+1) vertices corresponding to a triangle strip
	 */
	private float[] calcVertices(CurveTriListElem e) {
		float[] v = new float[2 * (nVerts + 1) * 3];

		float[] v0 = e.v0;
		float[] v1 = e.v1;

		float sf = 1 / currScale;
		float inv = 1 - sf;

		for (int i = 0; i <= nVerts; i++) {
			int j = i % nVerts;
			int k = i * 6;

			v[k] = e.pts[1][j][0];
			v[k + 1] = e.pts[1][j][1];
			v[k + 2] = e.pts[1][j][2];
			v[k + 3] = e.pts[0][j][0];
			v[k + 4] = e.pts[0][j][1];
			v[k + 5] = e.pts[0][j][2];

			// scale vertices
			v[k] = v[k] * sf + v1[0] * inv;
			v[k + 1] = v[k + 1] * sf + v1[1] * inv;
			v[k + 2] = v[k + 2] * sf + v1[2] * inv;
			v[k + 3] = v[k + 3] * sf + v0[0] * inv;
			v[k + 4] = v[k + 4] * sf + v0[1] * inv;
			v[k + 5] = v[k + 5] * sf + v0[2] * inv;

			e.scale = currScale;
		}

		return v;
	}

	/**
	 * Grabs an array of normals for the triangle strip.
	 * 
	 * @param e
	 * @return
	 */
	private float[] calcNormals(CurveTriListElem e) {
		float[] normals = new float[2 * (nVerts + 1) * 3];

		for (int i = 0; i <= nVerts; i++) {
			int j = i % nVerts;
			int k = i * 6;

			normals[k] = e.nrms[1][j][0];
			normals[k + 1] = e.nrms[1][j][1];
			normals[k + 2] = e.nrms[1][j][2];
			normals[k + 3] = e.nrms[0][j][0];
			normals[k + 4] = e.nrms[0][j][1];
			normals[k + 5] = e.nrms[0][j][2];
		}

		return normals;
	}

	/**
	 * Scales all visible elements to the given scale.
	 * 
	 * @param newScale
	 */
	public void rescale(float newScale) {
		CurveTriListElem t = (CurveTriListElem) front;
		while (t != null) {

			float[] v = getVertices(t);

			float[] v0 = t.v0;
			float[] v1 = t.v1;

			float sf = t.scale / newScale; // scale factor
			float inv = 1 - sf;

			for (int i = 0; i <= nVerts; i++) {
				int k = i * 6;

				v[k] = v[k] * sf + v1[0] * inv;
				v[k + 1] = v[k + 1] * sf + v1[1] * inv;
				v[k + 2] = v[k + 2] * sf + v1[2] * inv;
				v[k + 3] = v[k + 3] * sf + v0[0] * inv;
				v[k + 4] = v[k + 4] * sf + v0[1] * inv;
				v[k + 5] = v[k + 5] * sf + v0[2] * inv;
			}

			setVertices(t, v);
			t.scale = newScale;
			t = (CurveTriListElem) t.getNext();
		}
		currScale = newScale;
	}
}

/**
 * A class corresponding to an element in a CurveTriList
 * 
 * @author andre
 */
class CurveTriListElem extends TriListElem {

	/** The vertices and curve normals at the segment endpoints */
	float[] v0, v1, t0, t1;

	/** The scale the element was last adapted to */
	float scale;

	/** The vertices and normals of the surface corresponding to the segment */
	float[][][] pts, nrms;

	/**
	 * @param v0
	 *            the first endpoint
	 * @param v1
	 *            the second endpoint
	 * @param nVertices
	 *            number of surface vertices corresponding to each endpoint
	 * @param cosines
	 *            an array of precomputed cosines
	 * @param sines
	 *            an array of precomputed sines
	 */
	public CurveTriListElem(float[] v0, float[] v1, short nVertices,
			float[] cosines, float[] sines) {
		this(v0, v1,
				new float[] { v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2] },
				new float[] { v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2] },
				nVertices, cosines, sines);
	}

	/**
	 * @param v0
	 *            the first endpoint
	 * @param v1
	 *            the second endpoint
	 * @param t0
	 *            the normal at the first endpoint
	 * @param t1
	 *            the tangent at the second endpoint
	 * @param nVertices
	 *            number of surface vertices corresponding to each endpoint
	 * @param cosines
	 *            an array of precomputed cosines
	 * @param sines
	 *            an array of precomputed sines
	 */
	public CurveTriListElem(float[] v0, float[] v1, float[] t0, float[] t1,
			short nVertices, float[] cosines, float[] sines) {
		pts = new float[2][nVertices][3];
		nrms = new float[2][nVertices][3];

		this.v0 = v0;
		this.v1 = v1;
		this.t0 = t0;
		this.t1 = t1;

		genPoints(cosines, sines, 0, t0);
		genPoints(cosines, sines, 1, t1);
	}

	/**
	 * Generates the points for the surface
	 * 
	 * @param cosines
	 *            an array of precomputed cosines
	 * @param sines
	 *            an array of precomputed sines
	 * @param index
	 *            th
	 */
	private void genPoints(float[] cosines, float[] sines, int index, float[] tangent) {
		Coords t = new Coords(tangent[0], tangent[1], tangent[2]);
		t.normalize();
		Coords c;
		if(index==0){
			c = new Coords(v0[0], v0[1], v0[2]);
		} else {
			c = new Coords(v1[0], v1[1], v1[2]);
		}
		Coords[] v = t.completeOrthonormal();

		// create midpoints
		for (int j = 0; j < CurveMesh.nVerts; j++) {
			Coords point = c.add(v[0].mul(cosines[j])).add(v[1].mul(sines[j]));
			Coords normal = point.sub(c).normalized();

			pts[index][j][0] = (float) point.getX();
			pts[index][j][1] = (float) point.getY();
			pts[index][j][2] = (float) point.getZ();

			nrms[index][j][0] = (float) normal.getX();
			nrms[index][j][1] = (float) normal.getY();
			nrms[index][j][2] = (float) normal.getZ();
		}
	}
}