/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.euclidian;

import java.util.HashSet;
import java.util.function.Consumer;

import elemental2.core.JsIIterableResult;
import elemental2.core.JsIteratorIterable;
import elemental2.core.JsString;
import elemental2.core.RegExpResult;
import elemental2.dom.DomGlobal;
import elemental2.dom.FileReader;
import elemental2.dom.Response;
import jsinterop.base.Js;

public class BlobResolver {
	private String serializedSvg;
	private final HashSet<String> pending = new HashSet<>();

	public BlobResolver(String serializedSvg) {
		this.serializedSvg = serializedSvg;
	}

	/**
	 * Replace all blob URLs in SVG with their data URL equivalents
	 * @param callback callback
	 */
	public void resolve(Consumer<String> callback) {
		JsString jss = new JsString(serializedSvg);

		JsIteratorIterable<RegExpResult, ?, ?> result = jss.matchAll("\"(blob:[^\"]*)\"");
		JsIIterableResult<RegExpResult> next;
		do {
			next = result.next();
			// unchecked cast because of iframe :(
			RegExpResult value = Js.uncheckedCast(next.getValue());
			if (Js.isTruthy(value)) {
				final String blobUrl = value.getAt(1);
				if (pending.add(blobUrl)) {
					replaceBlobUrl(blobUrl, callback);
				}
			}
		} while (!next.isDone());
		if (pending.isEmpty()) {
			callback.accept(serializedSvg);
		}
	}

	private void replaceBlobUrl(String blobUrl, Consumer<String> callback) {
		FileReader fr = new FileReader();
		fr.addEventListener("load", (evt) -> {
			pending.remove(blobUrl);
			serializedSvg = serializedSvg.replace(blobUrl, fr.result.asString());
			if (pending.isEmpty()) {
				callback.accept(serializedSvg);
			}
		});
		DomGlobal.fetch(blobUrl).then(Response::blob).then(blob -> {
			fr.readAsDataURL(blob);
			return null;
		});
	}

}
