package org.geogebra.web.html5.main;

import org.geogebra.web.html5.util.GlobalHandlerRegistry;

import com.google.gwt.dom.client.Element;

import elemental2.dom.DataTransfer;
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
			}
		});
	}
}
