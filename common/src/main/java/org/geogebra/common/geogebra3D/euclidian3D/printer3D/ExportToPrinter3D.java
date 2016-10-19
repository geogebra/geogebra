package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.GeometriesSet;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.Geometry;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer.GeometryElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.Convexity;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;

public abstract class ExportToPrinter3D {

	static public enum Type {
		CURVE, CURVE_CLOSED, SURFACE_CLOSED, POINT
	}

	private Format format;

	private ManagerShadersElementsGlobalBuffer manager;

	private EuclidianView3D view;

	private StringBuilder sb;

	private Coords center = null;

	private boolean reverse = false;

	/**
	 * constructor
	 */
	public ExportToPrinter3D() {
		format = new FormatJscad();
		// format = new FormatScad();
		sb = new StringBuilder();
	}

	/**
	 * set view and manager
	 * 
	 * @param view
	 *            3D view
	 * @param manager
	 *            geometries manager
	 */
	final protected void set(EuclidianView3D view,
			ManagerShadersElementsGlobalBuffer manager) {
		this.view = view;
		this.manager = manager;
	}

	public void export(Drawable3D d, Type type) {

		reverse = false;

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
	}

	public void export(GeoPolygon polygon, Coords[] vertices) {

		sb.setLength(0);

		// check if the polygon is convex
		Convexity convexity = polygon.getPolygonTriangulation().checkIsConvex();
		if (convexity != Convexity.NOT) {

			Coords n = polygon.getMainDirection();
			double delta = 3 * PlotterBrush.LINE3D_THICKNESS / view.getScale();
			double dx = n.getX() * delta;
			double dy = n.getY() * delta;
			double dz = n.getZ() * delta;
			int length = polygon.getPointsLength();

			reverse = polygon.getReverseNormalForDrawing()
					^ (convexity == Convexity.CLOCKWISE);

			format.getObjectStart(sb, polygon.getGeoClassType(),
					polygon.getLabelSimple());

			// object is a polyhedron
			format.getPolyhedronStart(sb);

			// vertices
			boolean notFirst = false;
			format.getVerticesStart(sb);
			for (int i = 0; i < length; i++) {
				Coords v = vertices[i];
				getVertex(notFirst, v.getX() + dx, v.getY() + dy,
						v.getZ() + dz);
				notFirst = true;
				getVertex(notFirst, v.getX() - dx, v.getY() - dy,
						v.getZ() - dz);
			}
			format.getVerticesEnd(sb);

			// faces
			format.getFacesStart(sb);
			notFirst = false;
			for (int i = 1; i < length - 1; i++) {
				getFace(notFirst, 0, 2 * i, 2 * (i + 1)); // bottom
				notFirst = true;
				getFace(notFirst, 1, 2 * (i + 1) + 1, 2 * i + 1); // bottom
			}

			for (int i = 0; i < length; i++) { // side
				getFace(notFirst, 2 * i, 2 * i + 1, (2 * i + 3) % (2 * length));
				getFace(notFirst, 2 * i, (2 * i + 3) % (2 * length),
						(2 * i + 2) % (2 * length));
			}

			format.getFacesEnd(sb); // end of faces

			// end of polyhedron
			format.getPolyhedronEnd(sb);

			printToFile(sb.toString());


		} else {
			// TODO implement non convex polygons
		}

	}

	/**
	 * 
	 * @return 3D printer format
	 */
	public Format getFormat() {
		return format;
	}
	
	abstract protected void printToFile(String s);

	private void getVertex(boolean notFirst, double x, double y, double z) {
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

		if (reverse) {
			format.getFaces(sb, v1, v3, v2);
		} else {
			format.getFaces(sb, v1, v2, v3);
		}
	}


}
