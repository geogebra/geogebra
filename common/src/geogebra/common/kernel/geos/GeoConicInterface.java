package geogebra.common.kernel.geos;

import geogebra.common.kernel.Matrix.Coords;

public interface GeoConicInterface extends GeoElementInterface {

	// temporary methods, of course
	public boolean isCircle();
	public double[] getHalfAxes();
	public int getType();
	public double getCircleRadius();
	public Coords getMidpoint();
}
