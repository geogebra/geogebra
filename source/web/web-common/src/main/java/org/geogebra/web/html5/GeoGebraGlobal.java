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

package org.geogebra.web.html5;

import org.geogebra.common.util.InjectJsInterop;
import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.gwtutil.JsRunnable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import elemental2.core.Function;
import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * GeoGebra-specific global variables (related to deployggb)
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GeoGebraGlobal {

	public static GiacNative __ggb__giac;
	public static @InjectJsInterop Function evalGeoGebraCASExternal;
	public static JsPropertyMap<JsPropertyMap<JsPropertyMap<String>>> __GGB__keysVar;
	public static JsConsumer<Void> runCallbacks;
	public static @InjectJsInterop JsArray<JsRunnable> ggbCallbacks;

	@JsProperty(name = "ggbExportFile")
	public static native Function getGgbExportFile();

	@JsProperty(name = "changeMetaTitle")
	public static native Function getChangeMetaTitle();

	@JsProperty
	public static native Function getGgbHeaderResize();

	@JsProperty
	public static native JsConsumer<Object> getLoadWorksheet();

	@JsProperty
	public static native Function getSetUnsavedMessage();

	@JsProperty
	public static native JsConsumer<Boolean> getGgbExamMode();

	@JsProperty
	public static native JsConsumer<String> getGgbAppletOnLoad();

	@JsProperty
	public native static Function getGgbMultiplayerChange();

}
