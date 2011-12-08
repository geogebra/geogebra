package geogebra.common.kernel.geos;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoPointND;

public interface GeoConicInterface extends GeoConicNDInterface {

	// temporary methods, of course
	public void setCoeffs(ExpressionValue[][] coeff);
	public void fromLine(GeoLine line);
	public double getP();
}
