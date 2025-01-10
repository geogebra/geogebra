package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.kernel.matrix.Coords;
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
		private ArrayList<Coords> normalsList;
		private ArrayList<Coords> vertices;
		private ArrayList<Integer> indices;
		final private int id;
		final private boolean isFlat;

		public Polygon(int id, boolean isFlat) {
			this.id = id;
			this.isFlat = isFlat;

			if (isFlat) {
				normal = new Coords(3);
			} else {
				normalsList = new ArrayList<>();
			}
			vertices = new ArrayList<>();
			indices = new ArrayList<>();
		}

		public int getId() {
			return id;
		}

		public void setNormal(double x, double y, double z) {
			if (isFlat) {
				normal.set(x, y, z);
			} else {
				normalsList.add(new Coords(x, y, z));
			}
		}

		public void addVertex(double x, double y, double z) {
			vertices.add(new Coords(x, y, z));
		}

		public void addTriangle(int v1, int v2, int v3) {
			indices.add(v1);
			indices.add(v2);
			indices.add(v3);
		}

		public int getRandomPointAndNormal(Coords retPoint, Coords retNormal,
				Coords tmp, Random r) {
			int index = 3 * r.nextInt(indices.size() / 3);
			double a = Math.random();
			double b = Math.random() * (1 - a);
			retPoint.setMul3(vertices.get(indices.get(index)), a);
			tmp.setMul3(vertices.get(indices.get(index + 1)), b);
			retPoint.addInside(tmp);
			tmp.setMul3(vertices.get(indices.get(index + 2)), 1 - a - b);
			retPoint.addInside(tmp);

			if (isFlat) {
				retNormal.set3(normal);
			} else {
				retNormal.set3(normalsList.get(indices.get(index)));
			}

			return index;
		}

		public void swapNormals() {
			if (isFlat) {
				normal.mulInside(-1);
			} else {
				for (Coords n : normalsList) {
					n.mulInside(-1);
				}
			}
		}

		public void setPosition(int sourceId, int sourceTriangle, Coords start,
				Coords n, Coords tmp1, Coords tmp2, Coords tmpO,
				Coords inPlaneCoords,
				TreeMap<Double, PolygonWithTraversedNormal> sortedPolygons) {
			int sourceIndex = -1;
			if (id == sourceId) {
				if (isFlat) {
					return;
				}
				sourceIndex = sourceTriangle;
			}

			tmpO.setW(1);
			tmp1.setW(0);
			tmp2.setW(0);
			for (int i = 0; i < indices.size(); i += 3) {
				// don't check (again) source triangle
				if (i != sourceIndex) {
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
							Coords traversedNormal = isFlat ? normal
									: normalsList.get(indices.get(i));
							sortedPolygons.put(-inPlaneCoords.getZ(),
									new PolygonWithTraversedNormal(this,
											traversedNormal));
							if (isFlat) {
								// can be traversed only once
								return;
							}
						}
					}
				}
			}
		}

		public void getTriangles(StringBuilder sb, FormatSTL format,
				Coords tmp1, Coords tmp2) {
			for (int i = 0; i < indices.size(); i += 3) {
				Coords v1 = vertices.get(indices.get(i));
				Coords v2 = vertices.get(indices.get(i + 1));
				Coords v3 = vertices.get(indices.get(i + 2));
				tmp1.setSub3(v2, v1);
				tmp2.setSub3(v3, v1);
				Coords orientedNormal = isFlat ? normal
						: normalsList.get(indices.get(i));
				if (orientedNormal.dotCrossProduct(tmp1, tmp2) > 0) {
					getTriangle(sb, format, v1, v2, v3, orientedNormal);
				} else {
					getTriangle(sb, format, v1, v3, v2, orientedNormal);
				}
			}
		}

		final static private void getTriangle(StringBuilder sb,
				FormatSTL format, Coords v1, Coords v2, Coords v3,
				Coords orientedNormal) {
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
			if (isFlat) {
				sb.append("\nnormal:\n  ");
				sb.append(normal.toString(digits, precision));
			}
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

	static private class PolygonWithTraversedNormal {
		private Polygon p;
		private Coords traversedNormal;
		
		public PolygonWithTraversedNormal(Polygon p, Coords traversedNormal) {
			this.p = p;
			this.traversedNormal = traversedNormal;
		}

		public int getId() {
			return p.getId();
		}

		public void setOrientedNormal(double orientation, Coords direction) {
			if (traversedNormal.dotproduct3(direction) * orientation < 0) {
				p.swapNormals();
			}
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
	 * 
	 * @param isFlat
	 *            all geometries are in the same plane
	 */
	public void startPolygon(boolean isFlat) {
		currentPolygon = new Polygon(polygons.size(), isFlat);
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
		TreeMap<Double, PolygonWithTraversedNormal> sortedPolygons = new TreeMap<>();

		polygonsLeft = new TreeSet<>();
		for (int i = 0; i < polygons.size(); i++) {
			polygonsLeft.add(i);
		}
		// always stop after some attempts
		int attempts = polygons.size();
		while (attempts > 0 && polygonsLeft.size() > 0) {
			int currentId = polygonsLeft.first();
			Polygon p0 = polygons.get(currentId);
			int triangleIndex = p0.getRandomPointAndNormal(start, normal, tmp1,
					r);
			sortedPolygons.put(0.0, new PolygonWithTraversedNormal(p0, normal));
			// find polygons traversed and position on line
			for (Polygon p : polygons) {
				p.setPosition(currentId, triangleIndex, start, normal, tmp1,
						tmp2, tmpP, inPlaneCoords, sortedPolygons);
			}
			Collection<PolygonWithTraversedNormal> sorted = sortedPolygons
					.values();
			// successful only if there is an even count of polygons traversed
			if (sorted.size() % 2 == 0) {
				double orientation = -1;
				for (PolygonWithTraversedNormal p : sorted) {
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
