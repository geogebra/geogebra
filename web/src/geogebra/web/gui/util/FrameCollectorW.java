package geogebra.web.gui.util;

import geogebra.html5.gawt.GBufferedImageW;

public interface FrameCollectorW {
	public void addFrame(String url);

	public void finish();
}
