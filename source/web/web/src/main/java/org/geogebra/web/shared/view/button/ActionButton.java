package org.geogebra.web.shared.view.button;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.ActionView;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.RootPanel;

/**
 * A view element that can perform an action.
 */
public class ActionButton implements ActionView, SetLabels {

	private final AppW app;
	private final RootPanel view;
	private String titleLocalizationKey;

	/**
	 * @param app The app.
	 * @param view The wrapped view.
	 * @param title translation key for title
	 */
	public ActionButton(AppW app, RootPanel view, String title) {
		this.app = app;
		this.view = view;
		setTitle(title);
		app.getLocalization().registerLocalizedUI(this);
	}

	@Override
	public void setAction(final Runnable action) {
		if (action != null) {
			ClickStartHandler.init(
					view,
					new ClickStartHandler(true, true) {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					action.run();
				}
			});
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		Dom.toggleClass(view, "buttonActive", "buttonInactive", enabled);
	}

	/**
	 * Sets the title for the view with the localization key.
	 * @param titleLocalizationKey The localization key for the title.
	 */
	public void setTitle(String titleLocalizationKey) {
		this.titleLocalizationKey = titleLocalizationKey;
		setLabels();
	}

	RootPanel getView() {
		return view;
	}

	@Override
	public void setLabels() {
		AriaHelper.setTitle(view, app.getLocalization().getMenu(titleLocalizationKey));
	}
}
