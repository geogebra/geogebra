package org.geogebra.web.html5.kernel;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.main.App;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.util.ImageManagerW;

import com.google.gwt.resources.client.ImageResource;

/**
 * Connects geoelements to images in Web
 */
public class GeoElementGraphicsAdapterW extends
        org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter {

	private App app;

	/**
	 * @param appl
	 *            application
	 */
	public GeoElementGraphicsAdapterW(App appl) {
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
			String fn = imageFileName.replace("/geogebra/", "")
					.replace("gui/images/", "");
			ImageResource res = null;
			if ("go-down.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE
						.icons_fillings_arrow_big_down();
			} else if ("go-up.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE
						.icons_fillings_arrow_big_up();
			} else if ("go-previous.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE
						.icons_fillings_arrow_big_left();
			} else if ("go-next.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE
						.icons_fillings_arrow_big_right();
			} else if ("nav_rewind.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE.icons_fillings_rewind();
			} else if ("nav_fastforward.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE.icons_fillings_fastforward();
			} else if ("nav_skipback.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE.icons_fillings_skipback();
			} else if ("nav_skipforward.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE.icons_fillings_skipforward();
			} else if ("exit.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE.icons_fillings_cancel();
			} else if ("main/nav_play.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE.icons_fillings_play();
			} else if ("main/nav_pause.png".equals(fn)) {
				res = GuiResourcesSimple.INSTANCE.icons_fillings_pause();
			}

			return res == null ? null
					: new MyImageW(ImageManagerW
							.getInternalImage(res), false);
		}
		image = app.getExternalImageAdapter(imageFileName, 0, 0);

		return image;
	}

	@Override
	public void setImageFileName(String fileNameRaw) {
		String fileName = fileNameRaw;
		// for file names e.g. /geogebra/main/nav_play.png
		if (fileName != null && fileName.length() != 0
		        && fileName.charAt(0) == '/') {
			fileName = fileName.substring(1);
		}

		if (fileName == null || fileName.equals(this.imageFileName)) {
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
		String oldFn = imageFileName;
		FileExtensions ext = StringUtil.getFileExtension(imageFileName);

		if (ext.isAllowedImage()) {
			// allowed already, nothing to do
			return;
		}

		String fn = StringUtil.changeFileExtension(imageFileName,
				FileExtensions.PNG);

		imageFileName = app.md5Encrypt(fn) + "/" + fn;
		Log.debug("Converted:" + oldFn + "->" + imageFileName);
	}

	@Override
	public String toLaTeXStringBase64() {
		return image.toLaTeXStringBase64();
	}

}
