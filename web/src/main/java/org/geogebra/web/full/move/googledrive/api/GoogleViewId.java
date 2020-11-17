package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.common.util.InjectJsInterop;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jsinterop.annotations.JsType;

@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
@JsType(isNative = true, namespace = "google.picker", name = "ViewId")
public class GoogleViewId {

	@InjectJsInterop public static Object DOCS;

	@InjectJsInterop public static Object FOLDERS;
}
