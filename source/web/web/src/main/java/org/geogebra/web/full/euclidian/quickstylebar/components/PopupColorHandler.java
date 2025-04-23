package org.geogebra.web.full.euclidian.quickstylebar.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.ValuedProperty;

/**
 * Change handler for color popup.
 */
public interface PopupColorHandler {

	void fireActionPerformed(ValuedProperty<GColor> prop, GColor value);

}
