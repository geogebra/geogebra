package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.geogebra3D.kernel3D.MyPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Coords3;
import org.geogebra.common.kernel.Matrix.CoordsDouble3;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;

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
public class GeoTriangulatedSurface3D extends GeoElement3D {
	private static final int DEFAULT_CAPACITY = 1024;
	private int capacity = DEFAULT_CAPACITY;
	private int current;
	private int restore;
	private int counter;
	private boolean defined;
	private MyPoint3D[] vertices;

	/**
	 * 
	 * @param cons
	 *            {@link Construction}
	 */
	public GeoTriangulatedSurface3D(Construction cons) {
		super(cons);
		this.vertices = new MyPoint3D[capacity];
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
		ensureCapacity(current);
		if (vertices[current] != null) {
			vertices[current].x = x;
			vertices[current].y = y;
			vertices[current].z = z;
			vertices[current].lineTo = counter != 0;
		} else {
			vertices[current] = new MyPoint3D(x, y, z, counter != 0);
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
		}
	}

	@Override
	public Coords getLabelPosition() {
		return Coords.VZ;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.TRIANGULATED_SURFACE_3D;
	}

	@Override
	public GeoElement copy() {
		GeoTriangulatedSurface3D surf = new GeoTriangulatedSurface3D(cons);
		copy(this, surf);
		return surf;
	}

	@Override
	public void set(GeoElement geo) {
		GeoTriangulatedSurface3D surf = (GeoTriangulatedSurface3D) geo;
		copy(surf, this);
	}

	private static void copy(GeoTriangulatedSurface3D src,
			GeoTriangulatedSurface3D dst) {
		dst.vertices = copyOf(src.vertices);
		dst.current = src.current;
		dst.capacity = src.capacity;
		dst.restore = src.restore;
		dst.defined = src.defined;
		dst.counter = src.counter;

	}

	/**
	 * @return list of points on the surface
	 */
	public MyPoint3D[] getPoints() {
		return vertices;
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
	 * copy points from source to destination
	 * 
	 * @param src
	 *            source list
	 * @return copy of points
	 */
	public static MyPoint3D[] copyOf(MyPoint3D[] src) {
		int size = src.length;
		MyPoint3D[] pts = new MyPoint3D[size];
		for(int i = 0; i < size; i++) {
			pts[i] = new MyPoint3D(src[i].x, src[i].y, src[i].z, src[i].lineTo);
		}
		return pts;
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		this.defined = false;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return false;
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		return false;
	}

	@Override
	public HitType getLastHitType() {
		return HitType.NONE;
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
		private Triangle current = new Triangle(new CoordsDouble3(0, 0, 0),
				new CoordsDouble3(0, 0, 0), new CoordsDouble3(0, 0, 0));

		/**
		 * @param surf
		 *            list of points
		 */
		public SurfaceMover(GeoTriangulatedSurface3D surf) {
			this.size = surf.size();
			this.points = new MyPoint3D[this.size];
			System.arraycopy(surf.getPoints(), 0, this.points, 0, this.size);
			next = 2;
		}

		public boolean hasNext() {
			return next < size;
		}


		public Triangle next() {

			MyPoint3D p1 = points[next];
			MyPoint3D p2 = points[next - 1];
			MyPoint3D p3 = points[next - 2];

			current.v1.set(p1.x, p1.y, p1.z);
			current.v2.set(p2.x, p2.y, p2.z);
			current.v3.set(p3.x, p3.y, p3.z);
			next++;

			if (next < size && !points[next].lineTo) {
				next += 2;
			}

			return current;
		}
	}

	/**
	 * A lightweight class to represent coordinates of a 3D triangular surface
	 */
	public static class Triangle {
		/**
		 * First vertex
		 */
		public Coords3 v1;
		/**
		 * Second vertex
		 */
		public Coords3 v2;
		/**
		 * Third vertex
		 */
		public Coords3 v3;

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
			this.v1 = c1;
			this.v2 = c2;
			this.v3 = c3;
		}

		@Override
		public String toString() {
			return "[" + toString(v1) + toString(v2) + toString(v3) + "]";
		}

		private static String toString(Coords3 c) {
			return "";
			// return String.format("(%.2f, %.2f, %.2f)", c.getXd(), c.getYd(),
			// c.getZd());
		}
	}
}
