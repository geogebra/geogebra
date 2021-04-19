package org.geogebra.desktop.kernel.geos;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;

public class GeoElementGraphicsAdapterD extends GeoElementGraphicsAdapter {
	/** application */
	protected App app;

	/**
	 * @param appl
	 *            application
	 */
	public GeoElementGraphicsAdapterD(App appl) {
		app = appl;
	}

	@Override
	public MyImage getFillImage() {
		if (image != null) {
			return image;
		}

		if ("".equals(imageFileName)) {
			return null;
		}

		if (imageFileName.startsWith("/org/geogebra")) {
			image = new MyImageD();
			((MyImageD) image).loadAsSvg(imageFileName);
		} else {
			image = app.getExternalImageAdapter(imageFileName, 0, 0);
		}

		return image;
	}

	@Override
	public void setImageFileName(String fileName) {
		if (fileName.equals(this.imageFileName)) {
			return;
		}

		setImageFileNameOnly(fileName);

		if (fileName.startsWith("/org/geogebra")) { // internal image
			image = new MyImageD();
			((MyImageD) image).loadAsSvg(imageFileName);
		} else {
			image = app.getExternalImageAdapter(fileName, 0, 0);
		}
	}

	@Override
	public void convertToSaveableFormat() {
		// all openable formats are saveable in Desktop
	}

	@Override
	public String toLaTeXStringBase64() {
		if (image.isSVG()) {
			Log.debug("SVG not supported in LaTeX");
			return "";
		}

		return image.toLaTeXStringBase64();
	}

}
