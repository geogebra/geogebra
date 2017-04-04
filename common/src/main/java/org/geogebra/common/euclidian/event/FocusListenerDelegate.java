package org.geogebra.common.euclidian.event;

public interface FocusListenerDelegate {

	void focusLost(GFocusEvent event);

	void focusGained(GFocusEvent event);

}
