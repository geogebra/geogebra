package org.geogebra.desktop.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * Read OFF (Object File Format) file. Off file begins with "OFF" indicating it
 * is an off file, followed by one or more comments. Each comment line start
 * with '#'. Thereafter three integers in a single line representing
 * vertixCount(V), faceCount(F) and edgeCount(F) respectively. Since V + F - E =
 * 2, E can be ignored safely or it can be used for verification purpose. The
 * file is followed by V lines, each contains three doubles representing
 * coordinates x, y, and z respectively. Finally the file is followed by F
 * lines, every line represents for a face. Each line start with an integer N,
 * number of vertices in the face followed by N integers n1, n2, n3, ..., nN,
 * each integer represents an index of a vertex(0 based indexing)
 */
public class OFFReader {
	private static final String OFF = "OFF";
	private static final String COMMENT_PREFIX = "#";
	private int faceCount;
	private int vertexCount;
	private int edgeCount;
	private List<Coords> vertices;
	private List<int[]> faces;
	private GColor[] facesColor;

	/**
	 * @param content
	 *            Off Content
	 */
	public OFFReader(String content) {
		try {
			parse(new BufferedReader(new StringReader(content)));
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @param file
	 *            off file
	 * @throws FileNotFoundException
	 *             file not found
	 * @throws IOException
	 *             input output exception
	 */
	public OFFReader(File file) throws FileNotFoundException, IOException {
		this(new BufferedInputStream(new FileInputStream(file)));
	}

	/**
	 * 
	 * @param stream
	 *            file input stream
	 * @throws IOException
	 *             input output exception
	 */
	public OFFReader(InputStream stream) throws IOException {
		Objects.requireNonNull(stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));

		parse(br);

	}

	private void parse(BufferedReader in) throws IOException {

		Scanner aux;
		String line = in.readLine();
		if (line != null) {

			// Skip comments and headers
			while (isCommentOrOffHeader(line))
				line = in.readLine();

			aux = new Scanner(line);

			vertexCount = aux.nextInt();
			faceCount = aux.nextInt();
			edgeCount = aux.nextInt();
			facesColor = new GColor[faceCount];

			List<Coords> vert = new ArrayList<Coords>();
			List<int[]> fac = new ArrayList<int[]>();
			int count = 0;

			// read all vertices
			while (count < vertexCount) {
				line = in.readLine().trim();
				App.debug(line + "," + count);
				if (!isComment(line)) {
					aux = new Scanner(line);
					aux.useLocale(Locale.ENGLISH);
					vert.add(new Coords(aux.nextDouble(),
							aux.nextDouble(), aux.nextDouble(), 1.0));
					count++;
				}
			}

			count = 0;
			int vCount; // vertex count in each face
			while (count < faceCount) {
				line = in.readLine();
				if (!isComment(line)) {
					aux = new Scanner(line);
					vCount = aux.nextInt();
					int[] v = new int[vCount];
					for (int j = 0; j < vCount; j++) {
						v[j] = aux.nextInt();
						if (v[j] < 0 || v[j] >= vertexCount) {
							aux.close();
							exc(v[j]);
						}
					}
					fac.add(v);

					// check whether face color is specified;
					facesColor[count] = tryReadColor(aux);
					count++;
				}
			}
			this.vertices = Collections.unmodifiableList(vert);
			this.faces = Collections.unmodifiableList(fac);
		}
	}

	private static GColor tryReadColor(Scanner in) {
		GColor color = null;
		try {
			if (in.hasNextInt()) {
				int r = in.nextInt();
				int g = 0;
				int b = 0;
				int a = 0xff;
				if (in.hasNextInt()) {
					g = in.nextInt();
					b = in.hasNextInt() ? in.nextInt() : 0;
					a = in.hasNextInt() ? in.nextInt() : 0xff;
				} else {
					a = r & 0xff;
					b = (r >>> 8) & 0xff;
					g = (r >>> 16) & 0xff;
					r = r >>> 24;
				}
				color = AwtFactory.prototype.newColor(r, g, b, a);
			} else if (in.hasNextFloat()) {
				float h = in.nextFloat();
				h = Math.min(1, h);
				if (in.hasNextDouble()) {
					float s = in.nextFloat();
					float b = in.nextFloat();
					float a = 1.0f;
					if (in.hasNextFloat())
						a = in.nextFloat();
					s = Math.min(1.0f, s);
					b = Math.min(1.0f, b);
					a = Math.min(1.0f, a);
					color = AwtFactory.prototype.newColor(h, s, b, a);
				} else
					color = AwtFactory.prototype.newColor(h, h, h);
			}
		} catch(Exception ex) {
			Log.debug(ex.getMessage());
		}
		return color;
	}

	private void exc(int k) {
		throw new RuntimeException("Invalid vertex index " + k
				+ "(expected an integer between 0(inclusive) and "
				+ vertexCount + "(exclusive))");
	}

	private static boolean isCommentOrOffHeader(String line) {
		String l = line == null ? "" : line.trim();
		return isComment1(l) || OFF.equalsIgnoreCase(l);
	}

	private static boolean isComment(String line) {
		String l = line == null ? "" : line.trim();
		return isComment1(l);
	}

	private static boolean isComment1(String line) {
		return line.startsWith(COMMENT_PREFIX);
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
}
