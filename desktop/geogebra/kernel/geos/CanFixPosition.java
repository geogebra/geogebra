package geogebra.kernel.geos;

public interface CanFixPosition {
	public boolean isPositionFixed();

	public void setPositionFixed(boolean checkboxFixed);

	public void updateRepaint();
	

}
