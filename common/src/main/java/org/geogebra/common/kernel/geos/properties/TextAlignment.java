package org.geogebra.common.kernel.geos.properties;

import java.util.Locale;

public enum TextAlignment {
    LEFT, RIGHT, CENTER;

    @Override
    public String toString() {
		return name().toLowerCase(Locale.US);
	}
}
