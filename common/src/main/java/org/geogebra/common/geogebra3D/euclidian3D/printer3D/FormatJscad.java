package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * OpenJSCAD format
 */
public class FormatJscad extends Format {

	@Override
	public String getExtension() {
		return "jscad";
	}

	@Override
	public void getScriptStart(StringBuilder sb) {
		sb.append("// Created with GeoGebra www.geogebra.org");
		appendNewline(sb);
		sb.append("function main() {");
		appendNewline(sb);
		sb.append("    var s=[];");
	}

	@Override
	public void getScriptEnd(StringBuilder sb) {
		sb.append("    return union(s);");
		appendNewline(sb);
		sb.append("}");
	}

	@Override
	public void getObjectStart(StringBuilder sb, String type, GeoElement geo, boolean transparency,
			GColor color, double alpha) {
		appendNewline(sb);
		sb.append("    // ");
		sb.append(type);
		sb.append(": ");
		sb.append(geo.getLabelSimple());
	}

	@Override
	public void getPolyhedronStart(StringBuilder sb) {
		appendNewline(sb);
		sb.append("    s.push(polyhedron({");
	}

	@Override
	public void getPolyhedronEnd(StringBuilder sb) {
		appendNewline(sb);
		sb.append("    }));");
		appendNewline(sb);
	}

	@Override
	public void getVerticesStart(StringBuilder sb, int count) {
		appendNewline(sb);
		sb.append("        points : [");
	}

	@Override
	public void getVertices(StringBuilder sb, double x, double y, double z) {
		appendNewline(sb);
		sb.append("            [");
		sb.append(x);
		sb.append(",");
		sb.append(y);
		sb.append(",");
		sb.append(z);
		sb.append("]");
	}

	@Override
	public void getVertices(StringBuilder sb, double x, double y, double z,
			double thickness) {
		getVertices(sb, x, y, z);
	}

	@Override
	public void getVerticesSeparator(StringBuilder sb) {
		sb.append(",");
	}

	@Override
	public void getVerticesEnd(StringBuilder sb) {
		appendNewline(sb);
		sb.append("        ],");
	}

	@Override
	public void getFacesStart(StringBuilder sb, int count, boolean hasSpecificNormals) {
		appendNewline(sb);
		sb.append("        triangles : [");
	}

	@Override
	public boolean getFaces(StringBuilder sb, int v1, int v2, int v3, int normal) {
		appendNewline(sb);
		sb.append("            [");
		sb.append(v1);
		sb.append(",");
		sb.append(v3);
		sb.append(",");
		sb.append(v2);
		sb.append("]");
		return true; // value ignored
	}

	@Override
	public void getFacesSeparator(StringBuilder sb) {
		sb.append(",");
	}

	@Override
	public void getFacesEnd(StringBuilder sb) {
		appendNewline(sb);
		sb.append("        ]");
	}

	@Override
	public void getNormalsStart(StringBuilder sb, int count) {
		// not used
	}

	@Override
	public void getNormal(StringBuilder sb, double x, double y, double z, boolean withThickness) {
		// not used
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
		return false;
	}

	@Override
	public boolean useSpecificViewForExport() {
		return false;
	}

	@Override
	public void setScale(double scale) {
		// not used so far
	}

}
