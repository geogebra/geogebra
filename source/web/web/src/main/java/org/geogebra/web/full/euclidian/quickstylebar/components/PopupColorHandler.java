package org.geogebra.web.full.euclidian.quickstylebar.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.ValuedProperty;

public interface PopupColorHandler {

	void fireActionPerformed(ValuedProperty<GColor> prop, GColor value);

}
