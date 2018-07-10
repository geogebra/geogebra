package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ReusableArrayList;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * OpenSCAD format
 */
public class FormatSTL implements Format {

	private ReusableArrayList<Double> verticesList = new ReusableArrayList<>();
	private ReusableArrayList<Double> normalsList = new ReusableArrayList<>();

	private Coords tmpCoords1 = new Coords(3);
	private Coords tmpCoords2 = new Coords(3);
	private Coords tmpCoords3 = new Coords(3);

	@Override
	public String getExtension() {
		return "stl";
	}

	@Override
	public void getScriptStart(StringBuilder sb) {
		sb.append("solid geogebra");
	}

	@Override
	public void getScriptEnd(StringBuilder sb) {
		sb.append("\nendsolid geogebra");
	}

	@Override
	public void getObjectStart(StringBuilder sb, String type, GeoElement geo, boolean transparency,
			GColor color, double alpha) {
		// nothing to do
	}

	@Override
	public void getPolyhedronStart(StringBuilder sb) {
		// nothing to do
	}

	@Override
	public void getPolyhedronEnd(StringBuilder sb) {
		// nothing to do
	}

	@Override
	public void getVerticesStart(StringBuilder sb, int count) {
		verticesList.setLength(0);
	}

	@Override
	public void getVertices(StringBuilder sb, double x, double y, double z) {
		verticesList.addValue(x);
		verticesList.addValue(y);
		verticesList.addValue(z);
	}

	@Override
	public void getVerticesSeparator(StringBuilder sb) {
		// nothing to do
	}

	@Override
	public void getVerticesEnd(StringBuilder sb) {
		// nothing to do
	}

	@Override
	public void getFacesStart(StringBuilder sb, int count, boolean hasSpecificNormals) {
		// nothing to do
	}

	@Override
	public void getFaces(StringBuilder sb, int v1, int v2, int v3, int normal) {
		double v1x = verticesList.get(3 * v1);
		double v1y = verticesList.get(3 * v1 + 1);
		double v1z = verticesList.get(3 * v1 + 2);
		double v2x = verticesList.get(3 * v2);
		double v2y = verticesList.get(3 * v2 + 1);
		double v2z = verticesList.get(3 * v2 + 2);
		double v3x = verticesList.get(3 * v3);
		double v3y = verticesList.get(3 * v3 + 1);
		double v3z = verticesList.get(3 * v3 + 2);

		// out normal
		double nx, ny, nz;
		switch (normal) {
		case ExportToPrinter3D.NORMAL_SAME_INDEX:
			// use first normal
			nx = normalsList.get(3 * v1);
			ny = normalsList.get(3 * v1 + 1);
			nz = normalsList.get(3 * v1 + 2);
			break;
		case ExportToPrinter3D.NORMAL_NOT_SET:
			// calculate normal from vertices
			tmpCoords1.set(v1x, v1y, v1z);
			tmpCoords2.set(v2x, v2y, v2z);
			tmpCoords3.set(v3x, v3y, v3z);
			tmpCoords2.setSub(tmpCoords2, tmpCoords1);
			tmpCoords3.setSub(tmpCoords3, tmpCoords1);
			tmpCoords1.setCrossProduct(tmpCoords2, tmpCoords3);
			tmpCoords1.normalize();
			nx = tmpCoords1.getX();
			ny = tmpCoords1.getY();
			nz = tmpCoords1.getZ();
			break;
		default:
			// use normal index
			nx = normalsList.get(3 * normal);
			ny = normalsList.get(3 * normal + 1);
			nz = normalsList.get(3 * normal + 2);
			break;
		}
		sb.append("\nfacet normal ");
		sb.append(nx);
		sb.append(" ");
		sb.append(ny);
		sb.append(" ");
		sb.append(nz);

		// vertices
		sb.append("\n    outer loop");
		sb.append("\n        vertex ");
		sb.append(v1x);
		sb.append(" ");
		sb.append(v1y);
		sb.append(" ");
		sb.append(v1z);
		sb.append("\n        vertex ");
		sb.append(v2x);
		sb.append(" ");
		sb.append(v2y);
		sb.append(" ");
		sb.append(v2z);
		sb.append("\n        vertex ");
		sb.append(v3x);
		sb.append(" ");
		sb.append(v3y);
		sb.append(" ");
		sb.append(v3z);
		sb.append("\n    endloop");

		// end
		sb.append("\nendfacet");
	}

	@Override
	public void getFacesSeparator(StringBuilder sb) {
		// nothing to do
	}

	@Override
	public void getFacesEnd(StringBuilder sb) {
		// nothing to do
	}

	@Override
	public void getNormalsStart(StringBuilder sb, int count) {
		normalsList.setLength(0);
	}

	@Override
	public void getNormal(StringBuilder sb, double x, double y, double z) {
		normalsList.addValue(x);
		normalsList.addValue(y);
		normalsList.addValue(z);
	}

	@Override
	public void getNormalsSeparator(StringBuilder sb) {
		// not used
	}

	@Override
	public void getNormalsEnd(StringBuilder sb) {
		// not used
	}

	@Override
	public boolean handlesSurfaces() {
		return false;
	}

	@Override
	public boolean needsClosedObjects() {
		return true;
	}

	@Override
	public boolean handlesNormals() {
		return true;
	}

	@Override
	public boolean useSpecificViewForExport() {
		return true;
	}

}
