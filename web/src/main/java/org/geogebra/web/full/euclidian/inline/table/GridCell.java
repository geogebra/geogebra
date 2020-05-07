package org.geogebra.web.full.euclidian.inline.table;

import org.geogebra.common.util.InjectJsInterop;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
class GridCell {

	@InjectJsInterop
	public CellEvent cellEvent;
}
