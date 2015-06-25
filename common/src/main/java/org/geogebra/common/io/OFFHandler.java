package org.geogebra.common.io;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * A handler for OFF file
 * 
 * @author Shamshad Alam
 *
 */
public class OFFHandler {
	private final Kernel kernel;
	private final Construction construction;
	private int faceCount;
	private int vertexCount;
	private int edgeCount;
	public List<Coords> vertices;
	public List<int[]> faces;
	public GColor[] facesColor;
	private static final String OFF = "OFF";
	private static final String COMMENT_PREFIX = "#";
	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param construction
	 *            construction
	 */
	public OFFHandler(Kernel kernel, Construction construction) {


		this.kernel = kernel;
		this.construction = construction;
	}



	public void updateAfterParsing() {

		AlgoDispatcher disp = kernel.getAlgoDispatcher();

		int vCount = getVertexCount();
		GeoPointND[] vert = new GeoPointND[vCount];

		List<Coords> vertices = getVertices();

		// Create vertices
		for (int i = 0; i < vCount; i++) {
			vert[i] = geoPoint(vertices.get(i));
		}

		// process all faces
		int index = 0;
		for (int[] fs : getFaces()) {
			int s = fs.length;
			GeoPointND[] geoPs = new GeoPointND[s];

			// Create faces (Polygon)
			for (int i = 0; i < s; i++)
				geoPs[i] = vert[fs[i]];
			// FIXME: better to add a method in AlgoDispatcher for this
			AlgoPolygon3D algo = new AlgoPolygon3D(construction, geoPs,
					false, null);
			GeoPolygon3D polygon = (GeoPolygon3D) algo.getOutput()[0];
			boolean hasColor = hasColor(index);
			if (polygon.isDefined()) {
				polygon.setLabel(null);
				if (hasColor) {
					polygon.setObjColor(getFaceColor(index));
				}
			} else {
				algo.remove();
				// FIXME: It works only only if polygon is convex
				for (int i = 2; i < s; i++) {
					algo = new AlgoPolygon3D(construction, new GeoPointND[] {
							geoPs[0], geoPs[i - 1], geoPs[i] },
							false, null);

					polygon = (GeoPolygon3D) algo.getOutput()[0];
					polygon.setLabel(null);
					if (hasColor)
						polygon.setObjColor(getFaceColor(index));
				}
			}
			index++;
		}
	}

	private GeoPoint3D geoPoint(Coords p3d) {
		GeoPoint3D p = new GeoPoint3D(construction);
		p.setCoords(p3d);
		p.setLabel(null);
		return p;
	}

	/**
	 * 
	 * @param faceIndex
	 *            index of the face
	 * @return color associated with the face
	 */
	public GColor getFaceColor(int faceIndex) {
		rangeCheck(faceIndex);
		return facesColor[faceIndex];
	}

	private void rangeCheck(int faceIndex) {
		if (faceIndex < 0 || faceIndex >= faceCount)
			throw new ArrayIndexOutOfBoundsException(faceIndex);
	}

	/**
	 * 
	 * @param faceIndex
	 *            face index
	 * @return true if face color is specified in the file
	 */
	public boolean hasColor(int faceIndex) {
		rangeCheck(faceIndex);
		return facesColor[faceIndex] != null;
	}

	/**
	 * 
	 * @return faceCount
	 */
	public int getFaceCount() {
		return faceCount;
	}

	/**
	 * 
	 * @return edgeCount
	 */
	public int getEdgeCount() {
		return edgeCount;
	}

	/**
	 * 
	 * @return vertexCount
	 */
	public int getVertexCount() {
		return vertexCount;
	}

	/**
	 * 
	 * @return unmodifiable list of faces
	 */
	public List<int[]> getFaces() {
		return faces;
	}

	/**
	 * 
	 * @return unmodifiable list of vertices
	 */
	public List<Coords> getVertices() {
		return vertices;
	}

	public void setCounts(int v, int f, int e) {

		vertexCount = v;
		faceCount = f;
		edgeCount = e;
		facesColor = new GColor[faceCount];

	}

	public static boolean isCommentOrOffHeader(String line) {
		String l = line == null ? "" : line.trim();
		return isComment1(l) || OFF.equalsIgnoreCase(l);
	}

	public static boolean isComment(String line) {
		String l = line == null ? "" : line.trim();
		return isComment1(l);
	}

	public static boolean isComment1(String line) {
		return line.startsWith(COMMENT_PREFIX);
	}
}
