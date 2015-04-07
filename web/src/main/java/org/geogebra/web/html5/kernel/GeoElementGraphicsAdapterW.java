package org.geogebra.web.html5.kernel;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.main.App;
import org.geogebra.common.util.MD5EncrypterGWTImpl;

public class GeoElementGraphicsAdapterW extends
        org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter {

	private App app;

	public GeoElementGraphicsAdapterW(App appl) {
		app = (App) appl;
	}

	public MyImage getFillImage() {
		if (image != null)
			return image;

		if ("".equals(imageFileName)) {
			return null;
		}

		if (imageFileName.startsWith("/geogebra")) {
			return null;
		}
		image = app.getExternalImageAdapter(imageFileName, 0, 0);

		return image;
	}

	public void setImageFileName(String fileName) {

		// for file names e.g. /geogebra/main/nav_play.png
		if (fileName != null && fileName.length() != 0
		        && fileName.charAt(0) == '/')
			fileName = fileName.substring(1);

		if (fileName.equals(this.imageFileName)) {
			return;
		}

		setImageFileNameOnly(fileName);

		// such file names are saved in the ggb file too, so this if is not
		// needed (and does not work)
		// if (fileName.startsWith("/geogebra")) { // internal image
		// ImageElement im = ((ImageManager) app
		// .getImageManager()).getImageResource(imageFileName);
		// image = new
		// geogebra.html5.awt.BufferedImage(ImageManager.toBufferedImage(im));

		// } else {
		image = app.getExternalImageAdapter(fileName, 0, 0);
		// }
	}

	@Override
	public void convertToSaveableFormat() {
		if ("".equals(imageFileName)) {
			return;
		}
		int dotIndex = imageFileName.lastIndexOf('.');
		String ext = imageFileName.substring(dotIndex + 1).toLowerCase();
		if ("png".equals(ext) || "svg".equals(ext)) {
			return;
		}
		int index = imageFileName.lastIndexOf('/');
		int extDotLength = dotIndex < 0 ? 0 : ext.length() + 1;
		String fn = imageFileName.substring(index + 1, imageFileName.length()
		        - extDotLength)
		        + ".png";
		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		imageFileName = md5e.encrypt(fn) + "/" + fn;
	}

}
