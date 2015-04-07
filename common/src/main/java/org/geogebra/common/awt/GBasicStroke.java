package org.geogebra.common.awt;

public interface GBasicStroke {
	public static final int CAP_BUTT = 0;// Java & GWT
	public static final int CAP_ROUND = 1;// Java & GWT
	public static final int CAP_SQUARE = 2;// Java & GWT
	public static final int JOIN_MITER = 0;// Java
	public static final int JOIN_ROUND = 1;// Java
	public static final int JOIN_BEVEL = 2;// Java

	GShape createStrokedShape(GShape shape);

	int getEndCap();

	float getMiterLimit();

	int getLineJoin();

	float getLineWidth();

	float[] getDashArray();

}
