package org.geogebra.web.shared;

import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.dialog.CalcSwitcherPopup;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import com.google.gwt.user.client.ui.Widget;

public class SuiteHeaderAppPicker {
	protected final AppW app;
	private StandardButton appPickerButton;

	/**
	 * @param app
	 *            - application
	 */
	public SuiteHeaderAppPicker(AppW app) {
		this.app = app;
		createAppPickerButton();
	}

	private void createAppPickerButton() {
		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		appPickerButton = new StandardButton(res.menu_icon_algebra_transparent(),
				app.getLocalization().getMenu("GraphingCalculator.short"), 24, app,
				GuiResources.INSTANCE.triangle_down_dark());
		appPickerButton.addStyleName("suiteAppPickerButton");
		CalcSwitcherPopup suitePopup = new CalcSwitcherPopup((AppWFull) app, appPickerButton);
		appPickerButton.addFastClickHandler(event -> suitePopup.showCalcSwitcherPopup());
	}

	/**
	 * @return app picker button
	 */
	public StandardButton getAppPickerButton() {
		return appPickerButton;
	}
}
