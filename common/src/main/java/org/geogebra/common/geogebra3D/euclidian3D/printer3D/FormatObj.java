package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * .obj format
 *
 */
public class FormatObj implements Format {

	private int index;
	private int nextShift;

	@Override
	public String getExtension() {
		return "obj";
	}

	@Override
	public void getScriptStart(StringBuilder sb) {
		sb.append("### created by GeoGebra ###");
		index = 1;
		nextShift = 0;
	}

	@Override
	public void getScriptEnd(StringBuilder sb) {
		sb.append("### end ###");
	}

	@Override
	public void getObjectStart(StringBuilder sb, String type, GeoElement geo, boolean transparency,
			GColor color, double alpha) {
		sb.append("\n#########################\n### ");
		sb.append(geo.getLabelSimple());
		sb.append("\n");

	}

	@Override
	public void getPolyhedronStart(StringBuilder sb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getPolyhedronEnd(StringBuilder sb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getVerticesStart(StringBuilder sb, int count) {
		sb.append("\n");
		index += nextShift;
		nextShift = 0;
	}

	@Override
	public void getVertices(StringBuilder sb, double x, double y, double z) {
		sb.append("v ");
		sb.append(x);
		sb.append(" ");
		sb.append(y);
		sb.append(" ");
		sb.append(z);
		nextShift++;
	}

	@Override
	public void getVerticesSeparator(StringBuilder sb) {
		sb.append("\n");
	}

	@Override
	public void getVerticesEnd(StringBuilder sb) {
		sb.append("\n");
	}

	@Override
	public void getNormalsStart(StringBuilder sb, int count) {
		sb.append("\n");
	}

	@Override
	public void getNormal(StringBuilder sb, double x, double y, double z) {
		sb.append("vn ");
		sb.append(x);
		sb.append(" ");
		sb.append(y);
		sb.append(" ");
		sb.append(z);
	}

	@Override
	public void getNormalsSeparator(StringBuilder sb) {
		sb.append("\n");
	}

	@Override
	public void getNormalsEnd(StringBuilder sb) {
		sb.append("\n");
	}

	@Override
	public void getFacesStart(StringBuilder sb, int count, boolean hasSpecificNormals) {
		// not needed
	}

	@Override
	public void getFaces(StringBuilder sb, int v1, int v2, int v3, int normal) {
		if (normal < 0) {
			sb.append("f ");
			appendIndex(sb, v1);
			sb.append("//");
			appendIndex(sb, v1);
			sb.append(" ");
			appendIndex(sb, v2);
			sb.append("//");
			appendIndex(sb, v2);
			sb.append(" ");
			appendIndex(sb, v3);
			sb.append("//");
			appendIndex(sb, v3);
		} else {
			sb.append("f ");
			appendIndex(sb, v1);
			sb.append("//");
			appendIndex(sb, normal);
			sb.append(" ");
			appendIndex(sb, v2);
			sb.append("//");
			appendIndex(sb, normal);
			sb.append(" ");
			appendIndex(sb, v3);
			sb.append("//");
			appendIndex(sb, normal);
		}

	}

	private void appendIndex(StringBuilder sb, int i) {
		sb.append(i + index);
	}

	@Override
	public void getFacesSeparator(StringBuilder sb) {
		sb.append("\n");
	}

	@Override
	public void getFacesEnd(StringBuilder sb) {
		sb.append("\n");

	}

	@Override
	public boolean handlesSurfaces() {
		return true;
	}

	@Override
	public boolean needsClosedObjects() {
		return false;
	}

	@Override
	public boolean handlesNormals() {
		return true;
	}

	@Override
	public double getSurfaceThickness() {
		// not used
		return 0;
	}

}
