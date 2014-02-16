package geogebra3D.geogebra.common.geogebra3D.euclidian3D.plots;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw.BucketAssigner;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw.TriList;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw.TriListElem;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * An element in MarchingCubes.
 * 
 * @author André Eriksson
 */
class MCElement {

	/** parameter values at the start and end of the segment */
	double[][] cornerParams = new double[3][2];

	/** signs at corners */
	int signs;

	/** values at corners */
	double[] cornerVals = new double[8];

	/** midpoints of the edges */
	double[][] edges = new double[12][3];

	double[][] cz = new double[8][3];

	/**
	 * @param signs the signs of the corners
	 * @param c corner locations
	 * @param vals values at corners
	 */
	public MCElement(int signs, double[][] c, double[] vals) {
		this.signs = signs;

		cornerVals = new double[]{vals[0],vals[1],vals[2],vals[3],vals[4],vals[5],vals[6],vals[7]};

		cz[0][0] = c[0][0];
		cz[0][1] = c[1][0];
		cz[0][2] = c[2][0];
		cz[1][0] = c[0][1];
		cz[1][1] = c[1][0];
		cz[1][2] = c[2][0];
		cz[2][0] = c[0][0];
		cz[2][1] = c[1][1];
		cz[2][2] = c[2][0];
		cz[3][0] = c[0][1];
		cz[3][1] = c[1][1];
		cz[3][2] = c[2][0];
		cz[4][0] = c[0][0];
		cz[4][1] = c[1][0];
		cz[4][2] = c[2][1];
		cz[5][0] = c[0][1];
		cz[5][1] = c[1][0];
		cz[5][2] = c[2][1];
		cz[6][0] = c[0][0];
		cz[6][1] = c[1][1];
		cz[6][2] = c[2][1];
		cz[7][0] = c[0][1];
		cz[7][1] = c[1][1];
		cz[7][2] = c[2][1];

		cornerParams = new double[3][2];
		cornerParams[0][0] = c[0][0];
		cornerParams[0][1] = c[0][1];
		cornerParams[1][0] = c[1][0];
		cornerParams[1][1] = c[1][1];
		cornerParams[2][0] = c[2][0];
		cornerParams[2][1] = c[2][1];

		edges[0][0] = (c[0][0] + c[0][1]) * .5f;
		edges[0][1] = c[1][0];
		edges[0][2] = c[2][0];

		edges[1][0] = c[0][1];
		edges[1][1] = (c[1][0] + c[1][1]) * .5f;
		edges[1][2] = c[2][0];

		edges[2][0] = (c[0][0] + c[0][1]) * .5f;
		edges[2][1] = c[1][1];
		edges[2][2] = c[2][0];

		edges[3][0] = c[0][0];
		edges[3][1] = (c[1][0] + c[1][1]) * .5f;
		edges[3][2] = c[2][0];

		edges[4][0] = c[0][1];
		edges[4][1] = c[1][0];
		edges[4][2] = (c[2][0] + c[2][1]) * .5f;

		edges[5][0] = c[0][1];
		edges[5][1] = c[1][1];
		edges[5][2] = (c[2][0] + c[2][1]) * .5f;

		edges[6][0] = c[0][0];
		edges[6][1] = c[1][1];
		edges[6][2] = (c[2][0] + c[2][1]) * .5f;

		edges[7][0] = c[0][0];
		edges[7][1] = c[1][0];
		edges[7][2] = (c[2][0] + c[2][1]) * .5f;

		edges[8][0] = (c[0][0] + c[0][1]) * .5f;
		edges[8][1] = c[1][0];
		edges[8][2] = c[2][1];

		edges[9][0] = c[0][1];
		edges[9][1] = (c[1][0] + c[1][1]) * .5f;
		edges[9][2] = c[2][1];

		edges[10][0] = (c[0][0] + c[0][1]) * .5f;
		edges[10][1] = c[1][1];
		edges[10][2] = c[2][1];

		edges[11][0] = c[0][0];
		edges[11][1] = (c[1][0] + c[1][1]) * .5f;
		edges[11][2] = c[2][1];
	}
}

/**
 * A triangle in the marching cubes implementation
 * @author André Eriksson
 */
class MCTriangle extends DynamicMeshElement2 {
	
	/** The three adjacent triangles in the mesh */
	MCTriangle[] neighbors = new MCTriangle[3];
	
	/** Triangle corners */
	Coords[] corners = new Coords[3];
	
	/** Coordinate of the midpoint of the triangle base */
	Coords midpoint;
	
	/** Reference to the function being modeled*/
	GeoFunctionNVar func;

	/** The error associated with the element */
	double error;
	/** The associated triangle list element */
	public TriListElem triListElem;

	/**
	 * Standard constructor
	 * @param cs corner coordinates
	 * @param func the function being modeled
	 */
	MCTriangle(double[][] cs, GeoFunctionNVar func) {
		super(null, 0, false, 0);

		this.func = func;

		Coords c0 = new Coords(cs[0]);
		Coords c1 = new Coords(cs[1]);
		Coords c2 = new Coords(cs[2]);

		double d0 = c0.distance(c1);
		double d1 = c1.distance(c2);
		double d2 = c2.distance(c0);

		if (d0 > d1 && d0 > d2) {
			corners[0] = c0;
			corners[1] = c1;
			corners[2] = c2;
		}
		if (d1 > d2 && d1 > d0) {
			corners[0] = c1;
			corners[1] = c2;
			corners[2] = c0;
		} else {
			corners[0] = c2;
			corners[1] = c0;
			corners[2] = c1;
		}

		// create the midpoint
		midpoint = MarchingCubes.project(corners[0].add(corners[1]).mul(0.5),
				func);

		// calculate the error
		calcError();
	}

	/**
	 * Calculates the error of the element.
	 */
	private void calcError() {
		Coords a0 = corners[0].sub(midpoint);
		Coords a1 = corners[1].sub(midpoint);
		Coords a2 = corners[2].sub(midpoint);

		error = Math.abs(a0.dotproduct(a1.crossProduct(a2)));
		System.out.print("");
	}

	/**
	 * Removes a link to a neighbor
	 * @param a The neighbor to remove
	 */
	public void removeNeighbor(MCTriangle a) {
		boolean f = false;
		for (int i = 0; i < 4; i++)
			if (f) {
				if (i == 3)
					neighbors[2] = null;
				else
					neighbors[i - 1] = neighbors[i];
			} else if (i < 3 && neighbors[i] == a)
				f = true;
	}

	/**
	 * 
	 */
	public void resort() {
		MCTriangle[] tn = new MCTriangle[]{neighbors[0],neighbors[1],neighbors[2]};
		neighbors[0] = neighbors[1] = neighbors[2] = null;

		for (int x = 0; x < 3; x++) {
			MCTriangle temp = tn[x];
			if (temp == null)
				continue;

			// check for shared corners
			int flags = 0;
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					if (corners[i].equalsForKernel(temp.corners[j], 1e-5)) {
						flags |= 1 << i;
						break;
					}
			switch (flags) {
			case 3: // base edge
				neighbors[0] = temp;
				break;
			case 6: // next edge
				neighbors[1] = temp;
				break;
			case 5: // final edge
				neighbors[2] = temp;
				break;
			}
		}
	}

	public void addNeighbor(MCTriangle b) {
		if (neighbors[0] == null)
			neighbors[0] = b;
		else if (neighbors[1] == null)
			neighbors[1] = b;
		else if (neighbors[2] == null)
			neighbors[2] = b;
		else
			System.out.println("error");
	}

	void setNeighbors(List<MCTriangle> tris) {
		ListIterator<MCTriangle> it = tris.listIterator();
		while (it.hasNext()) {
			MCTriangle temp = it.next();
			if (temp != this) {
				// check for shared corners
				int flags = 0;
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++)
						if (corners[i].equalsForKernel(temp.corners[j], 1e-5)) {
							flags |= 1 << i;
							break;
						}

				switch (flags) {
				case 3: // base edge
					neighbors[0] = temp;
					break;
				case 6: // next edge
					neighbors[1] = temp;
					break;
				case 5: // final edge
					neighbors[2] = temp;
					break;
				}
			}
		}
	}

	@Override
	protected void createChild(int i) {
		if (i == 0) {
			children[0] = new MCTriangle(
					new double[][] {
							{ corners[0].getX(), corners[0].getY(),
									corners[0].getZ() },
							{ midpoint.getX(), midpoint.getY(), midpoint.getZ() },
							{ corners[2].getX(), corners[2].getY(),
									corners[2].getZ() } }, func);
		} else if (i == 1) {
			children[1] = new MCTriangle(
					new double[][] {
							{ corners[2].getX(), corners[2].getY(),
									corners[2].getZ() },
							{ midpoint.getX(), midpoint.getY(), midpoint.getZ() },
							{ corners[1].getX(), corners[1].getY(),
									corners[1].getZ() } }, func);
		}
	}

	@Override
	protected double getError() {
		return error;
	}

	@Override
	protected void setHidden(boolean val) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void reinsertInQueue() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void cullChildren() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean recalculate(int currentVersion, boolean recurse) {
		// TODO Auto-generated method stub
		return false;
	}

	public void switchNeighbor(MCTriangle a, MCTriangle d1) {
		d1.addNeighbor(this);
		for (int i = 0; i < 3; i++)
			if (neighbors[i] == a) {
				neighbors[i] = d1;
				return;
			}
		System.out.println("error");
	}

	public boolean hasNeighbor(MCTriangle a) {
		for (int i = 0; i < 3; i++)
			if (a == neighbors[i])
				return true;
		return false;
	}
}

/**
 * Triangle list used for MC
 * 
 * @author André Eriksson
 */
class MCTriList extends TriList {

	GeoFunctionNVar f;
	private final double delta = 1e-4;

	/**
	 * @param capacity
	 *            the goal amount of triangles available
	 * @param margin
	 *            extra triangle amount
	 */
	MCTriList(GeoFunctionNVar fcn, int capacity, int margin) {
		super(capacity, margin, 9, true);
		f = fcn;
	}

	double errorSum() {
		double sum = 0;
		for (TriListElem elem : this)
			sum += ((MCTriangle) elem.getOwner()).getError();
		return sum;
	}

	/**
	 * Adds a segment to the curve. If the segment vertices are unspecified,
	 * these are created.
	 * 
	 * @param e
	 *            the segment to add
	 */
	public void gen(MCElement e, List<MCTriangle> tris) {
		double[][][] t = getTriangles(e);
		if (t != null) {
			for (int i = 0; i < t.length; i++) {
				// System.out.println("added " +t.length+ " triangles");

				// TODO: remove the unneccessary copying of t to v here
				float[] v = new float[9];
				float[] n = new float[9];
				v[0] = (float) t[i][0][0];
				v[1] = (float) t[i][0][1];
				v[2] = (float) t[i][0][2];

				v[3] = (float) t[i][1][0];
				v[4] = (float) t[i][1][1];
				v[5] = (float) t[i][1][2];

				v[6] = (float) t[i][2][0];
				v[7] = (float) t[i][2][1];
				v[8] = (float) t[i][2][2];

				calcNormals(v, n);

				Coords v1 = MarchingCubes.project(new Coords(v[0], v[1], v[2]),
						f);
				Coords v2 = MarchingCubes.project(new Coords(v[3], v[4], v[5]),
						f);
				Coords v3 = MarchingCubes.project(new Coords(v[6], v[7], v[8]),
						f);

				v[0] = (float) v1.getX();
				v[1] = (float) v1.getY();
				v[2] = (float) v1.getZ();
				v[3] = (float) v2.getX();
				v[4] = (float) v2.getY();
				v[5] = (float) v2.getZ();
				v[6] = (float) v3.getX();
				v[7] = (float) v3.getY();
				v[8] = (float) v3.getZ();

				MCTriangle tri = new MCTriangle(new double[][] {
						{ v[0], v[1], v[2] }, { v[3], v[4], v[5] },
						{ v[6], v[7], v[8] } }, f);
				tris.add(tri);

				TriListElem el = add(v, n);
				el.setOwner(tri);
				tri.triListElem = el;
			}
		}
	}

	private void calcNormals(float[] f, float[] n) {
		calcNormal(f, n, 0);
		calcNormal(f, n, 3);
		calcNormal(f, n, 6);
	}

	private void calcNormal(float[] v, float[] n, int offset) {
		double x = v[offset];
		double y = v[offset + 1];
		double z = v[offset + 2];
		double val = f.evaluate(x, y, z);
		float dx = (float) (f.evaluate(x + delta, y, z) - val);
		float dy = (float) (f.evaluate(x, y + delta, z) - val);
		float dz = (float) (f.evaluate(x, y, z + delta) - val);
		float sum = dx + dy + dz;
		n[offset] = dx / sum;
		n[offset + 1] = dy / sum;
		n[offset + 2] = dz / sum;
	}

	/**
	 * @param e
	 *            the element to search
	 * @return a list of triangles
	 */
	double[][][] getTriangles(MCElement e) {
		double[][][] t = null;
		double[][] v = e.edges;
		int signs = e.signs;

		if (signs != 0x0 && signs != 0xff)
			System.out.print("");

		switch (signs) {
		case 0x00:
			break;
		case 0x01:
			t = new double[1][3][3];
			t[0][0] = v[7];
			t[0][1] = v[0];
			t[0][2] = v[3];
			break;
		case 0x02:
			t = new double[1][3][3];
			t[0][0] = v[4];
			t[0][1] = v[1];
			t[0][2] = v[0];
			break;
		case 0x03:
			t = new double[2][3][3];
			t[0][0] = v[7];
			t[0][1] = v[4];
			t[0][2] = v[3];
			t[1][0] = v[4];
			t[1][1] = v[1];
			t[1][2] = v[3];
			break;
		case 0x04:
			t = new double[1][3][3];
			t[0][0] = v[6];
			t[0][1] = v[3];
			t[0][2] = v[2];
			break;
		case 0x05:
			t = new double[2][3][3];
			t[0][0] = v[6];
			t[0][1] = v[7];
			t[0][2] = v[2];
			t[1][0] = v[7];
			t[1][1] = v[0];
			t[1][2] = v[2];
			break;
		case 0x06:
			t = new double[2][3][3];
			t[0][0] = v[0];
			t[0][1] = v[4];
			t[0][2] = v[1];
			t[1][0] = v[3];
			t[1][1] = v[2];
			t[1][2] = v[6];
			break;
		case 0x07:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[6];
			t[0][2] = v[1];
			t[1][0] = v[6];
			t[1][1] = v[4];
			t[1][2] = v[1];
			t[2][0] = v[6];
			t[2][1] = v[7];
			t[2][2] = v[4];
			break;
		case 0x08:
			t = new double[1][3][3];
			t[0][0] = v[5];
			t[0][1] = v[2];
			t[0][2] = v[1];
			break;
		case 0x09:
			t = new double[2][3][3];
			t[0][0] = v[1];
			t[0][1] = v[5];
			t[0][2] = v[2];
			t[1][0] = v[0];
			t[1][1] = v[3];
			t[1][2] = v[7];
			break;
		case 0x0a:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[5];
			t[0][2] = v[0];
			t[1][0] = v[5];
			t[1][1] = v[2];
			t[1][2] = v[0];
			break;
		case 0x0b:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[7];
			t[0][2] = v[2];
			t[1][0] = v[7];
			t[1][1] = v[5];
			t[1][2] = v[2];
			t[2][0] = v[7];
			t[2][1] = v[4];
			t[2][2] = v[5];
			break;
		case 0x0c:
			t = new double[2][3][3];
			t[0][0] = v[5];
			t[0][1] = v[6];
			t[0][2] = v[1];
			t[1][0] = v[6];
			t[1][1] = v[3];
			t[1][2] = v[1];
			break;
		case 0x0d:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[5];
			t[0][2] = v[0];
			t[1][0] = v[5];
			t[1][1] = v[7];
			t[1][2] = v[0];
			t[2][0] = v[5];
			t[2][1] = v[6];
			t[2][2] = v[7];
			break;
		case 0x0e:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[4];
			t[0][2] = v[3];
			t[1][0] = v[4];
			t[1][1] = v[6];
			t[1][2] = v[3];
			t[2][0] = v[4];
			t[2][1] = v[5];
			t[2][2] = v[6];
			break;
		case 0x0f:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[5];
			t[0][2] = v[7];
			t[1][0] = v[7];
			t[1][1] = v[5];
			t[1][2] = v[6];
			break;
		case 0x10:
			t = new double[1][3][3];
			t[0][0] = v[8];
			t[0][1] = v[7];
			t[0][2] = v[11];
			break;
		case 0x11:
			t = new double[2][3][3];
			t[0][0] = v[8];
			t[0][1] = v[0];
			t[0][2] = v[11];
			t[1][0] = v[0];
			t[1][1] = v[3];
			t[1][2] = v[11];
			break;
		case 0x12:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[1];
			t[0][2] = v[0];
			t[1][0] = v[8];
			t[1][1] = v[7];
			t[1][2] = v[11];
			break;
		case 0x13:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[1];
			t[0][2] = v[8];
			t[1][0] = v[1];
			t[1][1] = v[11];
			t[1][2] = v[8];
			t[2][0] = v[1];
			t[2][1] = v[3];
			t[2][2] = v[11];
			break;
		case 0x14:
			t = new double[2][3][3];
			t[0][0] = v[3];
			t[0][1] = v[2];
			t[0][2] = v[6];
			t[1][0] = v[7];
			t[1][1] = v[11];
			t[1][2] = v[8];
			break;
		case 0x15:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[8];
			t[0][2] = v[6];
			t[1][0] = v[8];
			t[1][1] = v[2];
			t[1][2] = v[6];
			t[2][0] = v[8];
			t[2][1] = v[0];
			t[2][2] = v[2];
			break;
		case 0x16:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[2];
			t[0][2] = v[3];
			t[1][0] = v[11];
			t[1][1] = v[8];
			t[1][2] = v[7];
			t[2][0] = v[1];
			t[2][1] = v[0];
			t[2][2] = v[4];
			break;
		case 0x17:
			t = new double[4][3][3];
			t[0][0] = v[2];
			t[0][1] = v[6];
			t[0][2] = v[1];
			t[1][0] = v[1];
			t[1][1] = v[6];
			t[1][2] = v[4];
			t[2][0] = v[4];
			t[2][1] = v[6];
			t[2][2] = v[11];
			t[3][0] = v[4];
			t[3][1] = v[11];
			t[3][2] = v[8];
			break;
		case 0x18:
			t = new double[2][3][3];
			t[0][0] = v[5];
			t[0][1] = v[2];
			t[0][2] = v[1];
			t[1][0] = v[11];
			t[1][1] = v[8];
			t[1][2] = v[7];
			break;
		case 0x19:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[11];
			t[1][2] = v[0];
			t[1][0] = v[11];
			t[1][1] = v[8];
			t[1][2] = v[0];
			t[2][0] = v[2];
			t[2][1] = v[1];
			t[2][2] = v[5];
			break;
		case 0x1a:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[5];
			t[1][2] = v[0];
			t[1][0] = v[5];
			t[1][1] = v[2];
			t[1][2] = v[0];
			t[2][0] = v[8];
			t[2][1] = v[7];
			t[2][2] = v[11];
			break;
		case 0x1c:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[1];
			t[1][2] = v[6];
			t[1][0] = v[1];
			t[1][1] = v[5];
			t[1][2] = v[6];
			t[2][0] = v[7];
			t[2][1] = v[11];
			t[2][2] = v[8];
			break;
		case 0x1d:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[1];
			t[0][2] = v[6];
			t[1][0] = v[6];
			t[1][1] = v[1];
			t[1][2] = v[5];
			t[2][0] = v[11];
			t[2][1] = v[8];
			t[2][2] = v[6];
			break;
		case 0x1e:
			t = new double[4][3][3];
			t[0][0] = v[8];
			t[0][1] = v[11];
			t[0][2] = v[6];
			t[1][0] = v[8];
			t[1][1] = v[6];
			t[1][2] = v[4];
			t[2][0] = v[4];
			t[2][1] = v[6];
			t[2][2] = v[5];
			t[3][0] = v[0];
			t[3][1] = v[7];
			t[3][2] = v[3];
			break;
		case 0x1f:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[11];
			t[0][2] = v[8];
			t[1][0] = v[4];
			t[1][1] = v[6];
			t[1][2] = v[8];
			t[2][0] = v[5];
			t[2][1] = v[6];
			t[2][2] = v[4];
			break;
		case 0x20:
			t = new double[1][3][3];
			t[0][0] = v[9];
			t[0][1] = v[4];
			t[0][2] = v[8];
			break;
		case 0x21:
			t = new double[2][3][3];
			t[0][0] = v[0];
			t[0][1] = v[3];
			t[0][2] = v[7];
			t[1][0] = v[4];
			t[1][1] = v[8];
			t[1][2] = v[9];
			break;
		case 0x22:
			t = new double[2][3][3];
			t[0][0] = v[9];
			t[0][1] = v[1];
			t[0][2] = v[8];
			t[1][0] = v[1];
			t[1][1] = v[0];
			t[1][2] = v[8];
			break;
		case 0x23:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[9];
			t[0][2] = v[7];
			t[1][0] = v[9];
			t[1][1] = v[3];
			t[1][2] = v[7];
			t[2][0] = v[9];
			t[2][1] = v[1];
			t[2][2] = v[3];
			break;
		case 0x24:
			t = new double[2][3][3];
			t[0][0] = v[6];
			t[0][1] = v[3];
			t[0][2] = v[2];
			t[1][0] = v[8];
			t[1][1] = v[9];
			t[1][2] = v[4];
			break;
		case 0x25:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[2];
			t[1][2] = v[7];
			t[1][0] = v[2];
			t[1][1] = v[6];
			t[1][2] = v[7];
			t[2][0] = v[4];
			t[2][1] = v[8];
			t[2][2] = v[9];
			break;
		case 0x26:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[8];
			t[1][2] = v[1];
			t[1][0] = v[8];
			t[1][1] = v[9];
			t[1][2] = v[1];
			t[2][0] = v[3];
			t[2][1] = v[2];
			t[2][2] = v[6];
			break;
		case 0x27:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[2];
			t[0][2] = v[7];
			t[1][0] = v[7];
			t[1][1] = v[2];
			t[1][2] = v[6];
			t[2][0] = v[8];
			t[2][1] = v[9];
			t[2][2] = v[7];
			break;
		case 0x28:
			t = new double[2][3][3];
			t[0][0] = v[5];
			t[0][1] = v[2];
			t[0][2] = v[1];
			t[1][0] = v[9];
			t[1][1] = v[4];
			t[1][2] = v[8];
			break;
		case 0x29:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[3];
			t[0][2] = v[0];
			t[1][0] = v[8];
			t[1][1] = v[9];
			t[1][2] = v[4];
			t[2][0] = v[2];
			t[2][1] = v[1];
			t[2][2] = v[5];
			break;
		case 0x2a:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[2];
			t[0][2] = v[9];
			t[1][0] = v[2];
			t[1][1] = v[8];
			t[1][2] = v[9];
			t[2][0] = v[2];
			t[2][1] = v[0];
			t[2][2] = v[8];
			break;
		case 0x2b:
			t = new double[4][3][3];
			t[0][0] = v[3];
			t[0][1] = v[7];
			t[0][2] = v[2];
			t[1][0] = v[2];
			t[1][1] = v[7];
			t[1][2] = v[5];
			t[2][0] = v[5];
			t[2][1] = v[7];
			t[2][2] = v[8];
			t[3][0] = v[5];
			t[3][1] = v[8];
			t[3][2] = v[9];
			break;
		case 0x2c:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[6];
			t[1][2] = v[1];
			t[1][0] = v[6];
			t[1][1] = v[3];
			t[1][2] = v[1];
			t[2][0] = v[9];
			t[2][1] = v[4];
			t[2][2] = v[8];
			break;
		case 0x2d:
			t = new double[4][3][3];
			t[0][0] = v[9];
			t[0][1] = v[8];
			t[0][2] = v[7];
			t[1][0] = v[9];
			t[1][1] = v[7];
			t[1][2] = v[5];
			t[2][0] = v[5];
			t[2][1] = v[7];
			t[2][2] = v[6];
			t[3][0] = v[1];
			t[3][1] = v[4];
			t[3][2] = v[0];
			break;
		case 0x2f:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[8];
			t[0][2] = v[9];
			t[1][0] = v[5];
			t[1][1] = v[7];
			t[1][2] = v[9];
			t[2][0] = v[6];
			t[2][1] = v[7];
			t[2][2] = v[5];
			break;
		case 0x30:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[7];
			t[0][2] = v[9];
			t[1][0] = v[7];
			t[1][1] = v[11];
			t[1][2] = v[9];
			break;
		case 0x31:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[3];
			t[0][2] = v[4];
			t[1][0] = v[3];
			t[1][1] = v[9];
			t[1][2] = v[4];
			t[2][0] = v[3];
			t[2][1] = v[11];
			t[2][2] = v[9];
			break;
		case 0x32:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[11];
			t[0][2] = v[0];
			t[1][0] = v[11];
			t[1][1] = v[1];
			t[1][2] = v[0];
			t[2][0] = v[11];
			t[2][1] = v[9];
			t[2][2] = v[1];
			break;
		case 0x33:
			t = new double[2][3][3];
			t[0][0] = v[9];
			t[0][1] = v[1];
			t[0][2] = v[11];
			t[1][0] = v[11];
			t[1][1] = v[1];
			t[1][2] = v[3];
			break;
		case 0x34:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[9];
			t[1][2] = v[7];
			t[1][0] = v[9];
			t[1][1] = v[4];
			t[1][2] = v[7];
			t[2][0] = v[6];
			t[2][1] = v[3];
			t[2][2] = v[2];
			break;
		case 0x36:
			t = new double[4][3][3];
			t[0][0] = v[6];
			t[0][1] = v[2];
			t[0][2] = v[1];
			t[1][0] = v[6];
			t[1][1] = v[1];
			t[1][2] = v[11];
			t[2][0] = v[11];
			t[2][1] = v[1];
			t[2][2] = v[9];
			t[3][0] = v[7];
			t[3][1] = v[3];
			t[3][2] = v[0];
			break;
		case 0x37:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[2];
			t[0][2] = v[6];
			t[1][0] = v[11];
			t[1][1] = v[1];
			t[1][2] = v[6];
			t[2][0] = v[9];
			t[2][1] = v[1];
			t[2][2] = v[11];
			break;
		case 0x38:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[7];
			t[1][2] = v[9];
			t[1][0] = v[7];
			t[1][1] = v[11];
			t[1][2] = v[9];
			t[2][0] = v[1];
			t[2][1] = v[5];
			t[2][2] = v[2];
			break;
		case 0x39:
			t = new double[4][3][3];
			t[0][0] = v[2];
			t[0][1] = v[5];
			t[0][2] = v[9];
			t[1][0] = v[2];
			t[1][1] = v[9];
			t[1][2] = v[3];
			t[2][0] = v[3];
			t[2][1] = v[9];
			t[2][2] = v[11];
			t[3][0] = v[0];
			t[3][1] = v[1];
			t[3][2] = v[4];
			break;
		case 0x3a:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[5];
			t[0][2] = v[0];
			t[1][0] = v[0];
			t[1][1] = v[5];
			t[1][2] = v[2];
			t[2][0] = v[7];
			t[2][1] = v[11];
			t[2][2] = v[0];
			break;
		case 0x3b:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[5];
			t[0][2] = v[2];
			t[1][0] = v[3];
			t[1][1] = v[9];
			t[1][2] = v[2];
			t[2][0] = v[11];
			t[2][1] = v[9];
			t[2][2] = v[3];
			break;
		case 0x3c:
			t = new double[4][3][3];
			t[0][0] = v[1];
			t[0][1] = v[4];
			t[0][2] = v[7];
			t[1][0] = v[3];
			t[1][1] = v[1];
			t[1][2] = v[7];
			t[2][0] = v[11];
			t[2][1] = v[9];
			t[2][2] = v[8];
			t[3][0] = v[11];
			t[3][1] = v[8];
			t[3][2] = v[6];
			break;
		case 0x3d:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[9];
			t[1][2] = v[5];
			t[1][0] = v[6];
			t[1][1] = v[11];
			t[1][2] = v[5];
			t[2][0] = v[1];
			t[2][1] = v[4];
			t[2][2] = v[0];
			break;
		case 0x3e:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[6];
			t[1][2] = v[11];
			t[1][0] = v[9];
			t[1][1] = v[5];
			t[1][2] = v[11];
			t[2][0] = v[7];
			t[2][1] = v[3];
			t[2][2] = v[0];
			break;
		case 0x3f:
			t = new double[2][3][3];
			t[0][0] = v[5];
			t[0][1] = v[6];
			t[0][2] = v[11];
			t[1][0] = v[9];
			t[1][1] = v[5];
			t[1][2] = v[11];
			break;
		case 0x40:
			t = new double[1][3][3];
			t[0][0] = v[11];
			t[0][1] = v[6];
			t[0][2] = v[10];
			break;
		case 0x41:
			t = new double[2][3][3];
			t[0][0] = v[7];
			t[0][1] = v[0];
			t[0][2] = v[3];
			t[1][0] = v[11];
			t[1][1] = v[6];
			t[1][2] = v[10];
			break;
		case 0x42:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[1];
			t[0][2] = v[0];
			t[1][0] = v[10];
			t[1][1] = v[11];
			t[1][2] = v[6];
			break;
		case 0x43:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[4];
			t[1][2] = v[3];
			t[1][0] = v[4];
			t[1][1] = v[1];
			t[1][2] = v[3];
			t[2][0] = v[11];
			t[2][1] = v[6];
			t[2][2] = v[10];
			break;
		case 0x44:
			t = new double[2][3][3];
			t[0][0] = v[11];
			t[0][1] = v[3];
			t[0][2] = v[10];
			t[1][0] = v[3];
			t[1][1] = v[2];
			t[1][2] = v[10];
			break;
		case 0x45:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[0];
			t[0][2] = v[11];
			t[1][0] = v[0];
			t[1][1] = v[10];
			t[1][2] = v[11];
			t[2][0] = v[0];
			t[2][1] = v[2];
			t[2][2] = v[10];
			break;
		case 0x46:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[10];
			t[1][2] = v[3];
			t[1][0] = v[10];
			t[1][1] = v[11];
			t[1][2] = v[3];
			t[2][0] = v[1];
			t[2][1] = v[0];
			t[2][2] = v[4];
			break;
		case 0x48:
			t = new double[2][3][3];
			t[0][0] = v[2];
			t[0][1] = v[1];
			t[0][2] = v[5];
			t[1][0] = v[6];
			t[1][1] = v[10];
			t[1][2] = v[11];
			break;
		case 0x49:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[1];
			t[0][2] = v[2];
			t[1][0] = v[10];
			t[1][1] = v[11];
			t[1][2] = v[6];
			t[2][0] = v[0];
			t[2][1] = v[3];
			t[2][2] = v[7];
			break;
		case 0x4a:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[0];
			t[1][2] = v[5];
			t[1][0] = v[0];
			t[1][1] = v[4];
			t[1][2] = v[5];
			t[2][0] = v[6];
			t[2][1] = v[10];
			t[2][2] = v[11];
			break;
		case 0x4b:
			t = new double[4][3][3];
			t[0][0] = v[11];
			t[0][1] = v[10];
			t[0][2] = v[5];
			t[1][0] = v[11];
			t[1][1] = v[5];
			t[1][2] = v[7];
			t[2][0] = v[7];
			t[2][1] = v[5];
			t[2][2] = v[4];
			t[3][0] = v[3];
			t[3][1] = v[6];
			t[3][2] = v[2];
			break;
		case 0x4c:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[11];
			t[0][2] = v[5];
			t[1][0] = v[11];
			t[1][1] = v[1];
			t[1][2] = v[5];
			t[2][0] = v[11];
			t[2][1] = v[3];
			t[2][2] = v[1];
			break;
		case 0x4d:
			t = new double[4][3][3];
			t[0][0] = v[1];
			t[0][1] = v[5];
			t[0][2] = v[0];
			t[1][0] = v[0];
			t[1][1] = v[5];
			t[1][2] = v[7];
			t[2][0] = v[7];
			t[2][1] = v[5];
			t[2][2] = v[10];
			t[3][0] = v[7];
			t[3][1] = v[10];
			t[3][2] = v[11];
			break;
		case 0x4e:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[0];
			t[0][2] = v[5];
			t[1][0] = v[5];
			t[1][1] = v[0];
			t[1][2] = v[4];
			t[2][0] = v[10];
			t[2][1] = v[11];
			t[2][2] = v[5];
			break;
		case 0x4f:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[10];
			t[0][2] = v[11];
			t[1][0] = v[7];
			t[1][1] = v[5];
			t[1][2] = v[11];
			t[2][0] = v[4];
			t[2][1] = v[5];
			t[2][2] = v[7];
			break;
		case 0x50:
			t = new double[2][3][3];
			t[0][0] = v[7];
			t[0][1] = v[6];
			t[0][2] = v[8];
			t[1][0] = v[6];
			t[1][1] = v[10];
			t[1][2] = v[8];
			break;
		case 0x51:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[10];
			t[0][2] = v[3];
			t[1][0] = v[10];
			t[1][1] = v[0];
			t[1][2] = v[3];
			t[2][0] = v[10];
			t[2][1] = v[8];
			t[2][2] = v[0];
			break;
		case 0x52:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[6];
			t[1][2] = v[8];
			t[1][0] = v[6];
			t[1][1] = v[10];
			t[1][2] = v[8];
			t[2][0] = v[0];
			t[2][1] = v[4];
			t[2][2] = v[1];
			break;
		case 0x53:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[4];
			t[0][2] = v[3];
			t[1][0] = v[3];
			t[1][1] = v[4];
			t[1][2] = v[1];
			t[2][0] = v[6];
			t[2][1] = v[10];
			t[2][2] = v[3];
			break;
		case 0x54:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[2];
			t[0][2] = v[7];
			t[1][0] = v[2];
			t[1][1] = v[8];
			t[1][2] = v[7];
			t[2][0] = v[2];
			t[2][1] = v[10];
			t[2][2] = v[8];
			break;
		case 0x55:
			t = new double[2][3][3];
			t[0][0] = v[8];
			t[0][1] = v[0];
			t[0][2] = v[10];
			t[1][0] = v[10];
			t[1][1] = v[0];
			t[1][2] = v[2];
			break;
		case 0x56:
			t = new double[4][3][3];
			t[0][0] = v[1];
			t[0][1] = v[4];
			t[0][2] = v[8];
			t[1][0] = v[1];
			t[1][1] = v[8];
			t[1][2] = v[2];
			t[2][0] = v[2];
			t[2][1] = v[8];
			t[2][2] = v[10];
			t[3][0] = v[3];
			t[3][1] = v[0];
			t[3][2] = v[7];
			break;
		case 0x57:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[4];
			t[0][2] = v[1];
			t[1][0] = v[2];
			t[1][1] = v[8];
			t[1][2] = v[1];
			t[2][0] = v[10];
			t[2][1] = v[8];
			t[2][2] = v[2];
			break;
		case 0x58:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[8];
			t[1][2] = v[6];
			t[1][0] = v[8];
			t[1][1] = v[7];
			t[1][2] = v[6];
			t[2][0] = v[5];
			t[2][1] = v[2];
			t[2][2] = v[1];
			break;
		case 0x59:
			t = new double[4][3][3];
			t[0][0] = v[5];
			t[0][1] = v[1];
			t[0][2] = v[0];
			t[1][0] = v[5];
			t[1][1] = v[0];
			t[1][2] = v[10];
			t[2][0] = v[10];
			t[2][1] = v[0];
			t[2][2] = v[8];
			t[3][0] = v[6];
			t[3][1] = v[2];
			t[3][2] = v[3];
			break;
		case 0x5a:
			t = new double[4][3][3];
			t[0][0] = v[0];
			t[0][1] = v[7];
			t[0][2] = v[6];
			t[1][0] = v[2];
			t[1][1] = v[0];
			t[1][2] = v[6];
			t[2][0] = v[10];
			t[2][1] = v[8];
			t[2][2] = v[11];
			t[3][0] = v[10];
			t[3][1] = v[11];
			t[3][2] = v[5];
			break;
		case 0x5b:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[5];
			t[1][2] = v[10];
			t[1][0] = v[8];
			t[1][1] = v[4];
			t[1][2] = v[10];
			t[2][0] = v[6];
			t[2][1] = v[2];
			t[2][2] = v[3];
			break;
		case 0x5d:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[1];
			t[0][2] = v[5];
			t[1][0] = v[10];
			t[1][1] = v[0];
			t[1][2] = v[5];
			t[2][0] = v[8];
			t[2][1] = v[0];
			t[2][2] = v[10];
			break;
		case 0x5e:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[8];
			t[1][2] = v[4];
			t[1][0] = v[5];
			t[1][1] = v[10];
			t[1][2] = v[4];
			t[2][0] = v[0];
			t[2][1] = v[7];
			t[2][2] = v[3];
			break;
		case 0x5f:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[5];
			t[0][2] = v[10];
			t[1][0] = v[8];
			t[1][1] = v[4];
			t[1][2] = v[10];
			break;
		case 0x60:
			t = new double[2][3][3];
			t[0][0] = v[9];
			t[0][1] = v[4];
			t[0][2] = v[8];
			t[1][0] = v[10];
			t[1][1] = v[11];
			t[1][2] = v[6];
			break;
		case 0x61:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[9];
			t[0][2] = v[8];
			t[1][0] = v[0];
			t[1][1] = v[3];
			t[1][2] = v[7];
			t[2][0] = v[10];
			t[2][1] = v[11];
			t[2][2] = v[6];
			break;
		case 0x62:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[1];
			t[1][2] = v[8];
			t[1][0] = v[1];
			t[1][1] = v[0];
			t[1][2] = v[8];
			t[2][0] = v[10];
			t[2][1] = v[11];
			t[2][2] = v[6];
			break;
		case 0x63:
			t = new double[4][3][3];
			t[0][0] = v[10];
			t[0][1] = v[6];
			t[0][2] = v[3];
			t[1][0] = v[10];
			t[1][1] = v[3];
			t[1][2] = v[9];
			t[2][0] = v[9];
			t[2][1] = v[3];
			t[2][2] = v[1];
			t[3][0] = v[8];
			t[3][1] = v[11];
			t[3][2] = v[7];
			break;
		case 0x64:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[3];
			t[1][2] = v[10];
			t[1][0] = v[3];
			t[1][1] = v[2];
			t[1][2] = v[10];
			t[2][0] = v[8];
			t[2][1] = v[9];
			t[2][2] = v[4];
			break;
		case 0x65:
			t = new double[4][3][3];
			t[0][0] = v[4];
			t[0][1] = v[9];
			t[0][2] = v[10];
			t[1][0] = v[4];
			t[1][1] = v[10];
			t[1][2] = v[0];
			t[2][0] = v[0];
			t[2][1] = v[10];
			t[2][2] = v[2];
			t[3][0] = v[7];
			t[3][1] = v[8];
			t[3][2] = v[11];
			break;
		case 0x66:
			t = new double[4][3][3];
			t[0][0] = v[3];
			t[0][1] = v[0];
			t[0][2] = v[8];
			t[1][0] = v[11];
			t[1][1] = v[3];
			t[1][2] = v[8];
			t[2][0] = v[9];
			t[2][1] = v[1];
			t[2][2] = v[4];
			t[3][0] = v[9];
			t[3][1] = v[4];
			t[3][2] = v[10];
			break;
		case 0x67:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[10];
			t[1][2] = v[9];
			t[1][0] = v[1];
			t[1][1] = v[2];
			t[1][2] = v[9];
			t[2][0] = v[8];
			t[2][1] = v[11];
			t[2][2] = v[7];
			break;
		case 0x68:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[11];
			t[0][2] = v[10];
			t[1][0] = v[2];
			t[1][1] = v[1];
			t[1][2] = v[5];
			t[2][0] = v[8];
			t[2][1] = v[9];
			t[2][2] = v[4];
			break;
		case 0x69:
			t = new double[4][3][3];
			t[0][0] = v[1];
			t[0][1] = v[4];
			t[0][2] = v[0];
			t[1][0] = v[10];
			t[1][1] = v[9];
			t[1][2] = v[8];
			t[2][0] = v[7];
			t[2][1] = v[8];
			t[2][2] = v[11];
			t[3][0] = v[6];
			t[3][1] = v[2];
			t[3][2] = v[3];
			break;
		case 0x6a:
			t = new double[4][3][3];
			t[0][0] = v[6];
			t[0][1] = v[11];
			t[0][2] = v[8];
			t[1][0] = v[6];
			t[1][1] = v[8];
			t[1][2] = v[2];
			t[2][0] = v[2];
			t[2][1] = v[8];
			t[2][2] = v[0];
			t[3][0] = v[5];
			t[3][1] = v[10];
			t[3][2] = v[9];
			break;
		case 0x6b:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[7];
			t[0][2] = v[11];
			t[1][0] = v[2];
			t[1][1] = v[3];
			t[1][2] = v[6];
			t[2][0] = v[10];
			t[2][1] = v[9];
			t[2][2] = v[5];
			break;
		case 0x6c:
			t = new double[4][3][3];
			t[0][0] = v[8];
			t[0][1] = v[4];
			t[0][2] = v[1];
			t[1][0] = v[8];
			t[1][1] = v[1];
			t[1][2] = v[11];
			t[2][0] = v[11];
			t[2][1] = v[1];
			t[2][2] = v[3];
			t[3][0] = v[10];
			t[3][1] = v[9];
			t[3][2] = v[5];
			break;
		case 0x6d:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[5];
			t[0][2] = v[9];
			t[1][0] = v[0];
			t[1][1] = v[1];
			t[1][2] = v[4];
			t[2][0] = v[8];
			t[2][1] = v[11];
			t[2][2] = v[7];
			break;
		case 0x6e:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[8];
			t[1][2] = v[11];
			t[1][0] = v[3];
			t[1][1] = v[0];
			t[1][2] = v[11];
			t[2][0] = v[10];
			t[2][1] = v[9];
			t[2][2] = v[5];
			break;
		case 0x6f:
			t = new double[2][3][3];
			t[0][0] = v[7];
			t[0][1] = v[8];
			t[0][2] = v[11];
			t[1][0] = v[10];
			t[1][1] = v[9];
			t[1][2] = v[5];
			break;
		case 0x70:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[4];
			t[0][2] = v[10];
			t[1][0] = v[4];
			t[1][1] = v[6];
			t[1][2] = v[10];
			t[2][0] = v[4];
			t[2][1] = v[7];
			t[2][2] = v[6];
			break;
		case 0x71:
			t = new double[4][3][3];
			t[0][0] = v[9];
			t[0][1] = v[4];
			t[0][2] = v[10];
			t[1][0] = v[10];
			t[1][1] = v[4];
			t[1][2] = v[6];
			t[2][0] = v[6];
			t[2][1] = v[4];
			t[2][2] = v[0];
			t[3][0] = v[6];
			t[3][1] = v[0];
			t[3][2] = v[3];
			break;
		case 0x73:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[6];
			t[0][2] = v[10];
			t[1][0] = v[9];
			t[1][1] = v[3];
			t[1][2] = v[10];
			t[2][0] = v[1];
			t[2][1] = v[3];
			t[2][2] = v[9];
			break;
		case 0x74:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[9];
			t[0][2] = v[7];
			t[1][0] = v[7];
			t[1][1] = v[9];
			t[1][2] = v[4];
			t[2][0] = v[3];
			t[2][1] = v[2];
			t[2][2] = v[7];
			break;
		case 0x75:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[9];
			t[0][2] = v[4];
			t[1][0] = v[0];
			t[1][1] = v[10];
			t[1][2] = v[4];
			t[2][0] = v[2];
			t[2][1] = v[10];
			t[2][2] = v[0];
			break;
		case 0x76:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[1];
			t[1][2] = v[2];
			t[1][0] = v[10];
			t[1][1] = v[9];
			t[1][2] = v[2];
			t[2][0] = v[3];
			t[2][1] = v[0];
			t[2][2] = v[7];
			break;
		case 0x77:
			t = new double[2][3][3];
			t[0][0] = v[2];
			t[0][1] = v[10];
			t[0][2] = v[9];
			t[1][0] = v[1];
			t[1][1] = v[2];
			t[1][2] = v[9];
			break;
		case 0x78:
			t = new double[4][3][3];
			t[0][0] = v[1];
			t[0][1] = v[2];
			t[0][2] = v[6];
			t[1][0] = v[1];
			t[1][1] = v[6];
			t[1][2] = v[4];
			t[2][0] = v[4];
			t[2][1] = v[6];
			t[2][2] = v[7];
			t[3][0] = v[9];
			t[3][1] = v[5];
			t[3][2] = v[10];
			break;
		case 0x79:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[4];
			t[0][2] = v[1];
			t[1][0] = v[10];
			t[1][1] = v[9];
			t[1][2] = v[5];
			t[2][0] = v[2];
			t[2][1] = v[3];
			t[2][2] = v[6];
			break;
		case 0x7a:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[6];
			t[1][2] = v[2];
			t[1][0] = v[0];
			t[1][1] = v[7];
			t[1][2] = v[2];
			t[2][0] = v[5];
			t[2][1] = v[10];
			t[2][2] = v[9];
			break;
		case 0x7b:
			t = new double[2][3][3];
			t[0][0] = v[3];
			t[0][1] = v[6];
			t[0][2] = v[2];
			t[1][0] = v[5];
			t[1][1] = v[10];
			t[1][2] = v[9];
			break;
		case 0x7c:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[1];
			t[1][2] = v[4];
			t[1][0] = v[7];
			t[1][1] = v[3];
			t[1][2] = v[4];
			t[2][0] = v[9];
			t[2][1] = v[5];
			t[2][2] = v[10];
			break;
		case 0x7d:
			t = new double[2][3][3];
			t[0][0] = v[0];
			t[0][1] = v[1];
			t[0][2] = v[4];
			t[1][0] = v[9];
			t[1][1] = v[5];
			t[1][2] = v[10];
			break;
		case 0x7e:
			t = new double[2][3][3];
			t[0][0] = v[0];
			t[0][1] = v[7];
			t[0][2] = v[3];
			t[1][0] = v[10];
			t[1][1] = v[9];
			t[1][2] = v[5];
			break;
		case 0x7f:
			t = new double[1][3][3];
			t[0][0] = v[5];
			t[0][1] = v[10];
			t[0][2] = v[9];
			break;
		case 0x80:
			t = new double[1][3][3];
			t[0][0] = v[10];
			t[0][1] = v[5];
			t[0][2] = v[9];
			break;
		case 0x81:
			t = new double[2][3][3];
			t[0][0] = v[7];
			t[0][1] = v[0];
			t[0][2] = v[3];
			t[1][0] = v[9];
			t[1][1] = v[10];
			t[1][2] = v[5];
			break;
		case 0x82:
			t = new double[2][3][3];
			t[0][0] = v[1];
			t[0][1] = v[0];
			t[0][2] = v[4];
			t[1][0] = v[5];
			t[1][1] = v[9];
			t[1][2] = v[10];
			break;
		case 0x83:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[3];
			t[1][2] = v[4];
			t[1][0] = v[3];
			t[1][1] = v[7];
			t[1][2] = v[4];
			t[2][0] = v[5];
			t[2][1] = v[9];
			t[2][2] = v[10];
			break;
		case 0x84:
			t = new double[2][3][3];
			t[0][0] = v[6];
			t[0][1] = v[3];
			t[0][2] = v[2];
			t[1][0] = v[10];
			t[1][1] = v[5];
			t[1][2] = v[9];
			break;
		case 0x85:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[7];
			t[1][2] = v[2];
			t[1][0] = v[7];
			t[1][1] = v[0];
			t[1][2] = v[2];
			t[2][0] = v[10];
			t[2][1] = v[5];
			t[2][2] = v[9];
			break;
		case 0x86:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[0];
			t[0][2] = v[1];
			t[1][0] = v[9];
			t[1][1] = v[10];
			t[1][2] = v[5];
			t[2][0] = v[3];
			t[2][1] = v[2];
			t[2][2] = v[6];
			break;
		case 0x87:
			t = new double[4][3][3];
			t[0][0] = v[10];
			t[0][1] = v[9];
			t[0][2] = v[4];
			t[1][0] = v[10];
			t[1][1] = v[4];
			t[1][2] = v[6];
			t[2][0] = v[6];
			t[2][1] = v[4];
			t[2][2] = v[7];
			t[3][0] = v[2];
			t[3][1] = v[5];
			t[3][2] = v[1];
			break;
		case 0x88:
			t = new double[2][3][3];
			t[0][0] = v[10];
			t[0][1] = v[2];
			t[0][2] = v[9];
			t[1][0] = v[2];
			t[1][1] = v[1];
			t[1][2] = v[9];
			break;
		case 0x89:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[9];
			t[1][2] = v[2];
			t[1][0] = v[9];
			t[1][1] = v[10];
			t[1][2] = v[2];
			t[2][0] = v[0];
			t[2][1] = v[3];
			t[2][2] = v[7];
			break;
		case 0x8a:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[10];
			t[0][2] = v[4];
			t[1][0] = v[10];
			t[1][1] = v[0];
			t[1][2] = v[4];
			t[2][0] = v[10];
			t[2][1] = v[2];
			t[2][2] = v[0];
			break;
		case 0x8b:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[3];
			t[0][2] = v[4];
			t[1][0] = v[4];
			t[1][1] = v[3];
			t[1][2] = v[7];
			t[2][0] = v[9];
			t[2][1] = v[10];
			t[2][2] = v[4];
			break;
		case 0x8c:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[3];
			t[0][2] = v[10];
			t[1][0] = v[3];
			t[1][1] = v[9];
			t[1][2] = v[10];
			t[2][0] = v[3];
			t[2][1] = v[1];
			t[2][2] = v[9];
			break;
		case 0x8e:
			t = new double[4][3][3];
			t[0][0] = v[0];
			t[0][1] = v[4];
			t[0][2] = v[3];
			t[1][0] = v[3];
			t[1][1] = v[4];
			t[1][2] = v[6];
			t[2][0] = v[6];
			t[2][1] = v[4];
			t[2][2] = v[9];
			t[3][0] = v[6];
			t[3][1] = v[9];
			t[3][2] = v[10];
			break;
		case 0x8f:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[9];
			t[0][2] = v[10];
			t[1][0] = v[6];
			t[1][1] = v[4];
			t[1][2] = v[10];
			t[2][0] = v[7];
			t[2][1] = v[4];
			t[2][2] = v[6];
			break;
		case 0x90:
			t = new double[2][3][3];
			t[0][0] = v[8];
			t[0][1] = v[7];
			t[0][2] = v[11];
			t[1][0] = v[9];
			t[1][1] = v[10];
			t[1][2] = v[5];
			break;
		case 0x91:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[0];
			t[1][2] = v[11];
			t[1][0] = v[0];
			t[1][1] = v[3];
			t[1][2] = v[11];
			t[2][0] = v[9];
			t[2][1] = v[10];
			t[2][2] = v[5];
			break;
		case 0x92:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[10];
			t[0][2] = v[9];
			t[1][0] = v[1];
			t[1][1] = v[0];
			t[1][2] = v[4];
			t[2][0] = v[11];
			t[2][1] = v[8];
			t[2][2] = v[7];
			break;
		case 0x93:
			t = new double[4][3][3];
			t[0][0] = v[5];
			t[0][1] = v[10];
			t[0][2] = v[11];
			t[1][0] = v[5];
			t[1][1] = v[11];
			t[1][2] = v[1];
			t[2][0] = v[1];
			t[2][1] = v[11];
			t[2][2] = v[3];
			t[3][0] = v[4];
			t[3][1] = v[9];
			t[3][2] = v[8];
			break;
		case 0x94:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[8];
			t[0][2] = v[11];
			t[1][0] = v[3];
			t[1][1] = v[2];
			t[1][2] = v[6];
			t[2][0] = v[9];
			t[2][1] = v[10];
			t[2][2] = v[5];
			break;
		case 0x95:
			t = new double[4][3][3];
			t[0][0] = v[9];
			t[0][1] = v[5];
			t[0][2] = v[2];
			t[1][0] = v[9];
			t[1][1] = v[2];
			t[1][2] = v[8];
			t[2][0] = v[8];
			t[2][1] = v[2];
			t[2][2] = v[0];
			t[3][0] = v[11];
			t[3][1] = v[10];
			t[3][2] = v[6];
			break;
		case 0x96:
			t = new double[4][3][3];
			t[0][0] = v[0];
			t[0][1] = v[7];
			t[0][2] = v[3];
			t[1][0] = v[9];
			t[1][1] = v[8];
			t[1][2] = v[11];
			t[2][0] = v[6];
			t[2][1] = v[11];
			t[2][2] = v[10];
			t[3][0] = v[5];
			t[3][1] = v[1];
			t[3][2] = v[2];
			break;
		case 0x97:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[6];
			t[0][2] = v[10];
			t[1][0] = v[1];
			t[1][1] = v[2];
			t[1][2] = v[5];
			t[2][0] = v[9];
			t[2][1] = v[8];
			t[2][2] = v[4];
			break;
		case 0x98:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[2];
			t[1][2] = v[9];
			t[1][0] = v[2];
			t[1][1] = v[1];
			t[1][2] = v[9];
			t[2][0] = v[11];
			t[2][1] = v[8];
			t[2][2] = v[7];
			break;
		case 0x99:
			t = new double[4][3][3];
			t[0][0] = v[0];
			t[0][1] = v[1];
			t[0][2] = v[9];
			t[1][0] = v[8];
			t[1][1] = v[0];
			t[1][2] = v[9];
			t[2][0] = v[10];
			t[2][1] = v[2];
			t[2][2] = v[5];
			t[3][0] = v[10];
			t[3][1] = v[5];
			t[3][2] = v[11];
			break;
		case 0x9a:
			t = new double[4][3][3];
			t[0][0] = v[11];
			t[0][1] = v[7];
			t[0][2] = v[0];
			t[1][0] = v[11];
			t[1][1] = v[0];
			t[1][2] = v[10];
			t[2][0] = v[10];
			t[2][1] = v[0];
			t[2][2] = v[2];
			t[3][0] = v[9];
			t[3][1] = v[8];
			t[3][2] = v[4];
			break;
		case 0x9b:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[11];
			t[1][2] = v[10];
			t[1][0] = v[2];
			t[1][1] = v[3];
			t[1][2] = v[10];
			t[2][0] = v[9];
			t[2][1] = v[8];
			t[2][2] = v[4];
			break;
		case 0x9c:
			t = new double[4][3][3];
			t[0][0] = v[7];
			t[0][1] = v[8];
			t[0][2] = v[9];
			t[1][0] = v[7];
			t[1][1] = v[9];
			t[1][2] = v[3];
			t[2][0] = v[3];
			t[2][1] = v[9];
			t[2][2] = v[1];
			t[3][0] = v[6];
			t[3][1] = v[11];
			t[3][2] = v[10];
			break;
		case 0x9d:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[9];
			t[1][2] = v[8];
			t[1][0] = v[0];
			t[1][1] = v[1];
			t[1][2] = v[8];
			t[2][0] = v[11];
			t[2][1] = v[10];
			t[2][2] = v[6];
			break;
		case 0x9e:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[4];
			t[0][2] = v[8];
			t[1][0] = v[3];
			t[1][1] = v[0];
			t[1][2] = v[7];
			t[2][0] = v[11];
			t[2][1] = v[10];
			t[2][2] = v[6];
			break;
		case 0x9f:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[9];
			t[0][2] = v[8];
			t[1][0] = v[11];
			t[1][1] = v[10];
			t[1][2] = v[6];
			break;
		case 0xa0:
			t = new double[2][3][3];
			t[0][0] = v[5];
			t[0][1] = v[4];
			t[0][2] = v[10];
			t[1][0] = v[4];
			t[1][1] = v[8];
			t[1][2] = v[10];
			break;
		case 0xa1:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[10];
			t[1][2] = v[4];
			t[1][0] = v[10];
			t[1][1] = v[5];
			t[1][2] = v[4];
			t[2][0] = v[7];
			t[2][1] = v[0];
			t[2][2] = v[3];
			break;
		case 0xa2:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[0];
			t[0][2] = v[5];
			t[1][0] = v[0];
			t[1][1] = v[10];
			t[1][2] = v[5];
			t[2][0] = v[0];
			t[2][1] = v[8];
			t[2][2] = v[10];
			break;
		case 0xa4:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[4];
			t[1][2] = v[10];
			t[1][0] = v[4];
			t[1][1] = v[8];
			t[1][2] = v[10];
			t[2][0] = v[2];
			t[2][1] = v[6];
			t[2][2] = v[3];
			break;
		case 0xa5:
			t = new double[4][3][3];
			t[0][0] = v[2];
			t[0][1] = v[5];
			t[0][2] = v[4];
			t[1][0] = v[0];
			t[1][1] = v[2];
			t[1][2] = v[4];
			t[2][0] = v[8];
			t[2][1] = v[10];
			t[2][2] = v[9];
			t[3][0] = v[8];
			t[3][1] = v[9];
			t[3][2] = v[7];
			break;
		case 0xa6:
			t = new double[4][3][3];
			t[0][0] = v[3];
			t[0][1] = v[6];
			t[0][2] = v[10];
			t[1][0] = v[3];
			t[1][1] = v[10];
			t[1][2] = v[0];
			t[2][0] = v[0];
			t[2][1] = v[10];
			t[2][2] = v[8];
			t[3][0] = v[1];
			t[3][1] = v[2];
			t[3][2] = v[5];
			break;
		case 0xa7:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[10];
			t[1][2] = v[6];
			t[1][0] = v[7];
			t[1][1] = v[8];
			t[1][2] = v[6];
			t[2][0] = v[2];
			t[2][1] = v[5];
			t[2][2] = v[1];
			break;
		case 0xa8:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[8];
			t[0][2] = v[1];
			t[1][0] = v[8];
			t[1][1] = v[2];
			t[1][2] = v[1];
			t[2][0] = v[8];
			t[2][1] = v[10];
			t[2][2] = v[2];
			break;
		case 0xa9:
			t = new double[4][3][3];
			t[0][0] = v[7];
			t[0][1] = v[3];
			t[0][2] = v[2];
			t[1][0] = v[7];
			t[1][1] = v[2];
			t[1][2] = v[8];
			t[2][0] = v[8];
			t[2][1] = v[2];
			t[2][2] = v[10];
			t[3][0] = v[4];
			t[3][1] = v[0];
			t[3][2] = v[1];
			break;
		case 0xaa:
			t = new double[2][3][3];
			t[0][0] = v[10];
			t[0][1] = v[2];
			t[0][2] = v[8];
			t[1][0] = v[8];
			t[1][1] = v[2];
			t[1][2] = v[0];
			break;
		case 0xab:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[3];
			t[0][2] = v[7];
			t[1][0] = v[8];
			t[1][1] = v[2];
			t[1][2] = v[7];
			t[2][0] = v[10];
			t[2][1] = v[2];
			t[2][2] = v[8];
			break;
		case 0xac:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[6];
			t[0][2] = v[1];
			t[1][0] = v[1];
			t[1][1] = v[6];
			t[1][2] = v[3];
			t[2][0] = v[4];
			t[2][1] = v[8];
			t[2][2] = v[1];
			break;
		case 0xad:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[7];
			t[1][2] = v[8];
			t[1][0] = v[10];
			t[1][1] = v[6];
			t[1][2] = v[8];
			t[2][0] = v[4];
			t[2][1] = v[0];
			t[2][2] = v[1];
			break;
		case 0xae:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[6];
			t[0][2] = v[3];
			t[1][0] = v[0];
			t[1][1] = v[10];
			t[1][2] = v[3];
			t[2][0] = v[8];
			t[2][1] = v[10];
			t[2][2] = v[0];
			break;
		case 0xaf:
			t = new double[2][3][3];
			t[0][0] = v[6];
			t[0][1] = v[7];
			t[0][2] = v[8];
			t[1][0] = v[10];
			t[1][1] = v[6];
			t[1][2] = v[8];
			break;
		case 0xb0:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[5];
			t[0][2] = v[11];
			t[1][0] = v[5];
			t[1][1] = v[7];
			t[1][2] = v[11];
			t[2][0] = v[5];
			t[2][1] = v[4];
			t[2][2] = v[7];
			break;
		case 0xb1:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[10];
			t[0][2] = v[4];
			t[1][0] = v[4];
			t[1][1] = v[10];
			t[1][2] = v[5];
			t[2][0] = v[0];
			t[2][1] = v[3];
			t[2][2] = v[4];
			break;
		case 0xb2:
			t = new double[4][3][3];
			t[0][0] = v[10];
			t[0][1] = v[5];
			t[0][2] = v[11];
			t[1][0] = v[11];
			t[1][1] = v[5];
			t[1][2] = v[7];
			t[2][0] = v[7];
			t[2][1] = v[5];
			t[2][2] = v[1];
			t[3][0] = v[7];
			t[3][1] = v[1];
			t[3][2] = v[0];
			break;
		case 0xb3:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[10];
			t[0][2] = v[5];
			t[1][0] = v[1];
			t[1][1] = v[11];
			t[1][2] = v[5];
			t[2][0] = v[3];
			t[2][1] = v[11];
			t[2][2] = v[1];
			break;
		case 0xb4:
			t = new double[4][3][3];
			t[0][0] = v[2];
			t[0][1] = v[3];
			t[0][2] = v[7];
			t[1][0] = v[2];
			t[1][1] = v[7];
			t[1][2] = v[5];
			t[2][0] = v[5];
			t[2][1] = v[7];
			t[2][2] = v[4];
			t[3][0] = v[10];
			t[3][1] = v[6];
			t[3][2] = v[11];
			break;
		case 0xb5:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[2];
			t[1][2] = v[5];
			t[1][0] = v[4];
			t[1][1] = v[0];
			t[1][2] = v[5];
			t[2][0] = v[10];
			t[2][1] = v[6];
			t[2][2] = v[11];
			break;
		case 0xb6:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[5];
			t[0][2] = v[2];
			t[1][0] = v[11];
			t[1][1] = v[10];
			t[1][2] = v[6];
			t[2][0] = v[3];
			t[2][1] = v[0];
			t[2][2] = v[7];
			break;
		case 0xb7:
			t = new double[2][3][3];
			t[0][0] = v[1];
			t[0][1] = v[2];
			t[0][2] = v[5];
			t[1][0] = v[10];
			t[1][1] = v[6];
			t[1][2] = v[11];
			break;
		case 0xb9:
			t = new double[3][3][3];
			t[0][0] = v[10];
			t[0][1] = v[2];
			t[1][2] = v[3];
			t[1][0] = v[11];
			t[1][1] = v[10];
			t[1][2] = v[3];
			t[2][0] = v[0];
			t[2][1] = v[1];
			t[2][2] = v[4];
			break;
		case 0xba:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[7];
			t[0][2] = v[11];
			t[1][0] = v[10];
			t[1][1] = v[0];
			t[1][2] = v[11];
			t[2][0] = v[2];
			t[2][1] = v[0];
			t[2][2] = v[10];
			break;
		case 0xbb:
			t = new double[2][3][3];
			t[0][0] = v[3];
			t[0][1] = v[11];
			t[0][2] = v[10];
			t[1][0] = v[2];
			t[1][1] = v[3];
			t[1][2] = v[10];
			break;
		case 0xbc:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[7];
			t[1][2] = v[3];
			t[1][0] = v[1];
			t[1][1] = v[4];
			t[1][2] = v[3];
			t[2][0] = v[6];
			t[2][1] = v[11];
			t[2][2] = v[10];
			break;
		case 0xbd:
			t = new double[2][3][3];
			t[0][0] = v[1];
			t[0][1] = v[4];
			t[0][2] = v[0];
			t[1][0] = v[11];
			t[1][1] = v[10];
			t[1][2] = v[6];
			break;
		case 0xbe:
			t = new double[2][3][3];
			t[0][0] = v[0];
			t[0][1] = v[7];
			t[0][2] = v[3];
			t[1][0] = v[6];
			t[1][1] = v[11];
			t[1][2] = v[10];
			break;
		case 0xbf:
			t = new double[1][3][3];
			t[0][0] = v[6];
			t[0][1] = v[11];
			t[0][2] = v[10];
			break;
		case 0xc0:
			t = new double[2][3][3];
			t[0][0] = v[6];
			t[0][1] = v[5];
			t[0][2] = v[11];
			t[1][0] = v[5];
			t[1][1] = v[9];
			t[1][2] = v[11];
			break;
		case 0xc1:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[5];
			t[1][2] = v[11];
			t[1][0] = v[5];
			t[1][1] = v[9];
			t[1][2] = v[11];
			t[2][0] = v[3];
			t[2][1] = v[7];
			t[2][2] = v[0];
			break;
		case 0xc2:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[11];
			t[1][2] = v[5];
			t[1][0] = v[11];
			t[1][1] = v[6];
			t[1][2] = v[5];
			t[2][0] = v[4];
			t[2][1] = v[1];
			t[2][2] = v[0];
			break;
		case 0xc3:
			t = new double[4][3][3];
			t[0][0] = v[3];
			t[0][1] = v[6];
			t[0][2] = v[5];
			t[1][0] = v[1];
			t[1][1] = v[3];
			t[1][2] = v[5];
			t[2][0] = v[9];
			t[2][1] = v[11];
			t[2][2] = v[10];
			t[3][0] = v[9];
			t[3][1] = v[10];
			t[3][2] = v[4];
			break;
		case 0xc4:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[9];
			t[0][2] = v[2];
			t[1][0] = v[9];
			t[1][1] = v[3];
			t[1][2] = v[2];
			t[2][0] = v[9];
			t[2][1] = v[11];
			t[2][2] = v[3];
			break;
		case 0xc5:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[7];
			t[0][2] = v[2];
			t[1][0] = v[2];
			t[1][1] = v[7];
			t[1][2] = v[0];
			t[2][0] = v[5];
			t[2][1] = v[9];
			t[2][2] = v[2];
			break;
		case 0xc6:
			t = new double[4][3][3];
			t[0][0] = v[4];
			t[0][1] = v[0];
			t[0][2] = v[3];
			t[1][0] = v[4];
			t[1][1] = v[3];
			t[1][2] = v[9];
			t[2][0] = v[9];
			t[2][1] = v[3];
			t[2][2] = v[11];
			t[3][0] = v[5];
			t[3][1] = v[1];
			t[3][2] = v[2];
			break;
		case 0xc7:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[4];
			t[1][2] = v[9];
			t[1][0] = v[11];
			t[1][1] = v[7];
			t[1][2] = v[9];
			t[2][0] = v[5];
			t[2][1] = v[1];
			t[2][2] = v[2];
			break;
		case 0xc8:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[1];
			t[0][2] = v[6];
			t[1][0] = v[1];
			t[1][1] = v[11];
			t[1][2] = v[6];
			t[2][0] = v[1];
			t[2][1] = v[9];
			t[2][2] = v[11];
			break;
		case 0xc9:
			t = new double[4][3][3];
			t[0][0] = v[0];
			t[0][1] = v[7];
			t[0][2] = v[11];
			t[1][0] = v[0];
			t[1][1] = v[11];
			t[1][2] = v[1];
			t[2][0] = v[1];
			t[2][1] = v[11];
			t[2][2] = v[9];
			t[3][0] = v[2];
			t[3][1] = v[3];
			t[3][2] = v[6];
			break;
		case 0xcb:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[11];
			t[1][2] = v[7];
			t[1][0] = v[4];
			t[1][1] = v[9];
			t[1][2] = v[7];
			t[2][0] = v[3];
			t[2][1] = v[6];
			t[2][2] = v[2];
			break;
		case 0xcc:
			t = new double[2][3][3];
			t[0][0] = v[11];
			t[0][1] = v[3];
			t[0][2] = v[9];
			t[1][0] = v[9];
			t[1][1] = v[3];
			t[1][2] = v[1];
			break;
		case 0xcd:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[7];
			t[0][2] = v[0];
			t[1][0] = v[1];
			t[1][1] = v[11];
			t[1][2] = v[0];
			t[2][0] = v[9];
			t[2][1] = v[11];
			t[2][2] = v[1];
			break;
		case 0xce:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[0];
			t[0][2] = v[4];
			t[1][0] = v[9];
			t[1][1] = v[3];
			t[1][2] = v[4];
			t[2][0] = v[11];
			t[2][1] = v[3];
			t[2][2] = v[9];
			break;
		case 0xcf:
			t = new double[2][3][3];
			t[0][0] = v[7];
			t[0][1] = v[4];
			t[0][2] = v[9];
			t[1][0] = v[11];
			t[1][1] = v[7];
			t[1][2] = v[9];
			break;
		case 0xd0:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[7];
			t[0][2] = v[9];
			t[1][0] = v[7];
			t[1][1] = v[5];
			t[1][2] = v[9];
			t[2][0] = v[7];
			t[2][1] = v[6];
			t[2][2] = v[5];
			break;
		case 0xd2:
			t = new double[4][3][3];
			t[0][0] = v[0];
			t[0][1] = v[1];
			t[0][2] = v[5];
			t[1][0] = v[0];
			t[1][1] = v[5];
			t[1][2] = v[7];
			t[2][0] = v[7];
			t[2][1] = v[5];
			t[2][2] = v[6];
			t[3][0] = v[8];
			t[3][1] = v[4];
			t[3][2] = v[9];
			break;
		case 0xd3:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[5];
			t[1][2] = v[1];
			t[1][0] = v[3];
			t[1][1] = v[6];
			t[1][2] = v[1];
			t[2][0] = v[4];
			t[2][1] = v[9];
			t[2][2] = v[8];
			break;
		case 0xd4:
			t = new double[4][3][3];
			t[0][0] = v[8];
			t[0][1] = v[7];
			t[0][2] = v[9];
			t[1][0] = v[9];
			t[1][1] = v[7];
			t[1][2] = v[5];
			t[2][0] = v[5];
			t[2][1] = v[7];
			t[2][2] = v[3];
			t[3][0] = v[5];
			t[3][1] = v[3];
			t[3][2] = v[2];
			break;
		case 0xd5:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[5];
			t[0][2] = v[9];
			t[1][0] = v[8];
			t[1][1] = v[2];
			t[1][2] = v[9];
			t[2][0] = v[0];
			t[2][1] = v[2];
			t[2][2] = v[8];
			break;
		case 0xd6:
			t = new double[3][3][3];
			t[0][0] = v[3];
			t[0][1] = v[7];
			t[0][2] = v[0];
			t[1][0] = v[9];
			t[1][1] = v[8];
			t[1][2] = v[4];
			t[2][0] = v[1];
			t[2][1] = v[2];
			t[2][2] = v[5];
			break;
		case 0xd7:
			t = new double[2][3][3];
			t[0][0] = v[2];
			t[0][1] = v[5];
			t[0][2] = v[1];
			t[1][0] = v[4];
			t[1][1] = v[9];
			t[1][2] = v[8];
			break;
		case 0xd8:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[8];
			t[0][2] = v[6];
			t[1][0] = v[6];
			t[1][1] = v[8];
			t[1][2] = v[7];
			t[2][0] = v[2];
			t[2][1] = v[1];
			t[2][2] = v[6];
			break;
		case 0xd9:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[0];
			t[1][2] = v[1];
			t[1][0] = v[9];
			t[1][1] = v[8];
			t[1][2] = v[1];
			t[2][0] = v[2];
			t[2][1] = v[3];
			t[2][2] = v[6];
			break;
		case 0xda:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[0];
			t[1][2] = v[7];
			t[1][0] = v[6];
			t[1][1] = v[2];
			t[1][2] = v[7];
			t[2][0] = v[8];
			t[2][1] = v[4];
			t[2][2] = v[9];
			break;
		case 0xdb:
			t = new double[2][3][3];
			t[0][0] = v[3];
			t[0][1] = v[6];
			t[0][2] = v[2];
			t[1][0] = v[9];
			t[1][1] = v[8];
			t[1][2] = v[4];
			break;
		case 0xdc:
			t = new double[3][3][3];
			t[0][0] = v[9];
			t[0][1] = v[8];
			t[0][2] = v[7];
			t[1][0] = v[3];
			t[1][1] = v[9];
			t[1][2] = v[7];
			t[2][0] = v[1];
			t[2][1] = v[9];
			t[2][2] = v[3];
			break;
		case 0xdd:
			t = new double[2][3][3];
			t[0][0] = v[1];
			t[0][1] = v[9];
			t[0][2] = v[8];
			t[1][0] = v[0];
			t[1][1] = v[1];
			t[1][2] = v[8];
			break;
		case 0xde:
			t = new double[2][3][3];
			t[0][0] = v[3];
			t[0][1] = v[0];
			t[0][2] = v[7];
			t[1][0] = v[8];
			t[1][1] = v[4];
			t[1][2] = v[9];
			break;
		case 0xdf:
			t = new double[1][3][3];
			t[0][0] = v[4];
			t[0][1] = v[9];
			t[0][2] = v[8];
			break;
		case 0xe0:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[6];
			t[0][2] = v[8];
			t[1][0] = v[6];
			t[1][1] = v[4];
			t[1][2] = v[8];
			t[2][0] = v[6];
			t[2][1] = v[5];
			t[2][2] = v[4];
			break;
		case 0xe1:
			t = new double[4][3][3];
			t[0][0] = v[3];
			t[0][1] = v[0];
			t[0][2] = v[4];
			t[1][0] = v[3];
			t[1][1] = v[4];
			t[1][2] = v[6];
			t[2][0] = v[6];
			t[2][1] = v[4];
			t[2][2] = v[5];
			t[3][0] = v[11];
			t[3][1] = v[7];
			t[3][2] = v[8];
			break;
		case 0xe2:
			t = new double[3][3][3];
			t[0][0] = v[0];
			t[0][1] = v[11];
			t[0][2] = v[5];
			t[1][0] = v[5];
			t[1][1] = v[11];
			t[1][2] = v[6];
			t[2][0] = v[1];
			t[2][1] = v[0];
			t[2][2] = v[5];
			break;
		case 0xe3:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[3];
			t[1][2] = v[6];
			t[1][0] = v[5];
			t[1][1] = v[1];
			t[1][2] = v[6];
			t[2][0] = v[11];
			t[2][1] = v[7];
			t[2][2] = v[8];
			break;
		case 0xe5:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[4];
			t[1][2] = v[0];
			t[1][0] = v[2];
			t[1][1] = v[5];
			t[1][2] = v[0];
			t[2][0] = v[7];
			t[2][1] = v[8];
			t[2][2] = v[11];
			break;
		case 0xe6:
			t = new double[3][3][3];
			t[0][0] = v[11];
			t[0][1] = v[3];
			t[1][2] = v[0];
			t[1][0] = v[8];
			t[1][1] = v[11];
			t[1][2] = v[0];
			t[2][0] = v[1];
			t[2][1] = v[2];
			t[2][2] = v[5];
			break;
		case 0xe7:
			t = new double[2][3][3];
			t[0][0] = v[2];
			t[0][1] = v[5];
			t[0][2] = v[1];
			t[1][0] = v[8];
			t[1][1] = v[11];
			t[1][2] = v[7];
			break;
		case 0xe8:
			t = new double[4][3][3];
			t[0][0] = v[11];
			t[0][1] = v[6];
			t[0][2] = v[8];
			t[1][0] = v[8];
			t[1][1] = v[6];
			t[1][2] = v[4];
			t[2][0] = v[4];
			t[2][1] = v[6];
			t[2][2] = v[2];
			t[3][0] = v[4];
			t[3][1] = v[2];
			t[3][2] = v[1];
			break;
		case 0xe9:
			t = new double[3][3][3];
			t[0][0] = v[2];
			t[0][1] = v[6];
			t[0][2] = v[3];
			t[1][0] = v[8];
			t[1][1] = v[11];
			t[1][2] = v[7];
			t[2][0] = v[0];
			t[2][1] = v[1];
			t[2][2] = v[4];
			break;
		case 0xea:
			t = new double[3][3][3];
			t[0][0] = v[8];
			t[0][1] = v[11];
			t[0][2] = v[6];
			t[1][0] = v[2];
			t[1][1] = v[8];
			t[1][2] = v[6];
			t[2][0] = v[0];
			t[2][1] = v[8];
			t[2][2] = v[2];
			break;
		case 0xeb:
			t = new double[2][3][3];
			t[0][0] = v[2];
			t[0][1] = v[3];
			t[0][2] = v[6];
			t[1][0] = v[11];
			t[1][1] = v[7];
			t[1][2] = v[8];
			break;
		case 0xec:
			t = new double[3][3][3];
			t[0][0] = v[1];
			t[0][1] = v[4];
			t[0][2] = v[8];
			t[1][0] = v[11];
			t[1][1] = v[1];
			t[1][2] = v[8];
			t[2][0] = v[3];
			t[2][1] = v[1];
			t[2][2] = v[11];
			break;
		case 0xed:
			t = new double[2][3][3];
			t[0][0] = v[1];
			t[0][1] = v[4];
			t[0][2] = v[0];
			t[1][0] = v[7];
			t[1][1] = v[8];
			t[1][2] = v[11];
			break;
		case 0xee:
			t = new double[2][3][3];
			t[0][0] = v[0];
			t[0][1] = v[8];
			t[0][2] = v[11];
			t[1][0] = v[3];
			t[1][1] = v[0];
			t[1][2] = v[11];
			break;
		case 0xef:
			t = new double[1][3][3];
			t[0][0] = v[7];
			t[0][1] = v[8];
			t[0][2] = v[11];
			break;
		case 0xf0:
			t = new double[2][3][3];
			t[0][0] = v[5];
			t[0][1] = v[4];
			t[0][2] = v[6];
			t[1][0] = v[6];
			t[1][1] = v[4];
			t[1][2] = v[7];
			break;
		case 0xf1:
			t = new double[3][3][3];
			t[0][0] = v[4];
			t[0][1] = v[0];
			t[0][2] = v[3];
			t[1][0] = v[6];
			t[1][1] = v[4];
			t[1][2] = v[3];
			t[2][0] = v[5];
			t[2][1] = v[4];
			t[2][2] = v[6];
			break;
		case 0xf2:
			t = new double[3][3][3];
			t[0][0] = v[5];
			t[0][1] = v[1];
			t[0][2] = v[0];
			t[1][0] = v[7];
			t[1][1] = v[5];
			t[1][2] = v[0];
			t[2][0] = v[6];
			t[2][1] = v[5];
			t[2][2] = v[7];
			break;
		case 0xf3:
			t = new double[2][3][3];
			t[0][0] = v[6];
			t[0][1] = v[5];
			t[0][2] = v[1];
			t[1][0] = v[3];
			t[1][1] = v[6];
			t[1][2] = v[1];
			break;
		case 0xf4:
			t = new double[3][3][3];
			t[0][0] = v[7];
			t[0][1] = v[3];
			t[0][2] = v[2];
			t[1][0] = v[5];
			t[1][1] = v[7];
			t[1][2] = v[2];
			t[2][0] = v[4];
			t[2][1] = v[7];
			t[2][2] = v[5];
			break;
		case 0xf5:
			t = new double[2][3][3];
			t[0][0] = v[5];
			t[0][1] = v[4];
			t[0][2] = v[0];
			t[1][0] = v[2];
			t[1][1] = v[5];
			t[1][2] = v[0];
			break;
		case 0xf6:
			t = new double[2][3][3];
			t[0][0] = v[5];
			t[0][1] = v[1];
			t[0][2] = v[2];
			t[1][0] = v[3];
			t[1][1] = v[0];
			t[1][2] = v[7];
			break;
		case 0xf7:
			t = new double[1][3][3];
			t[0][0] = v[2];
			t[0][1] = v[5];
			t[0][2] = v[1];
			break;
		case 0xf8:
			t = new double[3][3][3];
			t[0][0] = v[6];
			t[0][1] = v[2];
			t[0][2] = v[1];
			t[1][0] = v[4];
			t[1][1] = v[6];
			t[1][2] = v[1];
			t[2][0] = v[7];
			t[2][1] = v[6];
			t[2][2] = v[4];
			break;
		case 0xf9:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[0];
			t[0][2] = v[1];
			t[1][0] = v[2];
			t[1][1] = v[3];
			t[1][2] = v[6];
			break;
		case 0xfa:
			t = new double[2][3][3];
			t[0][0] = v[7];
			t[0][1] = v[6];
			t[0][2] = v[2];
			t[1][0] = v[0];
			t[1][1] = v[7];
			t[1][2] = v[2];
			break;
		case 0xfb:
			t = new double[1][3][3];
			t[0][0] = v[3];
			t[0][1] = v[6];
			t[0][2] = v[2];
			break;
		case 0xfc:
			t = new double[2][3][3];
			t[0][0] = v[4];
			t[0][1] = v[7];
			t[0][2] = v[3];
			t[1][0] = v[1];
			t[1][1] = v[4];
			t[1][2] = v[3];
			break;
		case 0xfd:
			t = new double[1][3][3];
			t[0][0] = v[1];
			t[0][1] = v[4];
			t[0][2] = v[0];
			break;
		case 0xfe:
			t = new double[1][3][3];
			t[0][0] = v[0];
			t[0][1] = v[7];
			t[0][2] = v[3];
			break;
		case 0xff:
			break;
		default:
			System.err.println("ERROR");
		}
		return t;
	}

	public void add(MCTriangle c) {
		float[] v = new float[9];
		float[] n = new float[9];
		v[0] = (float) c.corners[0].getX();
		v[1] = (float) c.corners[0].getY();
		v[2] = (float) c.corners[0].getZ();

		v[3] = (float) c.corners[1].getX();
		v[4] = (float) c.corners[1].getY();
		v[5] = (float) c.corners[1].getZ();

		v[6] = (float) c.corners[2].getX();
		v[7] = (float) c.corners[2].getY();
		v[8] = (float) c.corners[2].getZ();

		calcNormals(v, n);

		TriListElem el = add(v, n);
		el.setOwner(c);
		c.triListElem = el;
	}

	public void remove(MCTriangle t) {
		if (!remove(t.triListElem))
			System.out.print("");
	}

}

class MCAssigner implements BucketAssigner<DynamicMeshElement2> {

	public int getBucketIndex(Object o, int bucketAmt) {
		MCTriangle d = (MCTriangle) o;
		double e = d.error;
		int bucket = (int) (Math.pow(e, 0.1) * bucketAmt);
		if (bucket >= bucketAmt)
			bucket = bucketAmt - 1;
		if (bucket <= 0)
			bucket = 1;
		return bucket;
	}
}

/**
 * 
 * @author André Eriksson
 */
class MCROAM {
	private MCTriList triList;
	private FastBucketPQ pSplit = new FastBucketPQ(new MCAssigner(), false);

	MCROAM(MCTriList triList) {
		this.triList = triList;
		for (TriListElem t : triList) {
			MCTriangle tri = (MCTriangle) t.getOwner();
			pSplit.add(tri);
		}
	}

	int cntr = 0;

	/**
	 * Refines the mesh.
	 * @return true if the mesh is at an optimal level of detail, otherwise false
	 */
	public boolean refine() {
		int i = 0;
		while (tooCoarse() && i < 3) {
			split((MCTriangle) pSplit.poll(), null, null, null);
			i++;
		}
		if (i < 3)
			return true;
		return false;
	}

	/**
	 * Splits the triangle element
	 * @param a 
	 * @param b
	 * @param c0
	 * @param c1
	 */
	private void split(MCTriangle a, MCTriangle b, MCTriangle c0, MCTriangle c1) {

		if (a == null)
			return;

		// don't split an element that has already been split
		if (a.isSplit())
			return;

		// switch queues
		pSplit.remove(a);

		// mark as split
		a.setSplit(true);

		MCTriangle d, d0, d1;
		d = d0 = d1 = null;

		// subdivide
		if (b == a.neighbors[0] || b == null) {
			d0 = (MCTriangle) a.getChild(0);
			d1 = (MCTriangle) a.getChild(1);
		} else if (b == a.neighbors[1]) {
			Coords x1, x2, x3, x4;
			x1 = a.corners[1];
			x2 = a.corners[2];
			x3 = a.midpoint;
			x4 = MarchingCubes.project(x1.add(x2).mul(0.5), a.func);
			d = (MCTriangle) a.getChild(0);
			d0 = new MCTriangle(new double[][] {
					{ x3.getX(), x3.getY(), x3.getZ() },
					{ x1.getX(), x1.getY(), x1.getZ() },
					{ x4.getX(), x4.getY(), x4.getZ() } }, a.func);
			d1 = new MCTriangle(new double[][] {
					{ x3.getX(), x3.getY(), x3.getZ() },
					{ x4.getX(), x4.getY(), x4.getZ() },
					{ x2.getX(), x2.getY(), x2.getZ() } }, a.func);
		} else if (b == a.neighbors[2]) {
			Coords x1, x2, x3, x4;
			x1 = a.corners[0];
			x2 = a.corners[2];
			x3 = a.midpoint;
			x4 = MarchingCubes.project(x1.add(x2).mul(0.5), a.func);
			d = (MCTriangle) a.getChild(1);
			d0 = new MCTriangle(new double[][] {
					{ x4.getX(), x4.getY(), x4.getZ() },
					{ x3.getX(), x3.getY(), x3.getZ() },
					{ x2.getX(), x2.getY(), x2.getZ() } }, a.func);
			d1 = new MCTriangle(new double[][] {
					{ x1.getX(), x1.getY(), x1.getZ() },
					{ x3.getX(), x3.getY(), x3.getZ() },
					{ x4.getX(), x4.getY(), x4.getZ() } }, a.func);
		} else {
			System.out.println("error");
		}
		if (d != null) {
			triList.add(d);
			pSplit.add(d);
		}
		triList.add(d0);
		triList.add(d1);
		pSplit.add(d0);
		pSplit.add(d1);

		// link up
		if (b != null) {
			link(d0, c1);
			link(d1, c0);
			link(d1, d0);
			if (b == a.neighbors[0]) {
				if (a.neighbors[1] != null)
					a.neighbors[1].switchNeighbor(a, d1);
				if (a.neighbors[2] != null)
					a.neighbors[2].switchNeighbor(a, d0);
			} else {
				if (b == a.neighbors[1]) {
					if (a.neighbors[2] != null)
						a.neighbors[2].switchNeighbor(a, d);
					link(d1, d);
				} else {
					if (a.neighbors[1] != null)
						a.neighbors[1].switchNeighbor(a, d);
					link(d0, d);
				}
			}
			if (d != null)
				d.resort();
			if(d0!=null)
				d0.resort();
			if(c0!=null)
				c0.resort();
			if(d1!=null)
				d1.resort();
			if(c1!=null)
				c1.resort();
		} else {
			if (a.neighbors[1] != null)
				a.neighbors[1].switchNeighbor(a, d1);
			if (a.neighbors[2] != null)
				a.neighbors[2].switchNeighbor(a, d0);
			link(d0, d1);
		}

		// assert
		/*
		 * if(b==a.neighbors[0]){ verifyNeighbors(d0,a.neighbors[2]);
		 * verifyNeighbors(d1,a.neighbors[1]); verifyNeighbors(d1,c0);
		 * verifyNeighbors(c1,d0); verifyNeighbors(d1,d0);
		 * if(a.neighbors[1].hasNeighbor(a)) System.out.println("error");
		 * if(a.neighbors[2].hasNeighbor(a)) System.out.println("error"); } else
		 * if(b==a.neighbors[1]){ verifyNeighbors(d,d1); verifyNeighbors(d1,d0);
		 * verifyNeighbors(d0,c1); verifyNeighbors(d1,c0);
		 * if(!d0.hasNeighbor(c1)); if(a.neighbors[2]!=null){
		 * verifyNeighbors(d,a.neighbors[2]); if(a.neighbors[2].hasNeighbor(a))
		 * System.out.println("error"); } } else if(b==a.neighbors[2]){
		 * verifyNeighbors(d,d0); verifyNeighbors(d1,d0);
		 * verifyNeighbors(d0,c1); verifyNeighbors(d1,c0);
		 * if(a.neighbors[1]!=null){ verifyNeighbors(d,a.neighbors[1]);
		 * if(a.neighbors[1].hasNeighbor(a)) System.out.println("error"); } }
		 */

		// recurse
		if (b != a.neighbors[0]) {
			if (b == a.neighbors[1])
				split(a.neighbors[0], a, d, d0);
			else if (b == a.neighbors[2])
				split(a.neighbors[0], a, d1, d);
			else
				split(a.neighbors[0], a, d0, d1);
		}

		// remove from drawing list
		triList.remove(a);
	}

	@SuppressWarnings("unused")
	private void verifyNeighbors(MCTriangle t1, MCTriangle t2) {
		if (!t1.hasNeighbor(t2) || !t2.hasNeighbor(t1))
			System.out.println("error");
	}

	/**
	 * Links the triangle to two neighbors
	 * @param a
	 * @param b
	 */
	private void link(MCTriangle a, MCTriangle b) {
		if(a!=null)
			a.addNeighbor(b);
		if(b!=null)
			b.addNeighbor(a);
	}

	/**
	 * Determines if the level of detail is sufficient
	 * @return
	 */
	private boolean tooCoarse() {
		return (triList.getTriAmt() < 3000);
	}
}

/**
 * A variant of the Marching Cubes algorithm
 * @author André Eriksson 
 */
public class MarchingCubes {
	private int INITIAL_ELEMENTS = 2;
	private int REFINEMENTS = 3;

	/** masks corresponding to the corners */
	private final int C0 = 0x01;
	private final int C1 = 0x02;
	private final int C2 = 0x04;
	private final int C3 = 0x08;
	private final int C4 = 0x10;
	private final int C5 = 0x20;
	private final int C6 = 0x40;
	private final int C7 = 0x80;

	/** elements */
	LinkedList<MCElement> elems = new LinkedList<MCElement>();

	LinkedList<MCElement> allelems = new LinkedList<MCElement>();

	GeoFunctionNVar f;

	/** list of traingles, used for drawing */
	MCTriList drawList;

	/** viewing volume radius */
	double rad;

	/**
	 * @param fcn
	 * @param rad
	 */
	public MarchingCubes(GeoFunctionNVar fcn, double rad) {

		this.rad = rad;
		this.rad = 10;
		drawList = new MCTriList(fcn, 10000, 100);
		f = fcn;
		init();

		//refine a few times to get correct topology
		for(int i = 0; i < REFINEMENTS; i++)
			octreeRefine();
		
		render();

		// link up triangles
		for (MCTriangle t : tris) {
			t.setNeighbors(tris);
		}

		mcRoam = new MCROAM(drawList);
		mcRoam.refine();
	}

	private MCROAM mcRoam;

	/**
	 * Refines the mesh
	 * 
	 * @return false iff the desired LoD has been reached
	 */
	public boolean update() {
		return mcRoam.refine();
	}

	/**
	 * Projects a point onto the surface by using Newton's method in the
	 * gradient direction
	 * 
	 * @param c
	 *            the point to project
	 * @param f
	 *            the function used
	 * @return the projected point
	 */
	public static Coords project(Coords c, GeoFunctionNVar f) {
		// find the gradient direction
		double x = c.getX();
		double y = c.getY();
		double z = c.getZ();
		double delta = 1e-8;
		double val = f.evaluate(x, y, z);
		double dx = (f.evaluate(x + delta, y, z) - val);
		double dy = (f.evaluate(x, y + delta, z) - val);
		double dz = (f.evaluate(x, y, z + delta) - val);
		double sum = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
		double gx = dx / sum;
		double gy = dy / sum;
		double gz = dz / sum;

		// find a root in the gradient direction
		while (Math.abs(val) > 0.1) {
			double der = (f.evaluate(x + gx * delta, y + gy * delta, z + gz
					* delta) - val)
					/ delta;
			double mult = val / der;
			x -= gx * mult;
			y -= gy * mult;
			z -= gz * mult;
			val = f.evaluate(x, y, z);
		}

		Coords temp = new Coords(x, y, z);

		return temp;

	}

	Coords[][] t;

	public Coords[][] getSegments() {
		return t;
	}

	/**
	 * Generates a set of segments corresponding to the current octree. Useful
	 * for visualization purposes.
	 */
	private void createSegments() {
		ListIterator<MCElement> it = allelems.listIterator();
		t = new Coords[allelems.size() * 12][2];
		int i = 0;
		while (it.hasNext()) {
			double[][] c = it.next().cz;
			t[i][0] = new Coords(c[0]);
			t[i][1] = new Coords(c[1]);
			t[i + 1][0] = new Coords(c[1]);
			t[i + 1][1] = new Coords(c[3]);
			t[i + 2][0] = new Coords(c[3]);
			t[i + 2][1] = new Coords(c[2]);
			t[i + 3][0] = new Coords(c[2]);
			t[i + 3][1] = new Coords(c[0]);
			t[i + 4][0] = new Coords(c[0]);
			t[i + 4][1] = new Coords(c[4]);
			t[i + 5][0] = new Coords(c[5]);
			t[i + 5][1] = new Coords(c[1]);
			t[i + 6][0] = new Coords(c[3]);
			t[i + 6][1] = new Coords(c[7]);
			t[i + 7][0] = new Coords(c[2]);
			t[i + 7][1] = new Coords(c[6]);
			t[i + 8][0] = new Coords(c[4]);
			t[i + 8][1] = new Coords(c[5]);
			t[i + 9][0] = new Coords(c[5]);
			t[i + 9][1] = new Coords(c[7]);
			t[i + 10][0] = new Coords(c[6]);
			t[i + 10][1] = new Coords(c[7]);
			t[i + 11][0] = new Coords(c[6]);
			t[i + 11][1] = new Coords(c[4]);
			i += 12;
		}
	}

	/** the triangles in the octree */
	private LinkedList<MCTriangle> tris = new LinkedList<MCTriangle>();

	/**
	 * Generates triangles for the current octree
	 */
	private void render() {
		Iterator<MCElement> it = elems.iterator();
		while (it.hasNext()) {
			drawList.gen(it.next(), tris);
		}
	}

	/**
	 * Refines the octree one step (that is, splits each element once)
	 */
	private void octreeRefine() {
		Iterator<MCElement> it = elems.iterator();

		double[][][] t = new double[3][3][3];
		double[][] c = new double[3][2];
		double[] pc;
		int b, be;
		MCElement temp, elem;

		LinkedList<MCElement> tl = new LinkedList<MCElement>();

		while (it.hasNext()) {
			elem = it.next();

			be = elem.signs;
			if (be == 0x0 || be == 0xff)
				continue;

			pc = elem.cornerVals;

			c = elem.cornerParams;

			double minX = c[0][0];
			double maxX = c[0][1];
			double minY = c[1][0];
			double maxY = c[1][1];
			double minZ = c[2][0];
			double maxZ = c[2][1];
			double midX = 0.5f * (minX + maxX);
			double midY = 0.5f * (minY + maxY);
			double midZ = 0.5f * (minZ + maxZ);

			t[0][0][0] = pc[0];
			t[0][0][1] = f.evaluate(minX, minY, midZ);
			t[0][0][2] = pc[4];
			t[0][1][0] = f.evaluate(minX, midY, minZ);
			t[0][1][1] = f.evaluate(minX, midY, midZ);
			t[0][1][2] = f.evaluate(minX, midY, maxZ);
			t[0][2][0] = pc[2];
			t[0][2][1] = f.evaluate(minX, maxY, midZ);
			t[0][2][2] = pc[6];
			t[1][0][0] = f.evaluate(midX, minY, minZ);
			t[1][0][1] = f.evaluate(midX, minY, midZ);
			t[1][0][2] = f.evaluate(midX, minY, maxZ);
			t[1][1][0] = f.evaluate(midX, midY, minZ);
			t[1][1][1] = f.evaluate(midX, midY, midZ);
			t[1][1][2] = f.evaluate(midX, midY, maxZ);
			t[1][2][0] = f.evaluate(midX, maxY, minZ);
			t[1][2][1] = f.evaluate(midX, maxY, midZ);
			t[1][2][2] = f.evaluate(midX, maxY, maxZ);
			t[2][0][0] = pc[1];
			t[2][0][1] = f.evaluate(maxX, minY, midZ);
			t[2][0][2] = pc[5];
			t[2][1][0] = f.evaluate(maxX, midY, minZ);
			t[2][1][1] = f.evaluate(maxX, midY, midZ);
			t[2][1][2] = f.evaluate(maxX, midY, maxZ);
			t[2][2][0] = pc[3];
			t[2][2][1] = f.evaluate(maxX, maxY, midZ);
			t[2][2][2] = pc[7];

			// create subcubes in order corresponding to cube corners

			// corner 0
			c[0][0] = minX;
			c[0][1] = midX;
			c[1][0] = minY;
			c[1][1] = midY;
			c[2][0] = minZ;
			c[2][1] = midZ;

			b = 0;
			if (t[0][0][0] < 0)
				b |= C0;
			if (t[1][0][0] < 0)
				b |= C1;
			if (t[0][1][0] < 0)
				b |= C2;
			if (t[1][1][0] < 0)
				b |= C3;
			if (t[0][0][1] < 0)
				b |= C4;
			if (t[1][0][1] < 0)
				b |= C5;
			if (t[0][1][1] < 0)
				b |= C6;
			if (t[1][1][1] < 0)
				b |= C7;

			if (b != 0 && b != 0xff) {
				double[] d = { t[0][0][0], t[1][0][0], t[0][1][0], t[1][1][0],
						t[0][0][1], t[1][0][1], t[0][1][1], t[1][1][1] };
				tl.add(new MCElement(b, c, d));
			}

			// corner 1
			c[0][0] = midX;
			c[0][1] = maxX;
			c[1][0] = minY;
			c[1][1] = midY;
			c[2][0] = minZ;
			c[2][1] = midZ;

			b = 0;
			if (t[1][0][0] < 0)
				b |= C0;
			if (t[2][0][0] < 0)
				b |= C1;
			if (t[1][1][0] < 0)
				b |= C2;
			if (t[2][1][0] < 0)
				b |= C3;
			if (t[1][0][1] < 0)
				b |= C4;
			if (t[2][0][1] < 0)
				b |= C5;
			if (t[1][1][1] < 0)
				b |= C6;
			if (t[2][1][1] < 0)
				b |= C7;

			if (b != 0 && b != 0xff) {
				double[] d = { t[1][0][0], t[2][0][0], t[1][1][0], t[2][1][0],
						t[1][0][1], t[2][0][1], t[1][1][1], t[2][1][1] };
				tl.add(new MCElement(b, c, d));
			}

			// corner 2
			c[0][0] = minX;
			c[0][1] = midX;
			c[1][0] = midY;
			c[1][1] = maxY;
			c[2][0] = minZ;
			c[2][1] = midZ;

			b = 0;
			if (t[0][1][0] < 0)
				b |= C0;
			if (t[1][1][0] < 0)
				b |= C1;
			if (t[0][2][0] < 0)
				b |= C2;
			if (t[1][2][0] < 0)
				b |= C3;
			if (t[0][1][1] < 0)
				b |= C4;
			if (t[1][1][1] < 0)
				b |= C5;
			if (t[0][2][1] < 0)
				b |= C6;
			if (t[1][2][1] < 0)
				b |= C7;

			if (b != 0 && b != 0xff) {
				double[] d = { t[0][1][0], t[1][1][0], t[0][2][0], t[1][2][0],
						t[0][1][1], t[1][1][1], t[0][2][1], t[1][2][1] };
				tl.add(new MCElement(b, c, d));
			}

			// corner 3
			c[0][0] = midX;
			c[0][1] = maxX;
			c[1][0] = midY;
			c[1][1] = maxY;
			c[2][0] = minZ;
			c[2][1] = midZ;

			b = 0;
			if (t[1][1][0] < 0)
				b |= C0;
			if (t[2][1][0] < 0)
				b |= C1;
			if (t[1][2][0] < 0)
				b |= C2;
			if (t[2][2][0] < 0)
				b |= C3;
			if (t[1][1][1] < 0)
				b |= C4;
			if (t[2][1][1] < 0)
				b |= C5;
			if (t[1][2][1] < 0)
				b |= C6;
			if (t[2][2][1] < 0)
				b |= C7;

			if (b != 0 && b != 0xff) {
				double[] d = { t[1][1][0], t[2][1][0], t[1][2][0], t[2][2][0],
						t[1][1][1], t[2][1][1], t[1][2][1], t[2][2][1] };
				tl.add(new MCElement(b, c, d));
			}

			// corner 4
			c[0][0] = minX;
			c[0][1] = midX;
			c[1][0] = minY;
			c[1][1] = midY;
			c[2][0] = midZ;
			c[2][1] = maxZ;

			b = 0;
			if (t[0][0][1] < 0)
				b |= C0;
			if (t[1][0][1] < 0)
				b |= C1;
			if (t[0][1][1] < 0)
				b |= C2;
			if (t[1][1][1] < 0)
				b |= C3;
			if (t[0][0][2] < 0)
				b |= C4;
			if (t[1][0][2] < 0)
				b |= C5;
			if (t[0][1][2] < 0)
				b |= C6;
			if (t[1][1][2] < 0)
				b |= C7;

			if (b != 0 && b != 0xff) {
				double[] d = { t[0][0][1], t[1][0][1], t[0][1][1], t[1][1][1],
						t[0][0][2], t[1][0][2], t[0][1][2], t[1][1][2] };
				tl.add(new MCElement(b, c, d));
			}

			// corner 5
			c[0][0] = midX;
			c[0][1] = maxX;
			c[1][0] = minY;
			c[1][1] = midY;
			c[2][0] = midZ;
			c[2][1] = maxZ;

			b = 0;
			if (t[1][0][1] < 0)
				b |= C0;
			if (t[2][0][1] < 0)
				b |= C1;
			if (t[1][1][1] < 0)
				b |= C2;
			if (t[2][1][1] < 0)
				b |= C3;
			if (t[1][0][2] < 0)
				b |= C4;
			if (t[2][0][2] < 0)
				b |= C5;
			if (t[1][1][2] < 0)
				b |= C6;
			if (t[2][1][2] < 0)
				b |= C7;

			if (b != 0 && b != 0xff) {
				double[] d = { t[1][0][1], t[2][0][1], t[1][1][1], t[2][1][1],
						t[1][0][2], t[2][0][2], t[1][1][2], t[2][1][2] };
				tl.add(new MCElement(b, c, d));
			}

			// corner 6
			c[0][0] = minX;
			c[0][1] = midX;
			c[1][0] = midY;
			c[1][1] = maxY;
			c[2][0] = midZ;
			c[2][1] = maxZ;

			b = 0;
			if (t[0][1][1] < 0)
				b |= C0;
			if (t[1][1][1] < 0)
				b |= C1;
			if (t[0][2][1] < 0)
				b |= C2;
			if (t[1][2][1] < 0)
				b |= C3;
			if (t[0][1][2] < 0)
				b |= C4;
			if (t[1][1][2] < 0)
				b |= C5;
			if (t[0][2][2] < 0)
				b |= C6;
			if (t[1][2][2] < 0)
				b |= C7;

			if (b != 0 && b != 0xff) {
				double[] d = { t[0][1][1], t[1][1][1], t[0][2][1], t[1][2][1],
						t[0][1][2], t[1][1][2], t[0][2][2], t[1][2][2] };
				tl.add(new MCElement(b, c, d));
			}

			// corner 7
			c[0][0] = midX;
			c[0][1] = maxX;
			c[1][0] = midY;
			c[1][1] = maxY;
			c[2][0] = midZ;
			c[2][1] = maxZ;

			b = 0;
			if (t[1][1][1] < 0)
				b |= C0;
			if (t[2][1][1] < 0)
				b |= C1;
			if (t[1][2][1] < 0)
				b |= C2;
			if (t[2][2][1] < 0)
				b |= C3;
			if (t[1][1][2] < 0)
				b |= C4;
			if (t[2][1][2] < 0)
				b |= C5;
			if (t[1][2][2] < 0)
				b |= C6;
			if (t[2][2][2] < 0)
				b |= C7;

			if (b != 0 && b != 0xff) {
				double[] d = { t[1][1][1], t[2][1][1], t[1][2][1], t[2][2][1],
						t[1][1][2], t[2][1][2], t[1][2][2], t[2][2][2] };
				tl.add(new MCElement(b, c, d));
			}
		}
		elems.clear();
		elems.addAll(tl);
	}

	/**
	 * generates the first few segments
	 */
	private void init() {
		double d = (2 * rad / INITIAL_ELEMENTS);
		double[][] c = new double[3][2];
		double x, y, z;
		double r = rad;

		// precalc a 2d array representing the bottom
		double[][][] bottom = new double[INITIAL_ELEMENTS + 1][INITIAL_ELEMENTS + 1][2];
		x = -r;
		for (int xi = 0; xi <= INITIAL_ELEMENTS; xi++, x += d) {
			y = -r;
			for (int yi = 0; yi <= INITIAL_ELEMENTS; yi++, x += d)
				bottom[xi][yi][0] = f.evaluate(x, y, -r);
		}

		// calculate the initial division of squares
		z = -r;
		for (int zi = 0; zi < INITIAL_ELEMENTS; zi++, z += d) {
			int s1 = zi % 2;
			int s2 = (zi + 1) % 2;

			// precalc first rows in x/y dirs
			x = -r;
			for (int xi = 0; xi <= INITIAL_ELEMENTS; xi++, x += d) {
				bottom[xi][0][s2] = f.evaluate(x, -r, z + d);
				bottom[0][xi][s2] = f.evaluate(-r, x, z + d);
			}

			c[2][0] = z;
			c[2][1] = z + d;
			y = -r;
			for (int yi = 0; yi < INITIAL_ELEMENTS; yi++, y += d) {
				c[1][0] = y;
				c[1][1] = y + d;
				x = -r;
				for (int xi = 0; xi < INITIAL_ELEMENTS; xi++, x += d) {
					bottom[xi + 1][yi + 1][s2] = f
							.evaluate(x + d, y + d, z + d);

					c[0][0] = x;
					c[0][1] = x + d;
					int b = 0;

					if (bottom[xi][yi][s1] < 0)
						b |= C0;
					if (bottom[xi + 1][yi][s1] < 0)
						b |= C1;
					if (bottom[xi][yi + 1][s1] < 0)
						b |= C2;
					if (bottom[xi + 1][yi + 1][s1] < 0)
						b |= C3;
					if (bottom[xi][yi][s2] < 0)
						b |= C4;
					if (bottom[xi + 1][yi][s2] < 0)
						b |= C5;
					if (bottom[xi][yi + 1][s2] < 0)
						b |= C6;
					if (bottom[xi + 1][yi + 1][s2] < 0)
						b |= C7;

					if (b != 0 && b != 0xff) {
						double[] e = { bottom[xi][yi][s1],
								bottom[xi + 1][yi][s1], bottom[xi][yi + 1][s1],
								bottom[xi + 1][yi + 1][s1], bottom[xi][yi][s2],
								bottom[xi + 1][yi][s2], bottom[xi][yi + 1][s2],
								bottom[xi + 1][yi + 1][s2] };
						MCElement temp = new MCElement(b, c, e);
						elems.add(temp);
					}
					double[] e = { bottom[xi][yi][s1], bottom[xi + 1][yi][s1],
							bottom[xi][yi + 1][s1], bottom[xi + 1][yi + 1][s1],
							bottom[xi][yi][s2], bottom[xi + 1][yi][s2],
							bottom[xi][yi + 1][s2], bottom[xi + 1][yi + 1][s2] };
					MCElement temp = new MCElement(b, c, e);
					allelems.add(temp);
				}
			}
		}
	}

	/**
	 * @return the amount of visible segments
	 */
	public int getVisibleChunks() {
		return drawList.getChunkAmt();
	}

	/**
	 * @return the amount of vertices per segment
	 */
	public int getVerticesPerChunk() {
		return 3;
	}

	/**
	 * @return a float buffer with all triangles
	 */
	public FloatBuffer getVertices() {
		return drawList.getTriangleBuffer();
	}

	/**
	 * @return the normals for all triangles
	 */
	public FloatBuffer getNormals() {
		return drawList.getNormalBuffer();
	}
}
