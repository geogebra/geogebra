package geogebra.common.kernel.geos;

import geogebra.common.awt.BufferedImageAdapter;

public interface GeoElementGraphicsAdapter {
	
	public BufferedImageAdapter getImageOnly();
	public void setImageOnly(BufferedImageAdapter ba);
	public void setImageFileNameOnly(String fn);

	public BufferedImageAdapter getFillImage();
	public void setFillImage(String filename);
	public void setImageFileName(String fileName);
	public String getImageFileName();
}
