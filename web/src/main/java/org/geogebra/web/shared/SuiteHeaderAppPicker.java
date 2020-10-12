package org.geogebra.web.shared;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.CalcSwitcherPopup;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.view.button.header.AppPickerButton;
import org.geogebra.web.html5.main.AppW;

public class SuiteHeaderAppPicker {
	protected final AppW app;
	private AppPickerButton appPickerButton;
	private CalcSwitcherPopup suitePopup;

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
		appPickerButton = new AppPickerButton(res.menu_icon_algebra_transparent(),
				"GraphingCalculator.short", app,
				MaterialDesignResources.INSTANCE.arrow_drop_down_transparent());
		appPickerButton.addStyleName("suiteAppPickerButton");
		createAppPickerPopup();
		appPickerButton.addFastClickHandler(event -> suitePopup.show());
	}

	private void createAppPickerPopup() {
		suitePopup = new CalcSwitcherPopup((AppWFull) app, appPickerButton);
	}

	/**
	 * @return app picker button
	 */
	public StandardButton getAppPickerButton() {
		return appPickerButton;
	}
}
