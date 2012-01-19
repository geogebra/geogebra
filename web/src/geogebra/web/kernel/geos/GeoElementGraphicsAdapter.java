package geogebra.web.kernel.geos;

import geogebra.common.awt.BufferedImage;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

public class GeoElementGraphicsAdapter extends
        geogebra.common.kernel.geos.GeoElementGraphicsAdapter {

	private Application app;

	public GeoElementGraphicsAdapter(AbstractApplication appl) {
		app = (Application) appl;
	}
	
	public BufferedImage getFillImage() {
		if (image != null) return image;
		
		if (imageFileName.startsWith("/geogebra")) {
			return null;
		} else {
			image = app.getExternalImageAdapter(imageFileName);	
		}
		
		return image;
    }

	public void setImageFileName(String fileName) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

}
