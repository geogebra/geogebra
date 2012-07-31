package geogebra.kernel.geos;

import geogebra.awt.GBufferedImageD;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.main.App;
import geogebra.main.AppD;
import geogebra.util.ImageManager;

import java.awt.Image;

public class GeoElementGraphicsAdapterDesktop extends
		GeoElementGraphicsAdapter {

	protected AppD app;

	public GeoElementGraphicsAdapterDesktop(App appl) {
		app = (AppD) appl;
	}

	public geogebra.common.awt.GBufferedImage getFillImage() {
		if (image != null)
			return image;

		if (imageFileName.startsWith("/geogebra")) {
			Image im = app.getImageManager().getImageResource(imageFileName);
			image = new GBufferedImageD(ImageManager.toBufferedImage(im));
		} else {
/*
			java.awt.image.BufferedImage extimg = app
					.getExternalImage(imageFileName);
			if (extimg == null)
				image = null;
			else
				image = new BufferedImage(extimg);
				*/
			
			image = app.getExternalImageAdapter(imageFileName);

		}

		return image;
	}

	public void setImageFileName(String fileName) {
		if (fileName.equals(this.imageFileName))
			return;

		this.imageFileName = fileName;

		if (fileName.startsWith("/geogebra")) { // internal image
			Image im = ((ImageManager) ((App) app)
					.getImageManager()).getImageResource(imageFileName);
			image = new GBufferedImageD(ImageManager.toBufferedImage(im));

		} else {
			image = ((App) app)
					.getExternalImageAdapter(fileName);
		}
	}

}
