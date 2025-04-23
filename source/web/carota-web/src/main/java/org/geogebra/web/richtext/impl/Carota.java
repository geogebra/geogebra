package org.geogebra.web.richtext.impl;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class Carota {

	protected Carota() {
		// use Carota.get() instead, may return null
	}

	@JsProperty(name = "murok")
	public static native Carota get();

	@JsProperty
	public native CarotaEditorFactory getEditor();

	@JsProperty
	public native CarotaTableFactory getTable();

	@JsProperty
	public native CarotaText getText();

	@JsProperty
	public native CarotaRuns getRuns();

	/**
	 * Filter/processor for inserted strings.
	 */
	@JsFunction
	public interface InsertFilter {
		/**
		 * @param text inserted text
		 * @return processed text
		 */
		String onInserted(String text);
	}

}
