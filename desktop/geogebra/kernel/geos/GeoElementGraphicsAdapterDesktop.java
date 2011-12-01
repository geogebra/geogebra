package geogebra.kernel.geos;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.main.AbstractApplication;

import geogebra.main.Application;
import geogebra.util.ImageManager;
import geogebra.util.BufferedImageAdapterDesktop;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class GeoElementGraphicsAdapterDesktop implements GeoElementGraphicsAdapter {

	protected String imageFileName = "";
	protected BufferedImageAdapter image;
	protected Application app;

	public GeoElementGraphicsAdapterDesktop(Application appl) {
		app = appl;
	}

	public BufferedImageAdapter getImageOnly() {
		return image;
	}

	public void setImageOnly(BufferedImageAdapter ba) {
		try {
			image = (BufferedImageAdapterDesktop)ba;
		} catch (Exception e) {
		}
	}

	public void setImageFileNameOnly(String fn) {
		imageFileName = fn;
	}

	public BufferedImageAdapter getFillImage() {
		if (image != null) return image;

		if (imageFileName.startsWith("/geogebra")) {
			Image im = ((Application)app).getImageManager().getImageResource(imageFileName);
			image = new BufferedImageAdapterDesktop(ImageManager.toBufferedImage(im));
		} else {
			BufferedImage extimg = ((Application)app).getExternalImage(imageFileName);
			if (extimg == null)
				image = null;
			else
				image = new BufferedImageAdapterDesktop(extimg);
		}

		return image;
	}

	public void setFillImage(String filename) {
		imageFileName=filename;
		image = null;
	}

	public void setImageFileName(String fileName) {
		if (fileName.equals(this.imageFileName))
			return;

		this.imageFileName = fileName;

		if (fileName.startsWith("/geogebra")) { // internal image
			Image im = ((ImageManager) ((AbstractApplication)app).getImageManager()).getImageResource(imageFileName);
			image = new BufferedImageAdapterDesktop(ImageManager.toBufferedImage(im));

		} else {
			image = new BufferedImageAdapterDesktop((BufferedImage)((AbstractApplication)app).getExternalImage(fileName));
		}
	}

	public String getImageFileName() {
		return imageFileName;
	}

}
