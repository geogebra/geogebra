package org.geogebra.web.keyboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface KeyboardResources extends ClientBundle {

	KeyboardResources INSTANCE = GWT.create(KeyboardResources.class);

	// ONSCREENKEYBOARD
	@Source("org/geogebra/web/web/gui/images/view_close.png")
	ImageResource keyboard_close();

	@Source("org/geogebra/web/keyboard/images/keyboard_shiftDown.png")
	ImageResource keyboard_shiftDown();

	@Source("org/geogebra/web/keyboard/images/keyboard_shift.png")
	ImageResource keyboard_shift();

	@Source("org/geogebra/web/keyboard/images/keyboard_backspace.png")
	ImageResource keyboard_backspace();

	@Source("org/geogebra/web/keyboard/images/keyboard_enter.png")
	ImageResource keyboard_enter();

	@Source("org/geogebra/web/keyboard/images/keyboard_arrowLeft.png")
	ImageResource keyboard_arrowLeft();

	@Source("org/geogebra/web/keyboard/images/keyboard_arrowRight.png")
	ImageResource keyboard_arrowRight();

	@Source("org/geogebra/web/keyboard/images/keyboard_open.png")
	ImageResource keyboard_show();

}
