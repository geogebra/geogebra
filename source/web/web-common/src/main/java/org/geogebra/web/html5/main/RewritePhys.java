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

package org.geogebra.web.html5.main;

import elemental2.core.Uint8Array;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class RewritePhys {

	private RewritePhys() {
		// utility class, no instantiation
	}

	/**
	 * @param bytes PNG as bytes
	 * @param ppmx pixels per meter -- horizontal
	 * @param ppmy pixels per meter -- vertical
	 * @return encoded PNG
	 */
	@JsMethod(name = "rewrite_pHYs_chunk")
	public static native String rewritePhysChunk(Uint8Array bytes, double ppmx, double ppmy);
}
