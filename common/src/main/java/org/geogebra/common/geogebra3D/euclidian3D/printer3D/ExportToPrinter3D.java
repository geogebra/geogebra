package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSurface3DElements;
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
import org.geogebra.common.main.Feature;

public abstract class ExportToPrinter3D {

	static public enum Type {
		CURVE, CURVE_CLOSED, SURFACE_CLOSED, POINT
	}

	private Format format;

	private ManagerShadersElementsGlobalBuffer manager;

	protected EuclidianView3D view;

	private StringBuilder sb;

	private Coords center = null;

	private boolean reverse = false;
	
	private double xInvScale;
	private boolean differentAxisRatio = false;
	private Coords tmpNormal = new Coords(3);

	/**
	 * constructor
	 */
	public ExportToPrinter3D() {
		format = new FormatJscad();
		// format = new FormatObj();
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
				format.getObjectStart(sb, geo.getGeoClassType().toString(),
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

				// normals
				getNormals(geometry);

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

				if (type == Type.CURVE && format.needsClosedObjects()) {
					// face for start
					for (int i = 1; i < 7; i++) {
						getFace(notFirst, 0, i, i + 1);
					}

					// update index
					int l = geometry.getLength();

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

	/**
	 * export surface
	 * 
	 * @param d
	 *            surface drawable
	 */
	public void export(DrawSurface3DElements d) {
		if (format.handlesSurfaces()) {
			export(d.getGeoElement(), d.getGeometryIndex(), "SURFACE_MESH");
			export(d.getGeoElement(), d.getSurfaceIndex(), "SURFACE");
		}
	}

	private void export(GeoElement geo, int geometryIndex, String group) {

		GeometriesSet currentGeometriesSet = manager
				.getGeometrySet(geometryIndex);
		if (currentGeometriesSet != null) {
			sb.setLength(0);
			for (Geometry g : currentGeometriesSet) {

				GeometryElementsGlobalBuffer geometry = (GeometryElementsGlobalBuffer) g;

				format.getObjectStart(sb, group,
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

				// normals
				getNormals(geometry);

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

				format.getFacesEnd(sb); // end of faces

				// end of polyhedron
				format.getPolyhedronEnd(sb);

			}

			printToFile(sb.toString());
		}
	}

	private void getNormals(GeometryElementsGlobalBuffer geometry) {
		if (format.handlesNormals()) {
			GLBuffer fb = geometry.getNormals();
			if (fb != null && !fb.isEmpty() && fb.capacity() > 3) {
				format.getNormalsStart(sb);
				for (int i = 0; i < geometry.getLength(); i++) {
					double x = fb.get();
					double y = fb.get();
					double z = fb.get();
					getNormal(x, y, z);
				}
				format.getNormalsEnd(sb);
				fb.rewind();
			}
		}
	}

	public void export(GeoPolygon polygon, Coords[] vertices) {

		sb.setLength(0);

		// check if the polygon is convex
		Convexity convexity = polygon.getPolygonTriangulation().checkIsConvex();
		if (convexity != Convexity.NOT) {

			Coords n = polygon.getMainDirection();
			double delta;
			if (differentAxisRatio) {
				delta = 3 * PlotterBrush.LINE3D_THICKNESS;
				if (view.scaleAndNormalizeNormalXYZ(n, tmpNormal)) {
					n = tmpNormal;
				}
			} else {
				delta = 3 * PlotterBrush.LINE3D_THICKNESS / view.getScale();
			}
			
			double dx = n.getX() * delta;
			double dy = n.getY() * delta;
			double dz = n.getZ() * delta;
			int length = polygon.getPointsLength();

			reverse = polygon.getReverseNormalForDrawing()
					^ (convexity == Convexity.CLOCKWISE);

			format.getObjectStart(sb, polygon.getGeoClassType().toString(),
					polygon.getLabelSimple());

			// object is a polyhedron
			format.getPolyhedronStart(sb);

			// vertices
			boolean notFirst = false;
			format.getVerticesStart(sb);
			for (int i = 0; i < length; i++) {
				Coords v = vertices[i];
				double x, y, z;
				if (differentAxisRatio) {
					x = v.getX() * view.getXscale();
					y = v.getY() * view.getYscale();
					z = v.getZ() * view.getZscale();
				} else {
					x = v.getX();
					y = v.getY();
					z = v.getZ();
				}
				getVertex(notFirst, x + dx, y + dy, z + dz);
				notFirst = true;
				getVertex(notFirst, x - dx, y - dy, z - dz);
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

	/**
	 * Print script
	 * 
	 * @param s
	 *            exported script
	 */
	abstract protected void printToFile(String s);

	private void getVertex(boolean notFirst, double x0, double y0, double z0) {
		double x = x0;
		double y = y0;
		double z = z0;
		if (center != null) {
			double r;
			if (differentAxisRatio) {
				r = center.getW() * DrawPoint3D.DRAW_POINT_FACTOR;
			} else {
				r = center.getW() * DrawPoint3D.DRAW_POINT_FACTOR
						/ view.getScale();
			}
			x = center.getX() + x * r;
			y = center.getY() + y * r;
			z = center.getZ() + z * r;
		}
		if (notFirst) {
			format.getVerticesSeparator(sb);
		}
		if (differentAxisRatio) {
			format.getVertices(sb, x * xInvScale, y * xInvScale, z * xInvScale);
		} else {
			format.getVertices(sb, x, y, z);
		}
	}
	
	private void getNormal(double x, double y, double z) {
		format.getNormal(sb, x, y, z);
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
	
	public void start() {
		xInvScale = 1 / view.getXscale();
		differentAxisRatio = view.getApplication().has(Feature.DIFFERENT_AXIS_RATIO_3D);
	}
	
	abstract public void end();

}
