package geogebra.common.gui.dialog;

import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;

public interface TextInputDialog {

	void reInitEditor(GeoText text, GeoPointND startPoint);

	void setVisible(boolean b);

}
