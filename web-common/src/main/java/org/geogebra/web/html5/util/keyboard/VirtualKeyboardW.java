package org.geogebra.web.html5.util.keyboard;

import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.RequiresResize;

public interface VirtualKeyboardW extends IsWidget, RequiresResize {

	void show();

	void setVisible(boolean visible);

	void resetKeyboardState();

	boolean shouldBeShown();

	int getOffsetHeight();

	void showOnFocus();

	void afterShown(Runnable runnable);

	void prepareShow(boolean animated);

	void showMoreButton();

	void hideMoreButton();

}
