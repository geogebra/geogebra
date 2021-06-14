package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.geogebra3D.kernel3D.MyPoint3D;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.matrix.Coords3;
import org.geogebra.common.kernel.matrix.CoordsDouble3;

/**
 * Represent 3D triangulated surface. The set of points {p1, p2, p3, ... , pn}
 * added between a consecutive call of {@code startTriangulation()} and
 * {@code endTriangulation()} represent vertices of the surface. Triangular
 * surfaces are created by joining three consecutive points in the list and
 * finally joining last point with first two points, thus {@code n - 2}
 * triangles are created for each call of
 * {@code startTriangulation() and endTriangulation()}. No triangle is created
 * if n is less than 3
 * 
 * @author Shamshad Alam
 *
 */
public class GeoTriangulatedSurface3D {
	private static final int DEFAULT_CAPACITY = 1024;
	private int capacity = DEFAULT_CAPACITY;
	private int current;
	private int restore;
	private int counter;
	private MyPoint3D[] vertices;
	private MyPoint3D[] normals;

	/**
	 * 
	 */
	public GeoTriangulatedSurface3D() {
		this.vertices = new MyPoint3D[capacity];
		this.normals = new MyPoint3D[capacity];
	}

	/**
	 * Remove all the surfaces from the list
	 */
	public void clear() {
		this.current = 0;
		this.counter = 0;
	}

	/**
	 * Start a new surface triangulation
	 */
	public void beginTriangulation() {
		if (this.counter == 0) {
			this.restore = this.current;
		}
	}

	/**
	 * End current surface triangulation
	 */
	public void endTriangulation() {
		if (counter < 3) {
			this.current = this.restore;
		}
		counter = 0;
	}

	/**
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param z
	 *            z coordinate
	 */
	public void insertPoint(double x, double y, double z) {
		insertPoint(new double[] { x, y, z }, new double[] { 0, 0, 0 });
	}

	/**
	 * 
	 * @param p
	 *            coordinates of point {x, y, z}
	 * @param n
	 *            normal vector at point {x, y, z}
	 */
	public void insertPoint(double[] p, double[] n) {
		ensureCapacity(current);
		if (vertices[current] != null) {
			vertices[current].setLocation(p[0], p[1], p[2]);
			vertices[current].setLineTo(counter != 0);
			normals[current].setLocation(n[0], n[1], n[2]);
		} else {
			vertices[current] = new MyPoint3D(p[0], p[1], p[2],
					counter != 0 ? SegmentType.LINE_TO : SegmentType.MOVE_TO);
			normals[current] = new MyPoint3D(n[0], n[1], n[2],
					SegmentType.MOVE_TO);
		}
		++current;
		++counter;
	}

	private void ensureCapacity(int size) {
		if (size >= capacity) {
			capacity = Integer.highestOneBit(size) << 1;
			MyPoint3D[] temp = new MyPoint3D[capacity];
			System.arraycopy(vertices, 0, temp, 0, size);
			this.vertices = temp;
			temp = new MyPoint3D[capacity];
			System.arraycopy(normals, 0, temp, 0, size);
			this.normals = temp;
		}
	}

	/**
	 * @return list of points on the surface
	 */
	public MyPoint3D[] getPoints() {
		return vertices;
	}

	/**
	 * 
	 * @return list of normals corresponding to points
	 */
	public MyPoint3D[] getNormals() {
		return normals;
	}

	/**
	 * 
	 * @return total number of points before the last call of
	 *         {@link #endTriangulation()}
	 */
	public int size() {
		return this.current - this.counter;
	}

	/**
	 * Get the surface mover for the current surfaces. Any change in the surface
	 * after invocation of this method is not reflected by SurfaceMover
	 * 
	 * @return a {@link SurfaceMover} for this Surface
	 */
	public SurfaceMover getSurfaceMover() {
		return new SurfaceMover(this);
	}

	/**
	 * An iterator to allow move forward about the current surface
	 */
	public static class SurfaceMover {
		private final int size;
		private int next;
		private MyPoint3D[] points;
		private MyPoint3D[] normals;
		private Triangle current = new Triangle(new CoordsDouble3(0, 0, 0),
				new CoordsDouble3(0, 0, 0), new CoordsDouble3(0, 0, 0));

		/**
		 * @param surf
		 *            list of points
		 */
		public SurfaceMover(GeoTriangulatedSurface3D surf) {
			this.size = surf.size();
			this.points = new MyPoint3D[this.size];
			this.normals = new MyPoint3D[this.size];
			System.arraycopy(surf.getPoints(), 0, this.points, 0, this.size);
			System.arraycopy(surf.getNormals(), 0, this.normals, 0, this.size);
			next = 2;
		}

		/**
		 * @return whether this has next triangle
		 */
		public boolean hasNext() {
			return next < size;
		}

		/**
		 * @return next triangle
		 */
		public Triangle next() {

			set(current.v1, points[next]);
			set(current.v2, points[next - 1]);
			set(current.v3, points[next - 2]);
			set(current.n1, normals[next]);
			set(current.n2, normals[next - 1]);
			set(current.n3, normals[next - 2]);

			next++;

			if (next < size && !points[next].getLineTo()) {
				next += 2;
			}

			return current;
		}

		private static void set(Coords3 c, MyPoint3D p) {
			c.set(p.x, p.y, p.z);
		}

		/**
		 * 
		 * @return triangles count
		 */
		public int getTrianglesCount() {
			return size / 3;
		}

	}

	/**
	 * A lightweight class to represent coordinates of a 3D triangular surface
	 */
	public static class Triangle {
		private static final Coords3 UNDEFINED = new CoordsDouble3();
		/**
		 * First vertex
		 */
		public Coords3 v1;
		/**
		 * Normal at first vertex
		 */
		public Coords3 n1;
		/**
		 * Second vertex
		 */
		public Coords3 v2;
		/**
		 * Normal at first vertex
		 */
		public Coords3 n2;
		/**
		 * Third vertex
		 */
		public Coords3 v3;
		/**
		 * Normal at first vertex
		 */
		public Coords3 n3;

		/**
		 * 
		 * @param c1
		 *            Coordinate of first vertex
		 * @param c2
		 *            Coordinate of second vertex
		 * @param c3
		 *            Coordinate of third vertex
		 */
		public Triangle(Coords3 c1, Coords3 c2, Coords3 c3) {
			this(c1, c2, c3, UNDEFINED.copyVector(), UNDEFINED.copyVector(),
					UNDEFINED.copyVector());
		}

		/**
		 * 
		 * @param c1
		 *            Coordinate of first vertex
		 * @param c2
		 *            Coordinate of second vertex
		 * @param c3
		 *            Coordinate of third vertex
		 * @param n1
		 *            Normal at first vertex
		 * @param n2
		 *            Normal at second vertex
		 * @param n3
		 *            Normal at third vertex
		 */
		public Triangle(Coords3 c1, Coords3 c2, Coords3 c3, Coords3 n1,
				Coords3 n2, Coords3 n3) {
			this.v1 = c1;
			this.v2 = c2;
			this.v3 = c3;
			this.n1 = n1;
			this.n2 = n2;
			this.n3 = n3;
		}

		@Override
		public String toString() {
			return "[" + toString(v1) + toString(v2) + toString(v3) + "]";
		}

		private static String toString(Coords3 c) {
			return "(" + c.getXd() + "," + c.getYd() + "" + c.getZd() + ")";
		}
	}
}