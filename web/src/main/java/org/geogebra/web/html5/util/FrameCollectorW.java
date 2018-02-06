package org.geogebra.web.html5.util;

public interface FrameCollectorW {
	public void addFrame(String url);

	public void finish();

	public String getResult();
}
