package org.geogebra.common.properties;

import org.geogebra.common.awt.GColor;

public interface ColorProperty extends Property {

	GColor getColor();

	void setColor(GColor color);

	GColor[] getColors();
}
