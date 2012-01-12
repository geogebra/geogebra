package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;

import java.util.ArrayList;

public interface EuclidianStyleBar {

	void applyVisualStyle(ArrayList<GeoElement> selectedGeos);

	public void updateButtonPointCapture(int mode);

	void setMode(int mode);

	void setLabels();

	void restoreDefaultGeo();

	void updateStyleBar();

}
