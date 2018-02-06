package org.geogebra.web.html5.util;

public interface FrameCollectorW {
	public void addFrame(String url);

	public void finish(int width, int height);

	public String getResult();
}
