package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.common.euclidian.EuclidianView;

public interface EuclidianViewAccessibiliyAdapter {

	boolean focusSpeechRecBtn();

	void focusNextGUIElement();

	void focusLastZoomButton();

	EuclidianView getEuclidianView();

	boolean focusSettings();

}
