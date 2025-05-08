package org.geogebra.web.html5.gui;

import org.geogebra.common.annotation.MissingDoc;

/**
 * UI element that can be hidden. Semantic of hide() and setVisible(false) is different,
 * see GPopup.
 */
public interface HasHide {
	@MissingDoc
	void hide();
}
