package geogebra.web.kernel.geos;


import com.google.gwt.dom.client.ImageElement;

import geogebra.common.awt.BufferedImage;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;
import geogebra.web.util.ImageManager;

public class GeoElementGraphicsAdapter extends
        geogebra.common.kernel.geos.GeoElementGraphicsAdapter {

	private Application app;

	public GeoElementGraphicsAdapter(AbstractApplication appl) {
		app = (Application) appl;
	}
	
	public BufferedImage getFillImage() {
		if (image != null) return image;
		
		if (imageFileName.startsWith("/geogebra")) {
			return null;
		} else {
			image = app.getExternalImageAdapter(imageFileName);	
		}
		
		return image;
    }

	public void setImageFileName(String fileName) {
		if (fileName.equals(this.imageFileName))
			return;

		this.imageFileName = fileName;

		if (fileName.startsWith("/geogebra")) { // internal image
			ImageElement im = ((ImageManager) app
					.getImageManager()).getImageResource(imageFileName);
			image = new geogebra.web.awt.BufferedImage(ImageManager.toBufferedImage(im));

		} else {
			image = app
					.getExternalImageAdapter(fileName);
		}
    }

}
