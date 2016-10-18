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
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatJscad;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;

public class ExportToPrinter3DD extends ExportToPrinter3D {

	private BufferedWriter objBufferedWriter;

	private ManagerShadersElementsGlobalBuffer manager;

	private EuclidianView3D view;

	private StringBuilder sb;

	/**
	 * constructor
	 */
	public ExportToPrinter3DD() {
		format = new FormatJscad();
		// format = new FormatScad();
		sb = new StringBuilder();
	}

	/**
	 * start file
	 * 
	 * @param writer
	 *            file writer
	 * @param view
	 *            3D view
	 * @param manager
	 *            geometries manager
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
				sb.setLength(0);
				for (Geometry g : currentGeometriesSet) {

					GeometryElementsGlobalBuffer geometry = (GeometryElementsGlobalBuffer) g;

					GeoElement geo = d.getGeoElement();
					format.getObjectStart(sb, geo.getGeoClassType(),
							geo.getLabelSimple());

					// object is a polyhedron
					format.getPolyhedronStart(sb);

					// vertices
					boolean notFirst = false;
					format.getVerticesStart(sb);
					GLBuffer fb = geometry.getVertices();
					for (int i = 0; i < geometry.getLength(); i++) {
						double x = fb.get();
						double y = fb.get();
						double z = fb.get();
						getVertex(notFirst, x, y, z);
						notFirst = true;
					}
					format.getVerticesEnd(sb);
					fb.rewind();

					// faces
					GLBufferIndices bi = geometry.getCurrentBufferI();
					format.getFacesStart(sb);
					notFirst = false;
					for (int i = 0; i < geometry.getIndicesLength() / 3; i++) {
						int v1 = bi.get();
						int v2 = bi.get();
						int v3 = bi.get();
						getFace(notFirst, v1, v2, v3);
						notFirst = true;
					}
					bi.rewind();

					if (type == Type.CURVE) {
						// face for start
						for (int i = 1; i < 7; i++) {
							getFace(notFirst, 0, i, i + 1);
						}
					}

					// update index
					int l = geometry.getLength();

					if (type == Type.CURVE) {
						// face for end
						for (int i = 2; i < 8; i++) {
							getFace(notFirst, l - 1, l - i, l - i - 1);
						}
					}

					format.getFacesEnd(sb); // end of faces

					// end of polyhedron
					format.getPolyhedronEnd(sb);

				}

				printToFile(sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getVertex(boolean notFirst, double x,
			double y, double z) {
		if (center != null) {
			double r = center.getW() * DrawPoint3D.DRAW_POINT_FACTOR
					/ view.getScale();
			x = center.getX() + x * r;
			y = center.getY() + y * r;
			z = center.getZ() + z * r;
		}
		if (notFirst) {
			format.getVerticesSeparator(sb);
		}
		format.getVertices(sb, x, y, z);
	}

	private void getFace(boolean notFirst, int v1, int v2, int v3) {
		if (notFirst) {
			format.getFacesSeparator(sb);
		}
		format.getFaces(sb, v1, v2, v3);
	}

	private void printToFile(String s) throws IOException {
		// System.out.print(s);
		objBufferedWriter.write(s);
	}

}
