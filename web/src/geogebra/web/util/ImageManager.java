package geogebra.web.util;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.cell.client.ImageLoadingCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import geogebra.common.awt.BufferedImage;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.AbstractImageManager;
import geogebra.web.gui.app.GeoGebraFrame;
import geogebra.web.helper.ImageLoadCallback;
import geogebra.web.helper.ImageWrapper;
import geogebra.web.io.MyXMLio;
import geogebra.web.main.Application;

public class ImageManager extends AbstractImageManager {
	
	private static HashMap<String, ImageElement> externalImageTable = new HashMap<String, ImageElement>();
	private static HashMap<String, String> externalImageSrcs = new HashMap<String, String>();

	public void reset() {
		externalImageTable = new HashMap<String, ImageElement>();
		externalImageSrcs = new HashMap<String, String>();
		imagesLoaded = 0;
		construction = null;
		myXMLio = null;
		app = null;
	}

	@Override
	public String createImage(String filename, AbstractApplication app) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected int imagesLoaded = 0;
	
	ImageLoadCallback callBack = new ImageLoadCallback() {
		
		public void onLoad() {
			imagesLoaded++;
			checkIfAllLoaded();
		}
	};

	private String construction;
	private MyXMLio myXMLio;
	private Application app = null;

	public void addExternalImage(String fileName, String src) {
	   if (fileName != null && src != null) {
		   ImageElement img = Document.get().createImageElement();
		   externalImageSrcs.put(fileName, src);
		   externalImageTable.put(fileName, img);
	   }
    }
	
	protected void checkIfAllLoaded() {
		imagesLoaded++;
		if (imagesLoaded == externalImageSrcs.size()) {
			try {
				myXMLio.processXMLString(construction, true, false);
				app.afterLoadFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

	public static ImageElement getExternalImage(String fileName) {
		return externalImageTable.get(fileName);
	}

	public ImageElement getImageResource(String imageFileName) {
	   return externalImageTable.get(imageFileName);
    }

	public static BufferedImage toBufferedImage(ImageElement im) {
	    return new geogebra.web.awt.BufferedImage(im);
    }

	public void triggerImageLoading(String construction, MyXMLio myXMLio, Application app) {
		this.construction = construction;
		this.myXMLio = myXMLio;	
		this.app = app;
		if (externalImageSrcs.entrySet() != null) {
			for (Entry<String, String> imgSrc : externalImageSrcs.entrySet()) {
				ImageWrapper img = new ImageWrapper(externalImageTable.get(imgSrc.getKey())); 
				img.attachNativeLoadHandler(this);	
				img.getElement().setSrc(imgSrc.getValue());
			}
		}
	}
	
	
	/**
	 * @return has images
	 * because of async call of geogebra.xml if images exists, but not loaded yet.
	 */
	public boolean hasImages() {
		return !externalImageTable.isEmpty();
	}

	public ImageElement getInternalImage(ImageResource resource) {
	    ImageElement img = Document.get().createImageElement();
	    img.setSrc(resource.getSafeUri().asString());
	    return img;
    }
}
