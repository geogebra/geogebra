package org.geogebra.web.html5.main;

import com.google.gwt.dom.client.Element;

public class FileDropHandlerW {
	/**
	 * Register file drop handlers for the canvas of this application
	 * 
	 * @param ce
	 *            Element that listens to the drop events
	 */
	protected static native void registerDropHandler(Element ce, AppW app) /*-{

		var appl = this;
		var frameElement = ce;

		if (frameElement) {
			frameElement.addEventListener("dragover", function(e) {
				e.preventDefault();
				e.stopPropagation();
				frameElement.style.borderColor = "#ff0000";
			}, false);
			frameElement.addEventListener("dragenter", function(e) {
				e.preventDefault();
				e.stopPropagation();
			}, false);
			frameElement
					.addEventListener(
							"drop",
							function(e) {
								e.preventDefault();
								e.stopPropagation();
								frameElement.style.borderColor = "#000000";
								var dt = e.dataTransfer;
								if (dt.files.length) {
									var fileToHandle = dt.files[0];

									//at first this tries to open the fileToHandle as image,
									//if fileToHandle not an image, this will try to open as ggb or ggt.
									if (!appl.@org.geogebra.web.html5.main.AppW::openFileAsImage(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle, null)) {
										appl.@org.geogebra.web.html5.main.AppW::openFile(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle, null);
									}

								} else {
									// This would raise security exceptions later - see ticket #2301
									//var gdat = dt.getData("URL");
									//if (gdat && gdat != " ") {
									//	var coordx = e.offsetX ? e.offsetX : e.layerX;
									//	var coordy = e.offsetY ? e.offsetY : e.layerY;
									//	appl.@org.geogebra.web.html5.main.AppW::urlDropHappened(Ljava/lang/String;II)(gdat, coordx, coordy);
									//}
								}
							}, false);
		}
		$doc.body.addEventListener("dragover", function(e) {
			e.preventDefault();
			e.stopPropagation();
			if (frameElement)
				frameElement.style.borderColor = "#000000";
		}, false);
		$doc.body.addEventListener("drop", function(e) {
			e.preventDefault();
			e.stopPropagation();
		}, false);
	}-*/;
}
