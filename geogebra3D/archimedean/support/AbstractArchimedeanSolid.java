package geogebra3D.archimedean.support;

import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;

import java.util.ArrayList;
import java.util.List;

import com.quantimegroup.solutions.archimedean.app.ArchiBuilder;
import com.quantimegroup.solutions.archimedean.app.SpaceSide;
import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;


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
	
	public Coords[] getVerticesInABv(){
		return verticesInABv;
	}
	
	public void calcVerticesInABv(){
		//get 3 first points of the first face to create coord sys
		/*
		int[] indices = faces[0].getVertexIndices();
		
		Point A = vertices[indices[0]];
		Point B = vertices[indices[1]];
		Point C = vertices[indices[2]];
		*/
		
		Point A = vertices[0];
		Point B = vertices[1];
		Point C = vertices[2];	
		
		//calc AB length and create orthonormal vectors
		Coords v1l = B.sub(A);
		v1l.calcNorm();
		double l = v1l.getNorm();
		Coords v1 = v1l.mul(1/l);
		
		Coords v3 = v1.crossProduct(C.sub(A)).normalized();
		
		Coords v2 = v3.crossProduct(v1);
		
		//create matrix
		CoordMatrix4x4 matrix = new CoordMatrix4x4();
		matrix.setOrigin(A);
		matrix.setVx(v1l);
		matrix.setVy(v2.mul(l));
		matrix.setVz(v3.mul(l));
		
		CoordMatrix mInv = matrix.inverse();
		
		//calc vertices
		int count = getVertexCount();
		verticesInABv = new Coords[count];
		for (int i=0; i<count; i++) {
			verticesInABv[i] = mInv.mul(vertices[i]);
		}		
	}

	public int getFaceCount() {
		return faces.length;
	}

	public IFace[] getFaces() {
		return faces;
	}

	protected AbstractArchimedeanSolid(int[] polyTypes, int numPolys, boolean untwist) {
		ArchiBuilder builder = new ArchiBuilder(polyTypes, numPolys, untwist);
		List<OrderedTriple> otList = new ArrayList<OrderedTriple>();
		builder.getPoints(otList);
		List<Point> pointList = new ArrayList<Point>();
		for (OrderedTriple ot : otList) {
			pointList.add(new Point(ot.x, ot.y, ot.z));
		}
		setVertices(pointList.toArray(new Point[0]));

		List<SpaceSide> ssList = new ArrayList<SpaceSide>();
		builder.getSides(ssList);
		List<Face> faceList = new ArrayList<Face>();
		for (SpaceSide ss : ssList) {
			faceList.add(new Face(ss.getIndex().shrink().ints));
		}
		setFaces(faceList.toArray(new Face[0]));
		
		calcVerticesInABv();
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

	@Override
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
		 * Vertices must be in clockwise order when facing the exterior side of this
		 * face.
		 * 
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

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (int i = 0; i < vertexIndices.length; ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(String.valueOf(vertexIndices[i]));
				//sb.append(" " + AbstractArchimedeanSolid.this.vertices[i]);
			}
			sb.append(")");
			return sb.toString();
		}
	}
}
