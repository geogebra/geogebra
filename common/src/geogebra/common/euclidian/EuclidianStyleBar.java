package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;

import java.util.ArrayList;

public interface EuclidianStyleBar {

	void applyVisualStyle(ArrayList<GeoElement> selectedGeos);

	public void updateButtonPointCapture(int mode);

}
