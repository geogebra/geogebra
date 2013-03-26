package geogebra.web.util;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.App;
import geogebra.common.util.AbstractImageManager;
import geogebra.web.helper.ImageLoadCallback;
import geogebra.web.helper.ImageWrapper;
import geogebra.web.io.MyXMLioW;
import geogebra.web.main.AppW;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;

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
	public String createImage(String filename, App app) {
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
	private MyXMLioW myXMLio;
	private AppW app = null;

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
				if (!app.isFullAppGui()) {
					app.afterLoadFile();
				} else {
					app.afterLoadAppFile();
				}
				imagesLoaded=0;
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

	public static GBufferedImage toBufferedImage(ImageElement im) {
	    return new geogebra.web.awt.GBufferedImageW(im);
    }

	class ImageLoadCallback2 implements ImageLoadCallback {
		public GeoImage gi;
		public ImageLoadCallback2(GeoImage gi2) {
			this.gi = gi2;
		}
		public void onLoad() {
			gi.updateRepaint();
		}
	}

	class ImageErrorCallback2 implements ImageLoadCallback {
		public GeoImage gi;
		public ImageErrorCallback2(GeoImage gi2) {
			this.gi = gi2;
		}
		public void onLoad() {
			// Image onerror and onabort actually
			gi.getCorner(0).remove();
			gi.getCorner(1).remove();
			gi.remove();
			app.getKernel().notifyRepaint();
		}
	}

	public void triggerSingleImageLoading(String imageFileName, GeoImage geoi) {
		ImageElement img = externalImageTable.get(imageFileName);
		ImageWrapper.nativeon(img, "load", new ImageLoadCallback2(geoi));
		ImageErrorCallback2 i2 = new ImageErrorCallback2(geoi);
		ImageWrapper.nativeon(img, "error", i2);
		ImageWrapper.nativeon(img, "abort", i2);
		img.setSrc(externalImageSrcs.get(imageFileName));
	}

	public void triggerImageLoading(String construction, MyXMLioW myXMLio, AppW app) {
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
