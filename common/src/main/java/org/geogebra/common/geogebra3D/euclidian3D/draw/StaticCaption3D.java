package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;

public class StaticCaption3D extends StaticText3D {

	private boolean serif;

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
		return serif;
	}

	@Override
	public void createFont(GFont original) {
		int size = original.getSize();
		serif = true;
		if (getGeoElement() instanceof TextProperties) {
			serif = ((TextProperties) getGeoElement()).isSerifFont();
		}
		super.updateFont(original.deriveFont(original.getStyle(), size));
	}
}
