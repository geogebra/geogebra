package org.geogebra.web.web.gui.images;

import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.util.HasSetIcon;
import org.geogebra.web.web.gui.util.ImageOrText;

import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Helper for manipulationg with image resources
 */
public class ImgResourceHelper {
	
	/**
	 * Use icon on given button
	 * 
	 * @param ir
	 *            resource
	 * @param button
	 *            button
	 */
	public static void setIcon(final ResourcePrototype ir,
			final HasSetIcon button) {
	    ImageOrText img = new ImageOrText();
	    img.setUrl(GGWToolBar.safeURI(ir));
	    button.setIcon(img);
	   
	}


}
