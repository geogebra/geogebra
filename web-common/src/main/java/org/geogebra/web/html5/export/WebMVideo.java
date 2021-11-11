package org.geogebra.web.html5.export;

import elemental2.dom.Blob;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "WebMGL", name = "Video")
public class WebMVideo {

	@SuppressWarnings("unused")
	public WebMVideo(double framerate) {
		// native
	}

	public native void add(String image);

	public native Blob compile();
}
