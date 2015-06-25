package org.geogebra.desktop.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

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
	private OFFReader reader;

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

	/**
	 * 
	 * @param file
	 *            off file
	 */
	public void parse(File file) {
		try {
			parse(new FileInputStream(file));
			App.debug(String.format("Off file has ben load:(v=%d;e=%d;f=%d)",
					reader.getVertexCount(), reader.getEdgeCount(),
					reader.getEdgeCount()));
		} catch (FileNotFoundException e) {
			// It is unexpected as we already have checked existence GUI
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param offString
	 *            OFF content in form of string
	 */
	public void parse(String offString) {
		reader = new OFFReader(offString);
		updateAfterParsing();
	}

	/**
	 * 
	 * @param stream
	 *            off stream to read the content
	 */
	public void parse(InputStream stream) {
		try {
			reader = new OFFReader(new BufferedInputStream(stream));
			updateAfterParsing();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateAfterParsing() {

		AlgoDispatcher disp = kernel.getAlgoDispatcher();

		int vCount = reader.getVertexCount();
		GeoPointND[] vert = new GeoPointND[vCount];

		List<Coords> vertices = reader.getVertices();

		// Create vertices
		for (int i = 0; i < vCount; i++) {
			vert[i] = geoPoint(vertices.get(i));
		}

		// process all faces
		int index = 0;
		for (int[] fs : reader.getFaces()) {
			int s = fs.length;
			GeoPointND[] geoPs = new GeoPointND[s];

			// Create faces (Polygon)
			for (int i = 0; i < s; i++)
				geoPs[i] = vert[fs[i]];
			// FIXME: better to add a method in AlgoDispatcher for this
			AlgoPolygon3D algo = new AlgoPolygon3D(construction, geoPs,
					false, null);
			GeoPolygon3D polygon = (GeoPolygon3D) algo.getOutput()[0];
			boolean hasColor = reader.hasColor(index);
			if (polygon.isDefined()) {
				polygon.setLabel(null);
				if (hasColor) {
					polygon.setObjColor(reader.getFaceColor(index));
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
						polygon.setObjColor(reader.getFaceColor(index));
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
}
