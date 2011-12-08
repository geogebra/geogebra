package geogebra.common.kernel.geos;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoPointND;

public interface GeoConicInterface extends GeoElementInterface, GeoConicNDConstants {

	// temporary methods, of course
	public boolean isCircle();
	public double[] getHalfAxes();
	public int getType();
	public double getCircleRadius();
	public Coords getMidpoint();
	public double getP();
	public void addPointOnConic(GeoPointND p);
	public void removePointOnConic(GeoPointND p);
	public GeoVec2DInterface getTranslationVector();//GeoVec2D
	public Coords getEigenvec(int i);
	public void setCoeffs(ExpressionValue[][] coeff);
	public void setInverseFill(boolean aboveBorder);
	public void fromLine(GeoLineInterface line);
}
