package org.geogebra.desktop.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.io.OFFHandler;
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





	/**
	 * 
	 * @param stream
	 *            file input stream
	 * @throws IOException
	 *             input output exception
	 */
	public OFFReader() {


	}

	private void parse(BufferedReader in, OFFHandler handler)
			throws IOException {

		Scanner aux;
		String line = in.readLine();
		if (line != null) {

			// Skip comments and headers
			while (OFFHandler.isCommentOrOffHeader(line))
				line = in.readLine();
			aux = new Scanner(line);
			handler.setCounts(aux.nextInt(), aux.nextInt(), aux.nextInt());

			List<Coords> vert = new ArrayList<Coords>();
			List<int[]> fac = new ArrayList<int[]>();
			int count = 0;

			// read all vertices
			while (count < handler.getVertexCount()) {
				line = in.readLine().trim();
				App.debug(line + "," + count);
				if (!OFFHandler.isComment(line)) {
					aux = new Scanner(line);
					aux.useLocale(Locale.ENGLISH);
					vert.add(new Coords(aux.nextDouble(),
							aux.nextDouble(), aux.nextDouble(), 1.0));
					count++;
				}
			}

			count = 0;
			int vCount; // vertex count in each face
			while (count < handler.getFaceCount()) {
				line = in.readLine();
				if (!OFFHandler.isComment(line)) {
					aux = new Scanner(line);
					vCount = aux.nextInt();
					int[] v = new int[vCount];
					for (int j = 0; j < vCount; j++) {
						v[j] = aux.nextInt();
						if (v[j] < 0 || v[j] >= handler.getVertexCount()) {
							aux.close();
							exc(v[j], handler);
						}
					}
					fac.add(v);

					// check whether face color is specified;
					handler.facesColor[count] = tryReadColor(aux);
					count++;
				}
			}
			handler.vertices = Collections.unmodifiableList(vert);
			handler.faces = Collections.unmodifiableList(fac);
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

	private void exc(int k, OFFHandler handler) {
		throw new RuntimeException("Invalid vertex index " + k
				+ "(expected an integer between 0(inclusive) and "
				+ handler.getVertexCount() + "(exclusive))");
	}


	/**
	 * 
	 * @param file
	 *            off file
	 */
	public void parse(File file, OFFHandler handler) {
		try {
			InputStream stream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stream));
			parse(br, handler);
			handler.updateAfterParsing();
			App.debug(String.format("Off file has ben load:(v=%d;e=%d;f=%d)",
					handler.getVertexCount(), handler.getEdgeCount(),
					handler.getEdgeCount()));
		} catch (FileNotFoundException e) {
			// It is unexpected as we already have checked existence GUI
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
