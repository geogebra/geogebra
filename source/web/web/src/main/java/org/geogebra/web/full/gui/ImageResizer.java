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

package org.geogebra.web.full.gui;

import java.util.function.Consumer;

import org.geogebra.web.html5.gui.util.Dom;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

/**
 * Utility class for resizing Images
 *
 */
public class ImageResizer {

	/**
	 * Resizes an Image not keeping the aspect ratio
	 * 
	 * @param imgDataURL
	 *            the data URL of the image
	 * @param width
	 *            width of the resized image
	 * @param height
	 *            height of the resized image
	 * @param callback accepts the data URL of the resized image or the original data URL in
	 *         case of no resize happened
	 */
	public static void resizeImage(String imgDataURL, int width, int height,
			Consumer<String> callback) {
		HTMLImageElement image = Dom.createImage();
		image.addEventListener("load", event -> {
			int sWidth = image.width;
			int sHeight = image.height;

			if (!(sWidth == width && sHeight == height)) {
				HTMLCanvasElement canvasTmp =
						(HTMLCanvasElement) DomGlobal.document.createElement("canvas");
				CanvasRenderingContext2D context = Js.uncheckedCast(
						canvasTmp.getContext("2d"));
				canvasTmp.width = width;
				canvasTmp.height = height;

				context.drawImage(image, 0, 0, sWidth, sHeight, 0, 0, width, height);
				callback.accept(canvasTmp.toDataURL());
			} else {
				callback.accept(imgDataURL);
			}
		});
		image.src = imgDataURL;
	}

}
