package org.geogebra.web.html5.util;

import elemental2.dom.Blob;
import jsinterop.annotations.JsFunction;

@JsFunction
public interface FileConsumer {
	void consume(Blob file);
}
