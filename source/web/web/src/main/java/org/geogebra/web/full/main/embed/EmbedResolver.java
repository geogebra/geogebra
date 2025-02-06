package org.geogebra.web.full.main.embed;

import elemental2.promise.Promise;
import jsinterop.annotations.JsFunction;

@JsFunction
public interface EmbedResolver {
	Promise<String> resolve(String id);
}
