package geogebra.common.kernel.geos;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoPointND;

public interface GeoConicInterface extends GeoElementInterface {

	// temporary methods, of course
	public boolean isCircle();
	public double[] getHalfAxes();
	public int getType();
	public double getCircleRadius();
	public Coords getMidpoint();
	
	public void addPointOnConic(GeoPointND p);
	public void removePointOnConic(GeoPointND p);
	public Object getTranslationVector();//GeoVec2D
}
