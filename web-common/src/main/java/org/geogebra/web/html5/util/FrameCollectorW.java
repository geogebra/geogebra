package org.geogebra.web.html5.util;

import org.geogebra.common.main.App.ExportType;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

public interface FrameCollectorW {

	String finish(int width, int height);

	void addFrame(EuclidianViewWInterface ev, double scale,
			ExportType format);

}
