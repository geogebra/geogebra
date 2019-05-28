package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ReusableArrayList;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * STL format
 */
public class FormatSTL extends Format {

	private ReusableArrayList<Double> verticesList = new ReusableArrayList<>();
	private ReusableArrayList<Double> normalsList = new ReusableArrayList<>();

	private Coords tmpCoords1 = new Coords(3);
	private Coords tmpCoords2 = new Coords(3);
	private Coords tmpCoords3 = new Coords(3);
	private Coords n = new Coords(3);

	protected double scale;

	private boolean usesThickness;

	private FormatPolygonsHandler polygonHandler;

	/**
	 * constructor
	 */
	public FormatSTL() {
		usesThickness = true;
	}

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
		if (!usesThickness) {
			polygonHandler.setOrientedNormals();
		}
		appendNewline(sb);
		sb.append("endsolid geogebra");
	}

	@Override
	public void getObjectStart(StringBuilder sb, String type, GeoElement geo, boolean transparency,
			GColor color, double alpha) {
		// nothing to do
	}

	@Override
	public void getPolyhedronStart(StringBuilder sb) {
		if (!usesThickness) {
			polygonHandler.startPolygon();
		}
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
		if (usesThickness) {
			verticesList.addValue(x * scale);
			verticesList.addValue(y * scale);
			verticesList.addValue(z * scale);
		} else {
			polygonHandler.addVertex(x * scale, y * scale, z * scale);
		}

	}

	@Override
	public void getVertices(StringBuilder sb, double x, double y, double z,
			double thickness) {
		int index = verticesList.getLength();
		double nx = normalsList.get(index);
		double ny = normalsList.get(index + 1);
		double nz = normalsList.get(index + 2);

		verticesList.addValue((x + nx * thickness) * scale);
		verticesList.addValue((y + ny * thickness) * scale);
		verticesList.addValue((z + nz * thickness) * scale);

		verticesList.addValue((x - nx * thickness) * scale);
		verticesList.addValue((y - ny * thickness) * scale);
		verticesList.addValue((z - nz * thickness) * scale);
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
	public boolean getFaces(StringBuilder sb, int v1, int v2, int v3, int normal) {
		if (!usesThickness) {
			polygonHandler.addTriangle(v1, v2, v3);
			return true;
		}

		double v1x = verticesList.get(3 * v1);
		double v1y = verticesList.get(3 * v1 + 1);
		double v1z = verticesList.get(3 * v1 + 2);
		double v2x = verticesList.get(3 * v2);
		double v2y = verticesList.get(3 * v2 + 1);
		double v2z = verticesList.get(3 * v2 + 2);
		double v3x = verticesList.get(3 * v3);
		double v3y = verticesList.get(3 * v3 + 1);
		double v3z = verticesList.get(3 * v3 + 2);

		// calculate normal from vertices
		tmpCoords1.set(v1x, v1y, v1z);
		tmpCoords2.set(v2x, v2y, v2z);
		tmpCoords3.set(v3x, v3y, v3z);
		tmpCoords2.setSub(tmpCoords2, tmpCoords1);
		tmpCoords3.setSub(tmpCoords3, tmpCoords1);
		tmpCoords1.setCrossProduct3(tmpCoords2, tmpCoords3);
		tmpCoords1.normalize();

		// out normal
		switch (normal) {
		case ExportToPrinter3D.NORMAL_SAME_INDEX:
			// use first normal
			n.setX(normalsList.get(3 * v1));
			n.setY(normalsList.get(3 * v1 + 1));
			n.setZ(normalsList.get(3 * v1 + 2));
			break;
		case ExportToPrinter3D.NORMAL_NOT_SET:
			// use normals from vertices
			n.set3(tmpCoords1);
			break;
		default:
			// use normal index
			n.setX(normalsList.get(3 * normal));
			n.setY(normalsList.get(3 * normal + 1));
			n.setZ(normalsList.get(3 * normal + 2));
			break;
		}
		appendNewline(sb);
		sb.append("facet normal ");
		appendValue(sb, n.getX());
		sb.append(" ");
		appendValue(sb, n.getY());
		sb.append(" ");
		appendValue(sb, n.getZ());

		// vertex 1
		appendNewline(sb);
		sb.append("    outer loop");
		appendNewline(sb);
		sb.append("        vertex ");
		appendValue(sb, v1x);
		sb.append(" ");
		appendValue(sb, v1y);
		sb.append(" ");
		appendValue(sb, v1z);
		appendNewline(sb);
		boolean notReversed = normal == ExportToPrinter3D.NORMAL_NOT_SET
				|| tmpCoords1.dotproduct(n) > 0;
		if (notReversed) {
			// vertex 2
			sb.append("        vertex ");
			appendValue(sb, v2x);
			sb.append(" ");
			appendValue(sb, v2y);
			sb.append(" ");
			appendValue(sb, v2z);
			appendNewline(sb);
			// vertex 3
			sb.append("        vertex ");
			appendValue(sb, v3x);
			sb.append(" ");
			appendValue(sb, v3y);
			sb.append(" ");
			appendValue(sb, v3z);
			appendNewline(sb);
		} else {
			// vertex 3
			sb.append("        vertex ");
			appendValue(sb, v3x);
			sb.append(" ");
			appendValue(sb, v3y);
			sb.append(" ");
			appendValue(sb, v3z);
			appendNewline(sb);
			// vertex 2
			sb.append("        vertex ");
			appendValue(sb, v2x);
			sb.append(" ");
			appendValue(sb, v2y);
			sb.append(" ");
			appendValue(sb, v2z);
			appendNewline(sb);
		}
		sb.append("    endloop");

		// end
		appendNewline(sb);
		sb.append("endfacet");
		return notReversed;
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
	public void getNormal(StringBuilder sb, double x, double y, double z, boolean withThickness) {
		if (usesThickness) {
			normalsList.addValue(x);
			normalsList.addValue(y);
			normalsList.addValue(z);
			if (withThickness) {
				normalsList.addValue(-x);
				normalsList.addValue(-y);
				normalsList.addValue(-z);
			}
		} else {
			polygonHandler.setNormal(x, y, z);
		}
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
		return usesThickness;
	}

	@Override
	public boolean handlesNormals() {
		return true;
	}

	@Override
	public boolean useSpecificViewForExport() {
		return true;
	}

	@Override
	public void setScale(double scale) {
		this.scale = scale;
	}

	private static void appendValue(StringBuilder sb, double v) {
		int v1 = (int) Math.abs(v * 100);
		int integerValue = v1 / 100;
		int decimals = v1 % 100;
		if (v < 0) {
			sb.append("-");
		}
		sb.append(integerValue);
		sb.append(".");
		if (decimals < 10) {
			sb.append("0");
		}
		sb.append(decimals);
	}

	@Override
	public boolean needsScale() {
		return true;
	}

	@Override
	public boolean needsBothSided() {
		return usesThickness;
	}

	@Override
	public void setUsesThickness(boolean flag) {
		usesThickness = flag;
		if (!usesThickness) {
			polygonHandler = new FormatPolygonsHandler();
		}
	}

	public boolean exportsOnlyPolygons() {
		return !usesThickness;
	}
}
