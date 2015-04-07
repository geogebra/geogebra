package org.geogebra.web.web.gui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

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
		Image image = new Image(imgDataURL);
		int sWidth = image.getWidth();
		int sHeight = image.getHeight();
		String dImgDataURL;

		if (!(sWidth == width && sHeight == height)) {
			Canvas canvasTmp = Canvas.createIfSupported();
			Context2d context = canvasTmp.getContext2d();
			canvasTmp.setCoordinateSpaceWidth(width);
			canvasTmp.setCoordinateSpaceHeight(height);

			ImageElement im = ImageElement.as(image.getElement());
			context.drawImage(im, 0, 0, sWidth, sHeight, 0, 0, width, height);

			dImgDataURL = canvasTmp.toDataUrl();
		} else {
			dImgDataURL = imgDataURL;
		}

		return dImgDataURL;
	}

}
