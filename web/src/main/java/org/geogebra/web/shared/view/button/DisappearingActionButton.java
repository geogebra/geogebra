package org.geogebra.web.shared.view.button;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.Dom;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * This button disappears when disabled.
 */
public class DisappearingActionButton extends ActionButton {

	/**
	 * @param app  The app.
	 * @param view The wrapped view.
	 */
	public DisappearingActionButton(App app, RootPanel view) {
		super(app, view);
	}

	@Override
	public void setEnabled(boolean enabled) {
		Dom.toggleClass(getView(), "hidden", !enabled);
	}
}
