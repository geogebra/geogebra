package org.geogebra.web.html5.util;

import elemental2.dom.Blob;
import jsinterop.annotations.JsFunction;

/**
 * Consumer of blobs.
 */
@JsFunction
public interface FileConsumer {
	/**
	 * @param file file as blob
	 */
	void consume(Blob file);
}
