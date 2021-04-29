package org.geogebra.web.full.gui;

import org.geogebra.web.html5.util.Dom;

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
	 * @return the data URL of the resized image or the original data URL in
	 *         case of no resize happened
	 */
	public static String resizeImage(String imgDataURL, int width, int height) {
		HTMLImageElement image = Dom.createImage();
		image.src = imgDataURL;
		int sWidth = image.width;
		int sHeight = image.height;
		String dImgDataURL;

		if (!(sWidth == width && sHeight == height)) {
			HTMLCanvasElement canvasTmp =
					(HTMLCanvasElement) DomGlobal.document.createElement("canvas");
			CanvasRenderingContext2D context = Js.uncheckedCast(canvasTmp.getContext("2d"));
			canvasTmp.width = width;
			canvasTmp.height = height;

			context.drawImage(image, 0, 0, sWidth, sHeight, 0, 0, width, height);

			dImgDataURL = canvasTmp.toDataURL();
		} else {
			dImgDataURL = imgDataURL;
		}

		return dImgDataURL;
	}

}
