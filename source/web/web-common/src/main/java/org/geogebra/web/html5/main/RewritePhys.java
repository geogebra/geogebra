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
