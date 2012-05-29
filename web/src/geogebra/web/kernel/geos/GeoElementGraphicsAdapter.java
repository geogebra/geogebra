package geogebra.web.kernel.geos;


import com.google.gwt.dom.client.ImageElement;

import geogebra.common.awt.BufferedImage;
import geogebra.common.main.AbstractApplication;
import geogebra.web.util.ImageManager;

public class GeoElementGraphicsAdapter extends
        geogebra.common.kernel.geos.GeoElementGraphicsAdapter {

	private AbstractApplication app;

	public GeoElementGraphicsAdapter(AbstractApplication appl) {
		app = (AbstractApplication) appl;
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

		// for file names e.g. /geogebra/main/nav_play.png
		if (fileName.charAt(0) == '/')
			fileName = fileName.substring(1);

		if (fileName.equals(this.imageFileName))
			return;

		this.imageFileName = fileName;

		// such file names are saved in the ggb file too, so this if is not needed (and does not work)
		//if (fileName.startsWith("/geogebra")) { // internal image
		//	ImageElement im = ((ImageManager) app
		//			.getImageManager()).getImageResource(imageFileName);
		//	image = new geogebra.web.awt.BufferedImage(ImageManager.toBufferedImage(im));

		//} else {
			image = app.getExternalImageAdapter(fileName);
		//}
    }

}
