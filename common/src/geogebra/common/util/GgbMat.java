package geogebra.common.util;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.geos.GeoList;

public interface GgbMat {
	public void inverseImmediate();
	
	public void getMyList(MyList outputListEV,AbstractKernel kernel);
	public void getGeoList(GeoList outputListEV,Construction cons);

	public boolean isUndefined();

	public boolean isSquare();

	public double getDeterminant();

	public void transposeImmediate();

	public void reducedRowEchelonFormImmediate();
}
