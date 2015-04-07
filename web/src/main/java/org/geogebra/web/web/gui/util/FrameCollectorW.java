package org.geogebra.web.web.gui.util;

import org.geogebra.web.html5.gawt.GBufferedImageW;

public interface FrameCollectorW {
	public void addFrame(String url);

	public void finish();
}
