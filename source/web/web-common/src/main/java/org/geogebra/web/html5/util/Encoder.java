package org.geogebra.web.html5.util;

public interface Encoder {

	void addFrame(String url);

	String finish(int width, int height);

}
