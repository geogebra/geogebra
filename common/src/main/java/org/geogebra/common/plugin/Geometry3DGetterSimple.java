package org.geogebra.common.plugin;

/**
 * Simple geometry getter with 4 lines:
 * 
 * v x y z x y z ... (vertices)
 * 
 * n x y z x y z ... (normals)
 * 
 * c r g b a r g b a ... (colors)
 * 
 * t i1 i2 i3 ... (triangles indices)
 *
 */
public class Geometry3DGetterSimple implements Geometry3DGetter {

	private StringBuilder vsb;
	private StringBuilder nsb;
	private StringBuilder csb;
	private StringBuilder tsb;
	private int index;
	private int nextShift;

	/**
	 * constructor
	 */
	public Geometry3DGetterSimple() {
		vsb = new StringBuilder("v");
		nsb = new StringBuilder("n");
		csb = new StringBuilder("c");
		tsb = new StringBuilder("t");
		index = 0;
		nextShift = 0;
	}

	public boolean handles(GeometryType type) {
		return type == GeometryType.CURVE;
	}

	public void startGeometry(GeometryType type) {
		index += nextShift;
		nextShift = 0;
	}

	@Override
	public void addVertexNormalColor(double x, double y, double z, double nx,
			double ny, double nz, double r, double g, double b, double a) {
		vsb.append(" ");
		vsb.append(x);
		vsb.append(" ");
		vsb.append(y);
		vsb.append(" ");
		vsb.append(z);
	
		nsb.append(" ");
		nsb.append(nx);
		nsb.append(" ");
		nsb.append(ny);
		nsb.append(" ");
		nsb.append(nz);
		
		csb.append(" ");
		csb.append(r);
		csb.append(" ");
		csb.append(g);
		csb.append(" ");
		csb.append(b);
		csb.append(" ");
		csb.append(a);

		nextShift++;

	}

	@Override
	public void addTriangle(int v1, int v2, int v3) {
		tsb.append(" ");
		appendIndex(tsb, v1);
		tsb.append(" ");
		appendIndex(tsb, v2);
		tsb.append(" ");
		appendIndex(tsb, v3);
	}

	private void appendIndex(StringBuilder sb, int i) {
		sb.append(i + index);
	}

	/**
	 * 
	 * @return result
	 */
	public StringBuilder get() {
		StringBuilder sb = new StringBuilder();
		sb.append(vsb);
		sb.append("\n");
		sb.append(nsb);
		sb.append("\n");
		sb.append(csb);
		sb.append("\n");
		sb.append(tsb);
		sb.append("\n");
		return sb;
	}

}
