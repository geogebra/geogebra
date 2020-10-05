package org.geogebra.web.shared;

import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.dialog.CalcSwitcherPopup;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import com.google.gwt.user.client.ui.Widget;

public class SuiteAppChooser {
	protected final AppW app;
	private StandardButton appChooserButton;

	public SuiteAppChooser(AppW app) {
		this.app = app;
		createAppChooserButton();
	}

	private void createAppChooserButton () {

		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		appChooserButton = new StandardButton(res.menu_icon_algebra_transparent(),
				app.getLocalization().getMenu("GraphingCalculator.short"), 24, app,
				GuiResources.INSTANCE.triangle_down_dark());
		appChooserButton.addStyleName("suiteAppChooserButton");
		CalcSwitcherPopup suitepopup = new CalcSwitcherPopup((AppWFull) app, appChooserButton);
		appChooserButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget event) {
				suitepopup.showCalcPopup();
			}
		});
	}

	public StandardButton getAppChooserButton() {
		return appChooserButton;
	}
}
