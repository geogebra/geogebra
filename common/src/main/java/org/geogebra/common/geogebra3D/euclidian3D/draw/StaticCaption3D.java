package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;

public class StaticCaption3D extends StaticText3D {

	public StaticCaption3D(GeoElement geo) {
		super(geo);
	}

	@Override
	public String text() {
		return getGeoElement().getLabelDescription();
	}

	@Override
	public boolean isValid() {
		return !getGeoElement().hasDynamicCaption();
	}

	@Override
	public boolean isSerifFont() {
		return false;
	}

	@Override
	public void createFont(GFont original) {
		super.updateFont(original.deriveFont(original.getStyle(), original.getSize()));
	}
}
