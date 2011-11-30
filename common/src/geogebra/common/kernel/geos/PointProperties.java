package geogebra.common.kernel.geos;

public interface PointProperties {
	public void setPointSize(int size);
	public int getPointSize();
	public void setPointStyle(int type);
	public int getPointStyle();
	public void updateRepaint();
}