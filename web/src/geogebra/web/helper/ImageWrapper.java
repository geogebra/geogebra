package geogebra.web.helper;

import geogebra.web.util.ImageManager;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class ImageWrapper extends Image {

	public ImageWrapper(ImageElement imageElement) {
	    super(imageElement);
    }
	
	/**
	 * @param imageManager
	 * 
	 * Images in gwt event system not loaded when src added, only when attached to dom.
	 * So we must hack it.
	 */
	public void attachNativeLoadHandler(ImageManager imageManager) {
		addNativeLoadHandler(getElement(),imageManager);
	}

	private native void addNativeLoadHandler(Element img, ImageManager imageManager) /*-{
		img.addEventListener("load",function() {
			imageManager.@geogebra.web.util.ImageManager::checkIfAllLoaded()();
		});
	}-*/;

}
