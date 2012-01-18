package geogebra.common.kernel.geos;

import geogebra.common.awt.BufferedImageAdapter;

public abstract class GeoElementGraphicsAdapter {

	protected String imageFileName = "";
	protected BufferedImageAdapter image;

	public BufferedImageAdapter getImageOnly() {
		return image;
	}

	public void setImageOnly(BufferedImageAdapter ba) {
		try {
			image = ba;
		} catch (Exception e) {
		}
	}

	public void setImageFileNameOnly(String fn) {
		imageFileName = fn;
	}

	public String getImageFileName() {
		return imageFileName;
	}
	
	
	
	

	public abstract BufferedImageAdapter getFillImage();
	public abstract void setImageFileName(String fileName);

	public void setFillImage(String filename) {
		imageFileName = filename;
		image = null;
	}
	
	
}
