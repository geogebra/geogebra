package org.geogebra.web.shared.view.button;

import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.RootPanel;

/**
 * This button disappears when disabled.
 */
public class DisappearingActionButton extends ActionButton {

	/**
	 * @param app  The app.
	 * @param view The wrapped view.
	 * @param title translation key for title
	 */
	public DisappearingActionButton(AppW app, RootPanel view, String title) {
		super(app, view, title);
	}

	@Override
	public void setEnabled(boolean enabled) {
		Dom.toggleClass(getView(), "hidden", !enabled);
	}
}
