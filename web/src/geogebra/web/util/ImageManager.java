package geogebra.web.util;

import java.util.HashMap;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

import geogebra.common.awt.BufferedImage;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.AbstractImageManager;
import geogebra.web.gui.app.GeoGebraFrame;

public class ImageManager extends AbstractImageManager {
	
	private static HashMap<String, ImageElement> externalImageTable = new HashMap<String, ImageElement>();
	private static HashMap<String, String> externalImageSrcs = new HashMap<String, String>();
	private String constructionXml;

	@Override
	public String createImage(String filename, AbstractApplication app) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addExternalImage(String fileName, String src) {
	   if (fileName != null && src != null) {
		   ImageElement img = Document.get().createImageElement();
		   externalImageSrcs.put(fileName, src);
		   externalImageTable.put(fileName, img);
	   }
    }
	
	public static BufferedImage getExternalImage(String fileName) {
		return (BufferedImage) externalImageTable.get(fileName);
	}

	public ImageElement getImageResource(String imageFileName) {
	   return externalImageTable.get(imageFileName);
    }

	public static BufferedImage toBufferedImage(ImageElement im) {
	    return new geogebra.web.awt.BufferedImage(im);
    }
	
	
	/**
	 * @return has images
	 * because of async call of geogebra.xml if images exists, but not loaded yet.
	 */
	public boolean hasImages() {
		return !externalImageTable.isEmpty();
	}

	public void setConstructionXml(String xml) {
	    this.constructionXml = xml;
    }

}
