package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Handles fill image of GeoElement
 * @author Arpad
 */
public abstract class GeoElementGraphicsAdapter {
	/** image filename */
	protected String imageFileName = "";
	/** fill image */
	protected MyImage image;

	/**
	 * @return fill image
	 */
	public MyImage getImageOnly() {
		return image;
	}

	/**
	 * @param ba new fill image
	 */
	public void setImageOnly(MyImage ba) {
		image = ba;
	}

	/**
	 * @param fn new filename
	 */
	public void setImageFileNameOnly(String fn) {

		FileExtensions ext = StringUtil.getFileExtensionEnum(fn);

		if (!ext.isAllowedImage()) {

			// all bitmaps (except JPG) saved as PNG
			// eg .TIFF
			imageFileName = StringUtil.changeFileExtension(fn,
					FileExtensions.PNG);
			Log.debug(
					"changing image extension " + ext + " -> " + imageFileName);
		} else {

			imageFileName = fn;
		}
	}

	/**
	 * @return filename of fill image
	 */
	public String getImageFileName() {
		return imageFileName;
	}
	
	/**
	 * @return fill image
	 */
	public abstract MyImage getFillImage();
	
	/**
	 * @param fileName filename
	 */
	public abstract void setImageFileName(String fileName);

	/**
	 * @param filename filename
	 */
	public void setFillImage(String filename) {
		setImageFileNameOnly(filename);
		image = null;
	}

	/**
	 * Make sure the image name and MD5 is adjusted to match the saveable formats 
	 * (no change in desktop, JPEG, GIF -> PNG in Web)
	 */
	public abstract void convertToSaveableFormat();
	
	
}
