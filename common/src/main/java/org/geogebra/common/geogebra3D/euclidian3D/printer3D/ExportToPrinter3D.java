package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
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
import org.geogebra.common.kernel.discrete.PolygonTriangulation;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.Convexity;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;
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
		// format = new FormatCollada();
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
		if (type == Type.POINT) {
			center = ((DrawPoint3D) d).getCenter();
		} else {
			center = null;
		}
		GeoElement geo = d.getGeoElement();
		export(d.getGeometryIndex(), type, geo.getGeoClassType().toString(), geo);
		if (type == Type.CURVE_CLOSED) {
			exportSurface(d.getGeoElement(), d.getSurfaceIndex());
		}
	}

	public void export(int geometryIndex, Type type, String geoType,
			GeoElement geo) {

		reverse = false;
		GeometriesSet currentGeometriesSet = manager
				.getGeometrySet(geometryIndex);


		if (currentGeometriesSet != null) {
			sb.setLength(0);
			for (Geometry g : currentGeometriesSet) {

				GeometryElementsGlobalBuffer geometry = (GeometryElementsGlobalBuffer) g;

				format.getObjectStart(sb, geoType, geo, false, null, 1);

				// object is a polyhedron
				format.getPolyhedronStart(sb);

				// vertices
				boolean notFirst = false;
				format.getVerticesStart(sb, geometry.getLength());
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
				int length = geometry.getIndicesLength() / 3;
				format.getFacesStart(sb, length, false);
				notFirst = false;
				for (int i = 0; i < length; i++) {
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
			reverse = false;
			GeoElement geo = d.getGeoElement();
			export(geo, d.getGeometryIndex(), "SURFACE_MESH", false, GColor.BLACK, 1);
			exportSurface(geo, d.getSurfaceIndex());
		}
	}

	public void exportSurface(Drawable3D d) {
		exportSurface(d.getGeoElement(), d.getSurfaceIndex());
	}

	private void exportSurface(GeoElement geo, int index) {
		double alpha = geo.getAlphaValue();
		reverse = false;
		export(geo, index, "SURFACE", true, null, alpha);
		reverse = true;
		export(geo, index, "SURFACE", true, null, alpha);
	}

	private void export(GeoElement geo, int geometryIndex, String group, boolean transparency, GColor color,
			double alpha) {

		if (alpha < 0.001) {
			return;
		}

		GeometriesSet currentGeometriesSet = manager
				.getGeometrySet(geometryIndex);
		if (currentGeometriesSet != null) {
			sb.setLength(0);
			for (Geometry g : currentGeometriesSet) {

				GeometryElementsGlobalBuffer geometry = (GeometryElementsGlobalBuffer) g;

				format.getObjectStart(sb, group, geo, transparency, color, alpha);

				// object is a polyhedron
				format.getPolyhedronStart(sb);

				// vertices
				boolean notFirst = false;
				format.getVerticesStart(sb, geometry.getLength());
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
				switch (geometry.getType()) {
				case TRIANGLE_FAN:
					// for openGL we use replace triangle fans by triangle strips, repeating apex
					// every time
					int length = geometry.getIndicesLength() / 2;
					format.getFacesStart(sb, length - 1, false);
					notFirst = false;
					int v3 = bi.get();
					int v4 = bi.get();
					for (int i = 1; i < length; i++) {
						int v1 = v3;
						int v2 = v4;
						v3 = bi.get();
						v4 = bi.get();
						getFace(notFirst, v1, v2, v4);
						notFirst = true;
					}
					break;
				case TRIANGLE_STRIP:
					length = geometry.getIndicesLength() / 2;
					format.getFacesStart(sb, (length - 1) * 2, false);
					notFirst = false;
					v3 = bi.get();
					v4 = bi.get();
					for (int i = 1; i < length; i++) {
						int v1 = v3;
						int v2 = v4;
						v3 = bi.get();
						v4 = bi.get();
						getFace(notFirst, v1, v2, v3);
						notFirst = true;
						getFace(notFirst, v2, v4, v3);
					}
					break;
				case TRIANGLES:
				default:
					length = geometry.getIndicesLength() / 3;
					format.getFacesStart(sb, length, false);
					notFirst = false;
					for (int i = 0; i < length; i++) {
						int v1 = bi.get();
						int v2 = bi.get();
						v3 = bi.get();
						getFace(notFirst, v1, v2, v3);
						notFirst = true;
					}
					break;
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
				format.getNormalsStart(sb, geometry.getLength());
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

	public void export(GeoPolygon polygon, Coords[] vertices, GColor color, double alpha) {

		if (alpha < 0.001) {
			return;
		}

		sb.setLength(0);

		PolygonTriangulation pt = polygon.getPolygonTriangulation();
		if (pt.getMaxPointIndex() > 2) {
			Coords n = polygon.getMainDirection();
			double delta;
			if (differentAxisRatio) {
				if (format.needsClosedObjects()) {
					delta = 3 * PlotterBrush.LINE3D_THICKNESS;
				} else {
					delta = PlotterBrush.LINE3D_THICKNESS / 2;
				}
				if (view.scaleAndNormalizeNormalXYZ(n, tmpNormal)) {
					n = tmpNormal;
				}
			} else {
				delta = 3 * PlotterBrush.LINE3D_THICKNESS / view.getScale();
			}

			double dx = 0, dy = 0, dz = 0;
			if (format.needsClosedObjects()) {
				dx = n.getX() * delta;
				dy = n.getY() * delta;
				dz = n.getZ() * delta;
			}

			// check if the polygon is convex
			Convexity convexity = polygon.getPolygonTriangulation().checkIsConvex();
			if (convexity != Convexity.NOT) {
				int length = polygon.getPointsLength();

				reverse = polygon.getReverseNormalForDrawing() ^ (convexity == Convexity.CLOCKWISE);
				if (!format.needsClosedObjects()) {
					reverse = !reverse; // TODO fix that
				}

				format.getObjectStart(sb, polygon.getGeoClassType().toString(), polygon, true, color, alpha);

				// object is a polyhedron
				format.getPolyhedronStart(sb);

				// vertices
				boolean notFirst = false;
				format.getVerticesStart(sb, format.needsClosedObjects() ? length * 2 : length);
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
					if (format.needsClosedObjects()) {
						getVertex(notFirst, x + dx, y + dy, z + dz);
						notFirst = true;
						getVertex(notFirst, x - dx, y - dy, z - dz);
					} else {
						getVertex(notFirst, x, y, z);
						notFirst = true;
					}
				}
				format.getVerticesEnd(sb);

				// normal
				if (format.handlesNormals()) {
					format.getNormalsStart(sb, 2);
					getNormalHandlingReverse(-n.getX(), -n.getY(), -n.getZ());
					getNormalHandlingReverse(n.getX(), n.getY(), n.getZ());
					format.getNormalsEnd(sb);
				}

				// faces
				format.getFacesStart(sb, format.needsClosedObjects() ? (length - 2) * 2 + 2 : (length - 2) * 2, true);
				notFirst = false;

				for (int i = 1; i < length - 1; i++) {
					if (format.needsClosedObjects()) {
						getFace(notFirst, 0, 2 * i, 2 * (i + 1), 0); // top
						notFirst = true;
						getFace(notFirst, 1, 2 * (i + 1) + 1, 2 * i + 1, 1); // bottom
					} else {
						getFace(notFirst, 0, i, i + 1, 0); // top
						notFirst = true;
						getFace(notFirst, 0, i + 1, i, 1); // bottom
					}
				}

				if (format.needsClosedObjects()) {
					for (int i = 0; i < length; i++) { // side
						getFace(notFirst, 2 * i, 2 * i + 1, (2 * i + 3) % (2 * length));
						getFace(notFirst, 2 * i, (2 * i + 3) % (2 * length), (2 * i + 2) % (2 * length));
					}
				}

				format.getFacesEnd(sb); // end of faces

				// end of polyhedron
				format.getPolyhedronEnd(sb);

				printToFile(sb.toString());

			} else {
				if (!format.needsClosedObjects()) { // TODO for 3D printing
					int length = polygon.getPointsLength();
					Coords[] verticesWithIntersections = pt.getCompleteVertices(vertices, length);
					int completeLength = pt.getMaxPointIndex();
					reverse = false;

					format.getObjectStart(sb, polygon.getGeoClassType().toString(), polygon, true, color, alpha);

					// object is a polyhedron
					format.getPolyhedronStart(sb);

					// vertices
					boolean notFirst = false;
					format.getVerticesStart(sb, completeLength);
					for (int i = 0; i < completeLength; i++) {
						Coords v = verticesWithIntersections[i];
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
						getVertex(notFirst, x, y, z);
						notFirst = true;
					}
					format.getVerticesEnd(sb);

					// normal
					if (format.handlesNormals()) {
						format.getNormalsStart(sb, 2);
						getNormalHandlingReverse(n.getX(), n.getY(), n.getZ());
						getNormalHandlingReverse(-n.getX(), -n.getY(), -n.getZ());
						format.getNormalsEnd(sb);
					}

					// faces
					int size = 0;
					ArrayList<TriangleFan> triFanList = pt.getTriangleFans();
					for (TriangleFan triFan : triFanList) {
						size += triFan.size() - 1;
					}
					format.getFacesStart(sb, size * 2, true);
					notFirst = false;

					for (TriangleFan triFan : triFanList) {
						int apex = triFan.getApexPoint();
						int current = triFan.getVertexIndex(0);
						for (int i = 1; i < triFan.size(); i++) {
							int old = current;
							current = triFan.getVertexIndex(i);
							getFace(notFirst, apex, old, current, 0); // top
							notFirst = true;
							getFace(notFirst, apex, current, old, 1); // bottom
						}
					}

					format.getFacesEnd(sb); // end of faces

					// end of polyhedron
					format.getPolyhedronEnd(sb);

					printToFile(sb.toString());

				}

			}
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
		if (reverse) {
			getNormalHandlingReverse(-x, -y, -z);
		} else {
			getNormalHandlingReverse(x, y, z);
		}
		format.getNormalsSeparator(sb);
	}
	
	private void getNormalHandlingReverse(double x, double y, double z) {
		format.getNormal(sb, x, y, z);
		format.getNormalsSeparator(sb);
	}
	
	private void getFace(boolean notFirst, int v1, int v2, int v3) {
		getFace(notFirst, v1, v2, v3, -1);
	}

	private void getFace(boolean notFirst, int v1, int v2, int v3, int normal) {
		if (notFirst) {
			format.getFacesSeparator(sb);
		}

		if (reverse) {
			format.getFaces(sb, v1, v3, v2, normal);
		} else {
			format.getFaces(sb, v1, v2, v3, normal);
		}
	}
	
	public void start() {
		xInvScale = 1 / view.getXscale();
		differentAxisRatio = view.getApplication().has(Feature.DIFFERENT_AXIS_RATIO_3D);
	}
	
	abstract public void end();

}
