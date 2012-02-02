package geogebra.common.kernel.geos;

import geogebra.common.awt.BufferedImage;

public abstract class GeoElementGraphicsAdapter {

	protected String imageFileName = "";
	protected BufferedImage image;

	public BufferedImage getImageOnly() {
		return image;
	}

	public void setImageOnly(BufferedImage ba) {
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
	
	
	
	

	public abstract BufferedImage getFillImage();
	public abstract void setImageFileName(String fileName);

	public void setFillImage(String filename) {
		imageFileName = filename;
		image = null;
	}
	
	
}
