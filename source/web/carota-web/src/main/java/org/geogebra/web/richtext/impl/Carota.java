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
