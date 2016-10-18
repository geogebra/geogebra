package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.plugin.GeoClass;

/**
 * OpenSCAD format
 */
public class FormatJscad implements Format {

	public void getExtension(StringBuilder sb) {
		sb.append(".jscad");
	}

	public void getScriptStart(StringBuilder sb) {
		sb.append(
				"// Created with GeoGebra www.geogebra.org \nfunction main() {\n    var s=[];");
	}

	public void getScriptEnd(StringBuilder sb) {
		sb.append("    return union(s);\n}");
	}

	public void getObjectStart(StringBuilder sb, GeoClass type,
			String label) {
		sb.append("\n    // ");
		sb.append(type);
		sb.append(": ");
		sb.append(label);
	}

	public void getPolyhedronStart(StringBuilder sb) {
		sb.append("\n    s.push(polyhedron({");
	}

	public void getPolyhedronEnd(StringBuilder sb) {
		sb.append("\n    }));\n");
	}

	public void getVerticesStart(StringBuilder sb) {
		sb.append("\n        points : [");
	}

	public void getVertices(StringBuilder sb, double x, double y, double z) {
		sb.append("\n            [");
		sb.append(x);
		sb.append(",");
		sb.append(y);
		sb.append(",");
		sb.append(z);
		sb.append("]");
	}

	public void getVerticesSeparator(StringBuilder sb) {
		sb.append(",");
	}

	public void getVerticesEnd(StringBuilder sb) {
		sb.append("\n        ],");
	}

	public void getFacesStart(StringBuilder sb) {
		sb.append("\n        triangles : [");
	}

	public void getFaces(StringBuilder sb, int v1, int v2, int v3) {
		sb.append("\n            [");
		sb.append(v1);
		sb.append(",");
		sb.append(v3);
		sb.append(",");
		sb.append(v2);
		sb.append("]");
	}

	public void getFacesSeparator(StringBuilder sb) {
		sb.append(",");
	}

	public void getFacesEnd(StringBuilder sb) {
		sb.append("\n        ]");
	}



}
