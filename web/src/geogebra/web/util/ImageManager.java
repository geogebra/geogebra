package geogebra.web.util;

import java.util.HashMap;

import com.google.gwt.dom.client.ImageElement;

import geogebra.common.awt.BufferedImage;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.AbstractImageManager;
import geogebra.web.gui.app.GeoGebraFrame;

public class ImageManager extends AbstractImageManager {
	
	private static HashMap<String, ImageElement> externalImageTable = new HashMap<String, ImageElement>();

	@Override
	public String createImage(String filename, AbstractApplication app) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addExternalImage(String fileName, ImageElement imageElement) {
	   if (fileName != null && imageElement != null) {
		   externalImageTable.put(fileName, imageElement);
	   }
    }
	
	public static BufferedImage getExternalImage(String fileName) {
		return (BufferedImage) externalImageTable.get(fileName);
	}

}
