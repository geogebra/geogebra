package geogebra3D.util;

import java.awt.Component;
import java.awt.Image;

import geogebra.main.Application;
import geogebra.util.ImageManager;

/**
 * Class extending ImageManager for 3D.
 * @author mathieu
 *
 */
public class ImageManager3D extends ImageManager {

	/** default constructor
	 * @param comp
	 */
	public ImageManager3D(Component comp) {
		super(comp);
	}
	
	
	
	public Image getImageResourceGeoGebra(String name) {
		
		Image img = getImageResource("/geogebra3D"+name);

		if (img == null) {
			return super.getImageResourceGeoGebra(name);			
		}

		//Application.debug("Get from 3D image " + name);	

		return img;
	}

}
