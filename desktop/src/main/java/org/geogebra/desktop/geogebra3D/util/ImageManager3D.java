package org.geogebra.desktop.geogebra3D.util;

import java.awt.Component;
import java.awt.Image;

import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.ImageResourceD;

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
	 * @param comp main app component
	 */
	public ImageManager3D(Component comp) {
		super(comp);
	}

	@Override
	public Image getImageResourceGeoGebra(ImageResourceD name) {

		Image img = getImageResource(
				"/org/geogebra/desktop/geogebra3D" + name.getFilename());

		if (img == null) {
			return super.getImageResourceGeoGebra(name);
		}
		return img;
	}

}
