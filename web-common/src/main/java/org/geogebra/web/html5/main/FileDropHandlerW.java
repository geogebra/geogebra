package org.geogebra.web.html5.main;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.geogebra.web.html5.util.StringConsumer;

import com.google.gwt.dom.client.Element;

import elemental2.dom.DataTransfer;
import elemental2.dom.DataTransferItem;
import elemental2.dom.DragEvent;
import elemental2.dom.File;
import jsinterop.base.Js;

/**
 * Handles files dropped into GeoGebra.
 */
public class FileDropHandlerW {
	/**
	 * Register file drop handlers for the canvas of this application
	 * 
	 * @param ce
	 *            Element that listens to the drop events
	 * @param appl
	 *            application
	 */
	protected static void registerDropHandler(Element ce, AppW appl,
			GlobalHandlerRegistry list) {
		if (ce == null) {
			return;
		}

		elemental2.dom.HTMLElement frameElement = Js.uncheckedCast(ce);

		list.addEventListener(frameElement, "dragover", (e) -> {
			e.preventDefault();
			e.stopPropagation();
			frameElement.style.borderColor = "#ff0000";
		});

		list.addEventListener(frameElement, "dragleave", (e) -> {
			e.preventDefault();
			e.stopPropagation();
			frameElement.style.borderColor = "#000000";
		});

		list.addEventListener(frameElement, "drop", (event) -> {
			DragEvent e = (DragEvent) event;
			e.preventDefault();
			e.stopPropagation();
			frameElement.style.borderColor = "#000000";
			DataTransfer dt = e.dataTransfer;
			if (dt.files.length > 0) {
				File fileToHandle = dt.files.getAt(0);

				//at first this tries to open the fileToHandle as image,
				//if not possible, try to open as ggb or ggt.
				if (!appl.openFileAsImage(fileToHandle)) {
					appl.openFile(fileToHandle);
				}
			} else if (dt.items.length > 0) {
				for (int i = 0; i < dt.items.length; i++) {
					insertText(dt.items.getAt(i), appl);
				}
			}
		});
	}

	private static void insertText(DataTransferItem dt, AppW appl) {
		final StringConsumer handler;
		if (dt.type.startsWith("image/")) {
			String ext = dt.type.split("[/+]")[1];
			handler = s -> appl.imageDropHappened("drop." + ext , s);
		} else if (dt.type.equals("text/plain")) {
			handler = s -> CopyPasteW.pastePlainText(appl, s);
		} else if (dt.type.equals("application/x-latex")
				|| dt.type.equals("application/mathml-presentation+xml")) {
			handler = s -> CopyPasteW.pasteFormula(appl, s);
		} else {
			handler = null;
		}
		if (handler == null) {
			return;
		}
		dt.getAsString(text -> {
			if (!StringUtil.empty(text) && !"undefined".equals(text)) {
				handler.consume(text);
			}
			return null;
		});
	}

}
