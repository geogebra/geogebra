package org.geogebra.desktop.geogebra3D.euclidian3D.printer3D;

import java.io.BufferedWriter;
import java.io.IOException;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.GeometriesSet;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.Geometry;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer.GeometryElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;

public class ExportToPrinter3DD extends ExportToPrinter3D {

	private BufferedWriter objBufferedWriter;

	private ManagerShadersElementsGlobalBuffer manager;

	private EuclidianView3D view;

	/**
	 * start file
	 * 
	 * @param writer
	 *            file writer
	 */
	public void startFile(BufferedWriter writer, EuclidianView3D view,
			ManagerShadersElementsGlobalBuffer manager) {
		objBufferedWriter = writer;
		this.view = view;
		this.manager = manager;
	}

	private Coords center = null;

	@Override
	public void export(Drawable3D d, Type type) {

		try {
			GeometriesSet currentGeometriesSet = manager
					.getGeometrySet(d.getGeometryIndex());
			if (type == Type.POINT) {
				center = ((DrawPoint3D) d).getCenter();
			} else {
				center = null;
			}
			if (currentGeometriesSet != null) {
				for (Geometry g : currentGeometriesSet) {
					GeometryElementsGlobalBuffer geometry = (GeometryElementsGlobalBuffer) g;

					GeoElement geo = d.getGeoElement();
					printToFile("\n///////////////////////\n// "
							+ geo.getGeoClassType() + ": "
							+ geo.getLabelSimple() + "\n");

					// object is a polyhedron
					printToFile("\npolyhedron(");

					// vertices
					boolean notFirst = false;
					printToFile("\n    points = [");
					GLBuffer fb = geometry.getVertices();
					for (int i = 0; i < geometry.getLength(); i++) {
						double x = fb.get();
						double y = fb.get();
						double z = fb.get();
						printVertexToFile(notFirst, x, y, z);
						notFirst = true;
					}
					printToFile("\n    ],");
					fb.rewind();

					// faces
					GLBufferIndices bi = geometry.getCurrentBufferI();
					printToFile("\n    faces = [");
					notFirst = false;
					for (int i = 0; i < geometry.getIndicesLength() / 3; i++) {
						int v1 = bi.get();
						int v2 = bi.get();
						int v3 = bi.get();
						printFaceToFile(notFirst, v1, v2, v3);
						notFirst = true;
					}
					bi.rewind();

					if (type == Type.CURVE) {
						// face for start
						for (int i = 1; i < 7; i++) {
							printFaceToFile(notFirst, 0, i, i + 1);
						}
					}

					// update index
					int l = geometry.getLength();

					if (type == Type.CURVE) {
						// face for end
						for (int i = 2; i < 8; i++) {
							printFaceToFile(notFirst, l - 1, l - i, l - i - 1);
						}
					}

					printToFile("\n    ],"); // end of faces

					// end of polyhedron
					printToFile("\nconvexity = 10);\n");

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printVertexToFile(boolean notFirst, double x, double y,
			double z) throws IOException {
		if (center != null) {
			double r = center.getW() * DrawPoint3D.DRAW_POINT_FACTOR
					/ view.getScale();
			x = center.getX() + x * r;
			y = center.getY() + y * r;
			z = center.getZ() + z * r;
		}
		if (notFirst) {
			printToFile(",");
		}
		printToFile("\n        [" + x + "," + y + "," + z + "]");
	}

	private void printFaceToFile(boolean notFirst, int v1, int v2, int v3)
			throws IOException {
		if (notFirst) {
			printToFile(",");
		}
		printToFile("\n        [" + v1 + "," + v2 + "," + v3 + "]");
	}

	private void printToFile(String s) throws IOException {
		// System.out.print(s);
		objBufferedWriter.write(s);
	}

}
