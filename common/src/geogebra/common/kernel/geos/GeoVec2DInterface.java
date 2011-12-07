package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.ExpressionValue;

public interface GeoVec2DInterface extends ExpressionValue{

	public double getX();
	public double getY();
	public boolean isImaginaryUnit();
	public void setMode(int mode);

}
