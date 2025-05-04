package org.geogebra.web.html5.util.keyboard;

import org.gwtproject.user.client.ui.HasVisibility;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * Virtual keyboard.
 */
public interface VirtualKeyboardW extends IsWidget, RequiresResize, HasVisibility {

	/**
	 * Show the keyboard.
	 */
	void show();

	/**
	 * Reset keyboard state.
	 */
	void resetKeyboardState();

	/**
	 * @return TODO
	 */
	boolean shouldBeShown();

	/**
	 * @return height in pixels
	 */
	int getOffsetHeight();

	/**
	 * Show the keyboard once the listener is focused.
	 */
	void showOnFocus();

	/**
	 * Run callback when animation is done.
	 * @param runnable callback
	 */
	void afterShown(Runnable runnable);

	/**
	 * Show the keyboard.
	 * @param animated whether to use animation
	 */
	void prepareShow(boolean animated);

	/**
	 * Show "more" button.
	 */
	void showMoreButton();

	/**
	 * Hide "more" button.
	 */
	void hideMoreButton();

}
