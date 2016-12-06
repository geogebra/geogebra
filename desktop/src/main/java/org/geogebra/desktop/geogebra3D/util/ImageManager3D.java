package org.geogebra.desktop.geogebra3D.util;

import java.awt.Component;
import java.awt.Image;

import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * Class extending ImageManager for 3D.
 * 
 * @author mathieu
 *
 */
public class ImageManager3D extends ImageManagerD {

	private AppD app;

	/**
	 * default constructor
	 * 
	 * @param comp
	 */
	public ImageManager3D(Component comp, AppD app) {
		super(comp);
		this.app = app;
	}

	@Override
	public Image getImageResourceGeoGebra(ImageResourceD name) {

		Image img = getImageResource(
				"/org/geogebra/desktop/geogebra3D" + name.getFilename());

		if (img == null) {
			return super.getImageResourceGeoGebra(name);
		}

		// Application.debug("Get from 3D image " + name);

		return img;
	}

	@Override
	public int getMaxIconSize() {
		if (app.useHugeGuiForInput3D()) {
			return 64;
		}
		return super.getMaxIconSize();
	}

}
