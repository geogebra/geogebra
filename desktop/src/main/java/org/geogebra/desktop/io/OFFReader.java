package org.geogebra.desktop.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.geogebra.common.geogebra3D.io.OFFHandler;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.opencsv.CSVException;

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


	private static void parse(BufferedReader in, OFFHandler handler)
			throws IOException, CSVException {

		String line = in.readLine();
		handler.reset();
		while (line != null) {
			handler.addLine(line);
			line = in.readLine();
		}
		handler.updateAfterParsing();
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
					new InputStreamReader(stream, Charsets.getUtf8()));
			parse(br, handler);

			br.close();

			Log.debug(String.format("Off file has ben load:(v=%d;e=%d;f=%d)",
					handler.getVertexCount(), handler.getEdgeCount(),
					handler.getEdgeCount()));
		} catch (FileNotFoundException e) {
			// It is unexpected as we already have checked existence GUI
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CSVException e) {
			e.printStackTrace();
		}
	}

}
