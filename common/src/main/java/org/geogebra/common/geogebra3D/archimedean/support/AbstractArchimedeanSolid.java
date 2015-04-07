package org.geogebra.common.geogebra3D.archimedean.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.geom.ArchiBuilder;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.geom.Facet;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.geom.Surface;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.ObjectList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.OrderedTriple;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.Quick3X3Matrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Represents an Archimedean Solid. Contains an array of vertices and an array
 * of faces. Faces do not contain their own set of vertices. Rather, they
 * contain a list of indices into the AbstractArchimedeanSolid's array of
 * vertices.
 * 
 * @author kasparianr
 * 
 */
abstract class AbstractArchimedeanSolid implements IArchimedeanSolid {
	private Point[] vertices;
	private Face[] faces;
	private Coords[] verticesInABv;

	public int getVertexCount() {
		return vertices.length;
	}

	public Point[] getVertices() {
		return vertices;
	}

	public Coords[] getVerticesInABv() {
		return verticesInABv;
	}

	public void calcVerticesInABv() {
		// get 3 first points to create coord sys
		Point A = vertices[0];
		Point B = vertices[1];
		Point C = vertices[2];

		// calc AB length and create orthonormal vectors
		Coords v1l = B.sub(A);
		v1l.calcNorm();
		double l = v1l.getNorm();
		Coords v1 = v1l.mul(1 / l);

		Coords v3 = v1.crossProduct(C.sub(B)).normalized();

		Coords v2 = v3.crossProduct(v1);

		// create matrix
		CoordMatrix4x4 matrix = new CoordMatrix4x4();
		matrix.setOrigin(A);
		matrix.setVx(v1l);
		matrix.setVy(v2.mul(l));
		matrix.setVz(v3.mul(l));

		CoordMatrix mInv = matrix.inverse();

		// calc vertices
		int count = getVertexCount();
		verticesInABv = new Coords[count];
		for (int i = 0; i < count; i++)
			verticesInABv[i] = mInv.mul(vertices[i]);

	}

	public int getFaceCount() {
		return faces.length;
	}

	public IFace[] getFaces() {
		return faces;
	}

	/**
	 * Reposition the solid returned by ArchiBuilder as follows:
	 * 
	 * <li>Still centered on the origin. <li>The first face of the solid will be
	 * parallel to the XY plane. <li>All points of the solid will have z values
	 * greater than or equal to the z value of the first face of the solid. <li>
	 * The direction vector from the first vertex to the second vertex (this
	 * will be an edge of the first face) will be parallel to the X axis towards
	 * the positive. firstEdgeVector = (1, 0, 0).
	 * 
	 * @param polyTypes
	 * @param numPolys
	 * @param untwist
	 * @throws Exception
	 */

	protected AbstractArchimedeanSolid(int[] polyTypes, int numPolys,
			boolean untwist) throws Exception {
		Surface surface = new ArchiBuilder(polyTypes, numPolys, untwist)
				.createSurface();
		ObjectList<OrderedTriple> ots = surface.getPoints();
		Facet firstSide = surface.getRootFacet(0);
		OrderedTriple firstSideNormal = firstSide.getNormal().unit();
		OrderedTriple desiredFirstSideNormal = new OrderedTriple(0, 0, -1);
		OrderedTriple firstEdgeVector = ots.get(1).minus(ots.get(0)).unit();
		OrderedTriple desiredFirstEdgeVector = new OrderedTriple(1, 0, 0);

		Quick3X3Matrix m = Quick3X3Matrix
				.findRotationMatrix(firstSideNormal, desiredFirstSideNormal,
						firstEdgeVector, desiredFirstEdgeVector);
		for (OrderedTriple ot : ots) {
			ot.become(OrderedTriple.round(m.times(ot), 12));
		}
		Integer[] indexOrder = sort(ots);
		int[] inverseIndexOrder = new int[indexOrder.length];
		for (int i = 0; i < indexOrder.length; ++i) {
			inverseIndexOrder[indexOrder[i]] = i;
		}

		List<Point> pointList = new ArrayList<Point>();
		for (int index : indexOrder) {
			OrderedTriple ot = ots.get(index);
			pointList.add(new Point(ot.x, ot.y, ot.z));
		}
		setVertices(pointList.toArray(new Point[0]));

		calcVerticesInABv();

		List<Face> faceList = new ArrayList<Face>();
		for (Iterator<Facet> it = surface.rootFacetsIterator(); it.hasNext();) {
			Facet ss = it.next();
			int[] vertexIndices = ss.getPointIndices().toArray();
			int[] sortedVertexIndices = new int[vertexIndices.length];
			for (int i = 0; i < vertexIndices.length; ++i) {
				sortedVertexIndices[i] = inverseIndexOrder[vertexIndices[i]];
			}
			faceList.add(new Face(sortedVertexIndices));
		}
		setFaces(faceList.toArray(new Face[0]));
	}

	/**
	 * Return an array of indices that represent a counter-clockwise, Z-sort of
	 * the vertices.
	 * 
	 * For point order :
	 * 
	 * <li>first 4 points A, B, C, D would be the points of the first face other
	 * points E, F, G, H are coming after since they are above the face (when
	 * you go up to the center of the solid). <li>furthermore, for points at the
	 * same z-value (here E, F, G, H), points are ordered in the
	 * counter-clockwise order when you look at the first face, from the center,
	 * and first point (here E) should be the one that makes the lowest positive
	 * angle (center-A, center-E) when projected in the first face plane
	 * 
	 * 
	 * Center of first face is considered to be 0, 0, z-coord of first face.
	 * 
	 * @param vertices
	 * @return
	 */
	private static Integer[] sort(final ObjectList<OrderedTriple> vertices) {
		OrderedTriple p0 = vertices.get(0);
		final OrderedTriple zeroAngleVector = new OrderedTriple(p0.x, p0.y, 0);

		Integer[] indexList = new Integer[vertices.size()];
		for (int i = 0; i < vertices.size(); ++i) {
			indexList[i] = i;
		}
		Comparator<Integer> comp = new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				OrderedTriple ot1 = vertices.get(o1);
				OrderedTriple ot2 = vertices.get(o2);
				double zDiff = ot1.z - ot2.z;
				if (OrderedTriple.isApprox(zDiff, 0, 1e-8)) {
					OrderedTriple projectedOt1 = new OrderedTriple(ot1.x,
							ot1.y, 0);
					OrderedTriple projectedOt2 = new OrderedTriple(ot2.x,
							ot2.y, 0);
					OrderedTriple cross1 = zeroAngleVector.cross(projectedOt1);
					OrderedTriple cross2 = zeroAngleVector.cross(projectedOt2);
					double angle1 = zeroAngleVector.degBetween(projectedOt1);
					if (cross1.z < 0) {
						angle1 = 360 - angle1;
					}
					double angle2 = zeroAngleVector.degBetween(projectedOt2);
					if (cross2.z < 0) {
						angle2 = 360 - angle2;
					}
					double angleDiff = angle1 - angle2;
					if (angleDiff > 0) {
						return 1;
					} else if (angleDiff < 0) {
						return -1;
					} else {
						return 0;
					}
				} else if (zDiff > 0) {
					return 1;
				} else {// (zDiff < 0) {
					return -1;
				}
			}

		};

		Arrays.sort(indexList, comp);

		return indexList;
	}

	/**
	 * Intended to be called during construction
	 * 
	 * @param vertices
	 */
	protected void setVertices(Point... vertices) {
		this.vertices = vertices;
	}

	/**
	 * Intended to be called during construction
	 * 
	 * @param faces
	 */
	protected void setFaces(Face... faces) {
		this.faces = faces;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(vertices.length + " Vertices:");
		for (int i = 0; i < vertices.length; ++i) {
			Point p = vertices[i];
			sb.append("\n\t" + i + ": " + p);
		}

		sb.append("\n\n" + faces.length + " Faces:");
		for (Face f : faces) {
			sb.append("\n\t" + f);
		}

		return sb.toString();
	}

	/**
	 * Implementation of IFace
	 * 
	 * @author kasparianr
	 * 
	 */
	protected class Face implements IFace {
		private int[] vertexIndices;

		/**
		 * Vertices must be in clockwise order when facing the exterior side of
		 * this face.
		 * 
		 * @param vertices
		 */
		protected Face(int... vertexIndices) {
			this.vertexIndices = vertexIndices;
		}

		public int[] getVertexIndices() {
			return vertexIndices;
		}

		public int getVertexCount() {
			return vertexIndices.length;
		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (int i = 0; i < vertexIndices.length; ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(String.valueOf(vertexIndices[i]));
			}
			sb.append(")");
			return sb.toString();
		}

	}
}
