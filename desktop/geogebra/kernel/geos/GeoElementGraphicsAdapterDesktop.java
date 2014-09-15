package geogebra.kernel.geos;

import geogebra.common.awt.MyImage;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.main.App;
import geogebra.gui.MyImageD;
import geogebra.main.AppD;
import geogebra.util.ImageManager;

import java.awt.Image;

public class GeoElementGraphicsAdapterDesktop extends GeoElementGraphicsAdapter {

	protected AppD app;

	public GeoElementGraphicsAdapterDesktop(App appl) {
		app = (AppD) appl;
	}

	public MyImage getFillImage() {
		if (image != null)
			return image;

		if ("".equals(imageFileName)) {
			return null;
		}

		if (imageFileName.startsWith("/geogebra")) {
			Image im = app.getImageManager().getImageResource(imageFileName);
			image = new MyImageD(ImageManager.toBufferedImage(im));
		} else {
			/*
			 * java.awt.image.BufferedImage extimg = app
			 * .getExternalImage(imageFileName); if (extimg == null) image =
			 * null; else image = new BufferedImage(extimg);
			 */

			image = app.getExternalImageAdapter(imageFileName);

		}

		return image;
	}

	public void setImageFileName(String fileName) {

		if (fileName.equals(this.imageFileName)) {
			return;
		}

		setImageFileNameOnly(fileName);

		if (fileName.startsWith("/geogebra")) { // internal image
			Image im = ((ImageManager) ((App) app).getImageManager())
					.getImageResource(imageFileName);
			image = new MyImageD(ImageManager.toBufferedImage(im));

		} else {
			image = ((App) app).getExternalImageAdapter(fileName);
		}
	}

	@Override
	public void convertToSaveableFormat() {
		// all openable formats are saveable in Desktop
	}

}
