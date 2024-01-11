package org.geogebra.common.jre.headless;

import org.geogebra.common.euclidian.EuclidianView;

public interface ApiDelegate {

	String base64encodePNG(boolean transparent,
			double dpi, double exportScale, EuclidianView ev);

	void openFile(String strURL);
}
