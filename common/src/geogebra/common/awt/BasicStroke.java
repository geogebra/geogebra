package geogebra.common.awt;



public interface BasicStroke {
	public static final int CAP_BUTT = 0;
	public static final int CAP_ROUND = 1;
	public static final int CAP_SQUARE = 2;
	public static final int JOIN_BEVEL = 2;
	public static final int JOIN_MITER = 0;
	public static final int JOIN_ROUND = 1;
	Shape createStrokedShape(Shape shape);
	int getEndCap();
	float getMiterLimit();
	int getLineJoin();

	

}
