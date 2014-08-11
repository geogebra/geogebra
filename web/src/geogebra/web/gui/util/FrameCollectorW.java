package geogebra.web.gui.util;

import geogebra.html5.gawt.BufferedImage;

public interface FrameCollectorW {
	public void addFrame(BufferedImage img);

	public void finish();
}
