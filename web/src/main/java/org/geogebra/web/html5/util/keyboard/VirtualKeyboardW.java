package org.geogebra.web.html5.util.keyboard;

import com.google.gwt.user.client.ui.IsWidget;

public interface VirtualKeyboardW extends IsWidget {

	void show();

	void setVisible(boolean visible);

	void resetKeyboardState();

	boolean shouldBeShown();

	int getOffsetHeight();

	void showOnFocus();
}
