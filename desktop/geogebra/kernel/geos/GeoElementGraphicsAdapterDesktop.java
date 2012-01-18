package geogebra.kernel.geos;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.main.AbstractApplication;

import geogebra.main.Application;
import geogebra.util.ImageManager;
import geogebra.awt.BufferedImage;

import java.awt.Image;

public class GeoElementGraphicsAdapterDesktop extends
		GeoElementGraphicsAdapter {

	protected Application app;

	public GeoElementGraphicsAdapterDesktop(AbstractApplication appl) {
		app = (Application) appl;
	}

	public BufferedImageAdapter getFillImage() {
		if (image != null)
			return image;

		if (imageFileName.startsWith("/geogebra")) {
			Image im = app.getImageManager().getImageResource(imageFileName);
			image = new BufferedImage(ImageManager.toBufferedImage(im));
		} else {
			java.awt.image.BufferedImage extimg = app
					.getExternalImage(imageFileName);
			if (extimg == null)
				image = null;
			else
				image = new BufferedImage(extimg);
		}

		return image;
	}

	public void setImageFileName(String fileName) {
		if (fileName.equals(this.imageFileName))
			return;

		this.imageFileName = fileName;

		if (fileName.startsWith("/geogebra")) { // internal image
			Image im = ((ImageManager) ((AbstractApplication) app)
					.getImageManager()).getImageResource(imageFileName);
			image = new BufferedImage(ImageManager.toBufferedImage(im));

		} else {
			image = ((AbstractApplication) app)
					.getExternalImageAdapter(fileName);
		}
	}

}
