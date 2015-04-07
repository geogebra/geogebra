package org.geogebra.common.gui.dialog;

import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public interface TextInputDialog {

	void reInitEditor(GeoText text, GeoPointND startPoint);

	void setVisible(boolean b);
}
