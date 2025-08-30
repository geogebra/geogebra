package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3DForExport;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GeometriesSet;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Geometry;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Geometry3DGetter;
import org.geogebra.common.plugin.Geometry3DGetter.GeometryType;

/**
 * manages geometry getter
 *
 */
public class Geometry3DGetterManager {

	private Geometry3DGetter getter;
	private ManagerShaders geometriesManager;
	private double xInvScale;
	private int elementsOffset;

	/**
	 * constructor
	 * 
	 * @param view
	 *            view
	 * @param getter
	 *            geometry getter
	 */
	public Geometry3DGetterManager(EuclidianView3DForExport view, Geometry3DGetter getter) {
		this.getter = getter;
		geometriesManager = (ManagerShaders) view.getRenderer()
				.getGeometryManager();
		xInvScale = 1 / view.getXscale();
	}

	/**
	 * export geometry with color and alpha
	 * 
	 * @param geo
	 *            geo to export
	 * 
	 * @param geometryIndex
	 *            geometry index
	 * @param color
	 *            color
	 * @param alpha
	 *            opacity
	 * @param type
	 *            geometry type
	 */
	public void export(GeoElement geo, int geometryIndex, GColor color,
			double alpha, GeometryType type) {
		if (getter.handles(geo, type)) {
			GeometriesSet currentGeometriesSet = geometriesManager.getGeometrySet(geometryIndex);
			double red = color.getRed() / 255.0;
			double green = color.getGreen() / 255.0;
			double blue = color.getBlue() / 255.0;
			if (currentGeometriesSet != null) {
				for (Geometry geometry : currentGeometriesSet) {
					getter.startGeometry(type);
					geometry.initForExport();

					// vertices
					GLBuffer vb = geometry.getVerticesForExport();
					GLBuffer nb = geometry.getNormalsForExport();
					for (int i = 0; i < geometry.getLengthForExport(); i++) {
						double x = vb.get() * xInvScale;
						double y = vb.get() * xInvScale;
						double z = vb.get() * xInvScale;
						double nx = nb.get();
						double ny = nb.get();
						double nz = nb.get();
						getter.addVertexNormalColor(x, y, z, nx, ny, nz, red, green, blue, alpha);
					}
					vb.rewind();
					nb.rewind();

					// faces
					GLBufferIndices bi = geometry.getBufferIndices();
					elementsOffset = geometry.getElementsOffset();
					switch (geometry.getType()) {
					case TRIANGLE_FAN:
						// for openGL we use replace triangle fans by triangle
						// strips, repeating apex every time
						int length = geometry.getIndicesLength() / 2;
						int v3 = bi.get();
						int v4 = bi.get();
						for (int i = 1; i < length; i++) {
							int v1 = v3;
							int v2 = v4;
							v3 = bi.get();
							v4 = bi.get();
							addTriangle(v1, v2, v4);
						}
						break;
					case TRIANGLE_STRIP:
						length = geometry.getIndicesLength() / 2;
						v3 = bi.get();
						v4 = bi.get();
						for (int i = 1; i < length; i++) {
							int v1 = v3;
							int v2 = v4;
							v3 = bi.get();
							v4 = bi.get();
							addTriangle(v1, v2, v3);
							addTriangle(v2, v4, v3);
						}
						break;
					case TRIANGLES:
					default:
						length = geometry.getIndicesLength() / 3;
						for (int i = 0; i < length; i++) {
							int v1 = bi.get();
							int v2 = bi.get();
							v3 = bi.get();
							addTriangle(v1, v2, v3);
						}
						break;
					}
					bi.rewind();
				}
			}
		}
	}

	private void addTriangle(int v1, int v2, int v3) {
		getter.addTriangle(v1 - elementsOffset, v2 - elementsOffset,
				v3 - elementsOffset);
	}

}
