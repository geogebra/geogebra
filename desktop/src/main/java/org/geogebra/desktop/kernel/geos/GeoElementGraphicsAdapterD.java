package org.geogebra.desktop.kernel.geos;

import java.awt.Image;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.ImageResourceD;

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

		if (imageFileName.startsWith("/geogebra")) {
			Image im = ((AppD) app).getImageManager().getImageResource(
					findFillImage());
			if (im == null) {
				Log.error(imageFileName + " does not exist");
				return null;
			}
			image = new MyImageD(ImageManagerD.toBufferedImage(im));
		} else {
			/*
			 * java.awt.image.BufferedImage extimg = app
			 * .getExternalImage(imageFileName); if (extimg == null) image =
			 * null; else image = new BufferedImage(extimg);
			 */

			image = app.getExternalImageAdapter(imageFileName, 0, 0);

		}

		return image;
	}

	private ImageResourceD findFillImage() {
		ImageResourceD res = null;
		if (imageFileName.startsWith("/geogebra")) {
			String fn = imageFileName.replace("/geogebra/", "")
					.replace("gui/images/", "");

			if ("go-down.png".equals(fn)) {
				res = GuiResourcesD.GO_DOWN;
			} else if ("go-up.png".equals(fn)) {
				res = GuiResourcesD.GO_UP;
			} else if ("go-previous.png".equals(fn)) {
				res = GuiResourcesD.GO_PREVIOUS;
			} else if ("go-next.png".equals(fn)) {
				res = GuiResourcesD.GO_NEXT;
			} else if ("nav_rewind.png".equals(fn)) {
				res = GuiResourcesD.NAV_REWIND;
			} else if ("nav_fastforward.png".equals(fn)) {
				res = GuiResourcesD.NAV_FASTFORWARD;
			} else if ("nav_skipback.png".equals(fn)) {
				res = GuiResourcesD.NAV_SKIPBACK;
			} else if ("nav_skipforward.png".equals(fn)) {
				res = GuiResourcesD.NAV_SKIPFORWARD;
			} else if ("exit.png".equals(fn)) {
				res = GuiResourcesD.EXIT;
			} else if ("main/nav_play.png".equals(fn)) {
				res = GuiResourcesD.NAV_PLAY;
			} else if ("main/nav_pause.png".equals(fn)) {
				res = GuiResourcesD.NAV_PAUSE;
			}

		}
		return res;
	}

	@Override
	public void setImageFileName(String fileName) {
		if (fileName.equals(this.imageFileName)) {
			return;
		}

		setImageFileNameOnly(fileName);

		if (fileName.startsWith("/geogebra")) { // internal image
			Image im = ((ImageManagerD) app.getImageManager())
					.getImageResource(findFillImage());
			image = new MyImageD(ImageManagerD.toBufferedImage(im));

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
