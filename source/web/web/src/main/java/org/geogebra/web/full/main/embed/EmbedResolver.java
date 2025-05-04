package org.geogebra.web.full.main.embed;

import elemental2.promise.Promise;
import jsinterop.annotations.JsFunction;

/** Embed resolver for external embed type */
@JsFunction
public interface EmbedResolver {
	/**
	 * @param id content ID (unique within the embed type)
	 * @return promise resolving to a string with embed content
	 */
	Promise<String> resolve(String id);
}
