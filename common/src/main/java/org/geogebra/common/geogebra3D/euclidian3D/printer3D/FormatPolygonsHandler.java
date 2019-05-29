package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Handles polygons to give a correct normal orientation (inside to outside) if
 * possible
 *
 */
public class FormatPolygonsHandler {

	private ArrayList<Polygon> polygons;
	private TreeSet<Integer> polygonsLeft;
	private Polygon currentPolygon;

	final private Coords tmp1;
	final private Coords tmp2;

	static private class Polygon {
		private Coords normal;
		private Coords orientedNormal;
		private ArrayList<Coords> vertices;
		private ArrayList<Integer> indices;
		private int id;

		public Polygon(int id) {
			normal = new Coords(3);
			orientedNormal = new Coords(3);
			vertices = new ArrayList<>();
			indices = new ArrayList<>();
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void setNormal(double x, double y, double z) {
			normal.set(x, y, z);
		}

		public void addVertex(double x, double y, double z) {
			vertices.add(new Coords(x, y, z));
		}

		public void addTriangle(int v1, int v2, int v3) {
			indices.add(v1);
			indices.add(v2);
			indices.add(v3);
		}

		public void getRandomPoint(Coords ret, Coords tmp, Random r) {
			int index = 3 * r.nextInt(indices.size() / 3);
			double a = Math.random();
			double b = Math.random() * (1 - a);
			ret.setMul3(vertices.get(indices.get(index)), a);
			tmp.setMul3(vertices.get(indices.get(index + 1)), b);
			ret.addInside(tmp);
			tmp.setMul3(vertices.get(indices.get(index + 2)), 1 - a - b);
			ret.addInside(tmp);
		}

		public void getNormal(Coords ret) {
			ret.set3(normal);
		}

		public void setOrientedNormal(double orientation, Coords n) {
			orientedNormal.set3(normal);
			if (normal.dotproduct3(n) * orientation < 0) {
				orientedNormal.mulInside(-1);
			}
		}

		public double getPosition(Coords start, Coords n, Coords tmp1,
				Coords tmp2, Coords tmpO, Coords inPlaneCoords) {
			tmpO.setW(1);
			tmp1.setW(0);
			tmp2.setW(0);
			for (int i = 0; i < indices.size(); i += 3) {
				tmpO.set3(vertices.get(indices.get(i)));
				tmp1.setSub3(vertices.get(indices.get(i + 1)), tmpO);
				tmp2.setSub3(vertices.get(indices.get(i + 2)), tmpO);
				start.projectPlaneInPlaneCoords(tmp1, tmp2, n, tmpO,
						inPlaneCoords);
				if (!DoubleUtil.isZero(inPlaneCoords.getW())) {
					double x = inPlaneCoords.getX();
					double y = inPlaneCoords.getY();
					if (x > 0 && y > 0 && x + y < 1) {
						// inside the triangle
						return -inPlaneCoords.getZ();
					}
				}
			}
			
			return Double.NaN;
		}

		public void getTriangles(StringBuilder sb, FormatSTL format,
				Coords tmp1, Coords tmp2) {
			for (int i = 0; i < indices.size(); i += 3) {
				Coords v1 = vertices.get(indices.get(i));
				Coords v2 = vertices.get(indices.get(i + 1));
				Coords v3 = vertices.get(indices.get(i + 2));
				tmp1.setSub3(v2, v1);
				tmp2.setSub3(v3, v1);
				if (orientedNormal.dotCrossProduct(tmp1, tmp2) > 0) {
					getTriangle(sb, format, v1, v2, v3);
				} else {
					getTriangle(sb, format, v1, v3, v2);
				}
			}
		}

		private void getTriangle(StringBuilder sb, FormatSTL format, Coords v1,
				Coords v2, Coords v3) {
			format.getTriangle(sb, orientedNormal.getX(), orientedNormal.getY(),
					orientedNormal.getZ(), v1.getX(), v1.getY(), v1.getZ(),
					v2.getX(), v2.getY(), v2.getZ(), v3.getX(), v3.getY(),
					v3.getZ());
		}

		@Override
		public String toString() {
			return toString(2, 2);
		}

		public String toString(int digits, int precision) {
			StringBuilder sb = new StringBuilder();
			sb.append("== id: ");
			sb.append(id);
			sb.append("\nnormal:\n  ");
			sb.append(normal.toString(digits, precision));
			sb.append("\n");
			sb.append(orientedNormal.toString(digits, precision));
			sb.append("\nvertices:");
			for (Coords v : vertices) {
				sb.append("\n  ");
				sb.append(v.toString(digits, precision));
			}
			sb.append("\ntriangles:");
			for (int i = 0; i < indices.size() / 3; i++) {
				sb.append("\n  ");
				sb.append(indices.get(i));
				sb.append(",");
				sb.append(indices.get(i + 1));
				sb.append(",");
				sb.append(indices.get(i + 2));
			}

			return sb.toString();
		}
	}

	/**
	 * constructor
	 */
	public FormatPolygonsHandler() {
		polygons = new ArrayList<>();
		tmp1 = new Coords(4);
		tmp2 = new Coords(4);
	}

	/**
	 * start a polygon
	 */
	public void startPolygon() {
		currentPolygon = new Polygon(polygons.size());
		polygons.add(currentPolygon);
	}

	/**
	 * add a vertex to the current polygon
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	public void addVertex(double x, double y, double z) {
		currentPolygon.addVertex(x, y, z);
	}

	/**
	 * set current polygon normal
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	public void setNormal(double x, double y, double z) {
		currentPolygon.setNormal(x, y, z);
	}

	/**
	 * add a triangle to current polygon
	 * 
	 * @param v1
	 *            first vertex index
	 * @param v2
	 *            second vertex index
	 * @param v3
	 *            third vertex index
	 */
	public void addTriangle(int v1, int v2, int v3) {
		currentPolygon.addTriangle(v1, v2, v3);
	}

	/**
	 * set oriented normals
	 */
	public void setOrientedNormals() {
		Coords start = Coords.createInhomCoorsInD3();
		Coords tmpP = Coords.createInhomCoorsInD3();
		Coords inPlaneCoords = new Coords(4);
		Coords normal = new Coords(4);
		Random r = new Random();
		TreeMap<Double, Polygon> sortedPolygons = new TreeMap<>();

		polygonsLeft = new TreeSet<>();
		for (int i = 0; i < polygons.size(); i++) {
			polygonsLeft.add(i);
		}
		// always stop after some attempts
		int attempts = polygons.size();
		while (attempts > 0 && polygonsLeft.size() > 0) {
			int currentId = polygonsLeft.first();
			Polygon p0 = polygons.get(currentId);
			p0.getRandomPoint(start, tmp1, r);
			p0.getNormal(normal);
			sortedPolygons.put(0.0, p0);
			// find polygons traversed and position on line
			for (int i = 0; i < polygons.size(); i++) {
				if (i != currentId) {
					Polygon p = polygons.get(i);
					double position = p.getPosition(start, normal, tmp1, tmp2,
							tmpP, inPlaneCoords);

					if (!Double.isNaN(position)) {
						sortedPolygons.put(position, p);
					}
				}
			}
			Collection<Polygon> sorted = sortedPolygons.values();
			// successful only if there is an even count of polygons traversed
			if (sorted.size() % 2 == 0) {
				double orientation = -1;
				for (Polygon p : sorted) {
					if (polygonsLeft.contains(p.getId())) {
						polygonsLeft.remove(p.getId());
						p.setOrientedNormal(orientation, normal);
					}
					orientation *= -1;
				}
			}
			sortedPolygons.clear();
			attempts--;
		}

	}

	/**
	 * get triangles from polygons
	 * 
	 * @param sb
	 *            string builder
	 * @param format
	 *            calling format
	 */
	public void getTriangles(StringBuilder sb, FormatSTL format) {
		for (Polygon p : polygons) {
			p.getTriangles(sb, format, tmp1, tmp2);
		}
	}
}
