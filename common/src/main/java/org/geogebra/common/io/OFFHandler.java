package org.geogebra.common.io;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.opencsv.CSVException;
import org.geogebra.common.util.opencsv.CSVParser;

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
	private CSVParser parser;
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
		this.parser = new CSVParser(' ');
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
		faces = new ArrayList<int[]>(faceCount);
		vertices = new ArrayList<Coords>(vertexCount);

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

	public static GColor tryReadColor(String[] in, int offset) {
		GColor color = null;
		try {
			if (in.length > offset && in[offset].indexOf('.') < 0) {
				int r = Integer.parseInt(in[offset]);
				int g = 0;
				int b = 0;
				int a = 0xff;
				if (in.length > offset + 1) {
					g = Integer.parseInt(in[offset + 1]);
					b = in.length > offset + 2 ? Integer
							.parseInt(in[offset + 2]) : 0;
					a = in.length > offset + 3 ? Integer
							.parseInt(in[offset + 3]) : 0xff;
				} else {
					a = r & 0xff;
					b = (r >>> 8) & 0xff;
					g = (r >>> 16) & 0xff;
					r = r >>> 24;
				}
				color = AwtFactory.prototype.newColor(r, g, b, a);
			}

			else if (in.length > offset) {
				float h = Float.parseFloat(in[offset]);
				h = Math.min(1, h);
				if (in.length > offset + 1) {
					float s = Float.parseFloat(in[offset + 1]);
					float b = Float.parseFloat(in[offset + 2]);
					float a = 1.0f;
					if (in.length > offset + 3) {
						a = Float.parseFloat(in[offset + 3]);
					}
					s = Math.min(1.0f, s);
					b = Math.min(1.0f, b);
					a = Math.min(1.0f, a);
					color = AwtFactory.prototype.newColor(h, s, b, a);
				} else
					color = AwtFactory.prototype.newColor(h, h, h);
			}
		} catch (Exception ex) {
			Log.debug(ex.getMessage());
		}
		return color;
	}

	public void addFaceLine(String line) throws CSVException {
		App.debug(line);
		if (!OFFHandler.isComment(line)) {
			String[] aux = nonempty(parser.parseLine(line));
			int vCount = Integer.parseInt(aux[0]);
			int[] v = new int[vCount];
			for (int j = 0; j < vCount; j++) {
				v[j] = Integer.parseInt(aux[j + 1]);
				if (v[j] < 0 || v[j] >= getVertexCount()) {
					Log.error(v[j] + " out of range");
				}
			}
			faces.add(v);

			// check whether face color is specified;
			facesColor[faces.size() - 1] = OFFHandler
					.tryReadColor(aux, vCount + 1);

		}

	}

	public void addVertexLine(String line) throws CSVException {
		if (!OFFHandler.isComment(line)) {
			String[] aux = nonempty(parser.parseLine(line));
			vertices.add(new Coords(Double.parseDouble(aux[0]), Double
					.parseDouble(aux[1]), Double.parseDouble(aux[2]), 1.0));
		}

	}

	private String[] nonempty(String[] parseLine) {
		String[] nonempty = new String[parseLine.length];
		int j = 0;
		for (int i = 0; i < parseLine.length; i++) {
			if (parseLine[i].trim().length() > 0) {
				nonempty[j++] = parseLine[i].trim();
			}
		}
		return nonempty;
	}

	public void addLine(String line) throws CSVException {
		if (OFFHandler.isCommentOrOffHeader(line)) {
			return;
		}
		if (vertexCount == 0) {
			String[] aux = nonempty(parser.parseLine(line));
			setCounts(Integer.parseInt(aux[0]), Integer.parseInt(aux[1]),
				Integer.parseInt(aux[2]));
			return;
		}
		// read all vertices
		if (getVertices().size() < vertexCount) {
			addVertexLine(line);
			return;
		}

		while (getFaces().size() < faceCount) {
			addFaceLine(line);
			return;
		}

	}

	public void reset() {
		this.vertexCount = 0;

	}
}
