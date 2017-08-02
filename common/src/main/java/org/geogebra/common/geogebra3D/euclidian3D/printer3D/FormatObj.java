package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

/**
 * OpenSCAD format
 */
public class FormatObj implements Format {

	private int vertexShift, vertexSize, normalShift, normalSize;
	boolean hasNormals;

	@Override
	public void getExtension(StringBuilder sb) {
		sb.append(".obj");
	}

	@Override
	public void getScriptStart(StringBuilder sb) {
		vertexSize = 0;
		normalSize = 0;
		sb.append(
				"# Created with GeoGebra www.geogebra.org");
	}

	@Override
	public void getScriptEnd(StringBuilder sb) {
		sb.append("");
	}

	@Override
	public void getObjectStart(StringBuilder sb, String type, String label) {
		vertexShift = vertexSize + 1;
		normalShift = normalSize + 1;
		hasNormals = false;
		sb.append("\n ######## ");
		sb.append(label);
		sb.append("\n g ");
		sb.append(type);
	}

	@Override
	public void getPolyhedronStart(StringBuilder sb) {
		// sb.append("");
	}

	@Override
	public void getPolyhedronEnd(StringBuilder sb) {
		// sb.append("");
	}

	@Override
	public void getVerticesStart(StringBuilder sb) {
		sb.append("\n        # vertices");
	}

	@Override
	public void getVertices(StringBuilder sb, double x, double y, double z) {
		sb.append("\n            v ");
		sb.append(x);
		sb.append(" ");
		sb.append(y);
		sb.append(" ");
		sb.append(z);
		vertexSize++;
		// sb.append("]");
	}

	@Override
	public void getVerticesSeparator(StringBuilder sb) {
		// sb.append("");
	}

	@Override
	public void getVerticesEnd(StringBuilder sb) {
		sb.append("\n");
	}

	@Override
	public void getFacesStart(StringBuilder sb) {
		sb.append("\n        # triangles:");
	}

	@Override
	public void getFaces(StringBuilder sb, int v1, int v2, int v3) {
		sb.append("\n            f ");
		appendIndex(sb, v1);
		sb.append(" ");
		appendIndex(sb, v2);
		sb.append(" ");
		appendIndex(sb, v3);
		// sb.append("");
	}

	private void appendIndex(StringBuilder sb, int index) {
		sb.append(index + vertexShift);
		if (hasNormals) {
			sb.append("//");
			sb.append(index + normalShift);
		}
	}

	@Override
	public void getFacesSeparator(StringBuilder sb) {
		// sb.append("");
	}

	@Override
	public void getFacesEnd(StringBuilder sb) {
		sb.append("\n");
	}

	@Override
	public void getListType(StringBuilder sb, int type) {
		sb.append("\n  g ");
		sb.append(type);
	}

	@Override
	public void getNormalsStart(StringBuilder sb) {
		hasNormals = true;
		sb.append("\n        # normals");
	}

	@Override
	public void getNormal(StringBuilder sb, double x, double y, double z) {
		sb.append("\n            vn ");
		sb.append(x);
		sb.append(" ");
		sb.append(y);
		sb.append(" ");
		sb.append(z);
		normalSize++;
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

}
