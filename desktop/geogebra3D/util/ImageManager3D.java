package geogebra3D.util;

import geogebra.util.ImageManagerD;

import java.awt.Component;
import java.awt.Image;

/**
 * Class extending ImageManager for 3D.
 * 
 * @author mathieu
 *
 */
public class ImageManager3D extends ImageManagerD {

	/**
	 * default constructor
	 * 
	 * @param comp
	 */
	public ImageManager3D(Component comp) {
		super(comp);
	}

	@Override
	public Image getImageResourceGeoGebra(String name) {

		Image img = getImageResource("/geogebra3D" + name);

		if (img == null) {
			return super.getImageResourceGeoGebra(name);
		}

		// Application.debug("Get from 3D image " + name);

		return img;
	}

}
