package geogebra.web.helper;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Image;

/**
 * @author gabor
 *
 *	Factory class for create safeHtml strings
 */
public class SafeHtmlFactory {
	
	/**
	 * @param imgres ImageResource for safehtml
	 * @return The safehtml string of the image
	 */
	public static SafeHtml getImageHtml(ImageResource imgres) {
		final Image img = new Image(imgres);
		return new SafeHtml() {
			
			public String asString() {
				return img.getElement().getInnerHTML();
			}
		};
	}

}
