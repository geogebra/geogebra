package geogebra.common.kernel.geos;

import geogebra.common.kernel.Path;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoPointND;

public interface GeoConicPartInterface extends GeoConicInterface, Path {

	// temporary, of course
	/** conic arc*/
	public static final int CONIC_PART_ARC = 1;
	/** conic sector */
	public static final int CONIC_PART_SECTOR = 2;

	public void set(GeoElement geo);
	public void setParameters(double a, double b, boolean positiveOrientation);
	public void setUndefined();
	public GeoLine[] getLines();
	public GeoVec2D getTranslationVector();
	public boolean positiveOrientation();
	public GeoPoint2 getPointParam(double param);
	public double getParameterStart();
	public double getParameterEnd();
}
