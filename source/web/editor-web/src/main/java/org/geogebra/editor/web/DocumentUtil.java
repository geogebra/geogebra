package org.geogebra.editor.web;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class DocumentUtil {

	/**
	 * Copy currently selected text
	 */
	public static void copySelection() {
		Function exec =
				(Function) Js.asPropertyMap(DomGlobal.document)
						.get("execCommand");
		exec.call(DomGlobal.document, "copy");
	}
}
