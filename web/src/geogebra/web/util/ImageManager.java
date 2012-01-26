package geogebra.web.util;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.cell.client.ImageLoadingCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
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

public class ImageManager extends AbstractImageManager {
	
	private static HashMap<String, ImageElement> externalImageTable = new HashMap<String, ImageElement>();
	private static HashMap<String, String> externalImageSrcs = new HashMap<String, String>();

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
	
	public void triggerImageLoading(String construction, MyXMLio myXMLio) {
		this.construction = construction;
		this.myXMLio = myXMLio;	
		if (externalImageSrcs.entrySet() != null) {
			for (Entry<String, String> imgSrc : externalImageSrcs.entrySet()) {
				ImageWrapper img = new ImageWrapper(externalImageTable.get(imgSrc.getKey())); 
				img.attachNativeLoadHandler(this);	
				img.setUrl(imgSrc.getValue());
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
}
