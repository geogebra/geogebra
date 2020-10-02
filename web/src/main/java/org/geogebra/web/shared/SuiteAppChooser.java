package org.geogebra.web.shared;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.dialog.CalcSwitcherPopup;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.menubar.PerspectivesPopup;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
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
		appChooserButton = new StandardButton(app, 40, res.menu_icon_algebra_transparent(), app.getLocalization().getMenu("GraphingCalculator.short"));
		appChooserButton.addStyleName("suiteAppChooserButton");
		showCurrentApp(app, appChooserButton);
		appChooserButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget event) {
				int i = 3; //popup
				CalcSwitcherPopup suitepopup = new CalcSwitcherPopup((AppWFull) app);
				suitepopup.showCalcPopup();
			}
		});
	}

	public StandardButton getAppChooserButton() {
		return appChooserButton;
	}

	private void showCurrentApp(AppW appW, StandardButton button) {

	}
}
