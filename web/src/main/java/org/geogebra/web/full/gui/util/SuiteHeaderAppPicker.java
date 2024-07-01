package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.AppDescription;
import org.geogebra.web.full.gui.dialog.AppSwitcherPopup;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RootPanel;

public class SuiteHeaderAppPicker extends StandardButton {
	private final AppW appW;
	private AppSwitcherPopup suitePopup;

	/**
	 * @param app
	 *            - application
	 */
	public SuiteHeaderAppPicker(AppW app) {
		this.appW = app;
		createAppPickerButton(app);
		setExpanded(false);
	}

	/**
	 * Add app picker button in external header for suite
	 *
	 * @param app
	 *            application
	 * @return app picker button
	 */
	public static SuiteHeaderAppPicker addSuiteAppPicker(AppWFull app) {
		RootPanel appPickerPanel = RootPanel.get("suiteAppPicker");
		if (appPickerPanel != null) {
			SuiteHeaderAppPicker suiteHeaderAppPicker = new SuiteHeaderAppPicker(app);
			new FocusableWidget(AccessibilityGroup.SUBAPP_CHOOSER, null,
					suiteHeaderAppPicker).attachTo(app);
			appPickerPanel.add(suiteHeaderAppPicker);
			GlobalHeader.onResize();
			return suiteHeaderAppPicker;
		}
		return null;
	}

	private void createAppPickerButton(AppW app) {
		setIconAndLabel(((AppWFull) appW).getLastUsedSubApp());
		setStyleName("suiteAppPickerButton");
		suitePopup = new AppSwitcherPopup((AppWFull) app, this);
		suitePopup.addCloseHandler(close -> setExpanded(false));
		addFastClickHandler(event -> {
			setExpanded(true);
			suitePopup.showPopup();
		});
	}

	private void setExpanded(boolean expanded) {
		getElement().setAttribute("aria-expanded", String.valueOf(expanded));
	}

	/**
	 * @param appCode
	 *            - subapp code
	 */
	public void setIconAndLabel(String appCode) {
		AppDescription description = AppDescription.get(appCode);
		NoDragImage btnImage = new NoDragImage(description.getIcon(), 24, 24);
		btnImage.getElement().setTabIndex(-1);
		String label = description.getNameKey();
		Label btnLabel = new Label(appW.getLocalization().getMenu(label));
		AriaHelper.setAttribute(btnLabel, "data-trans-key", label);

		this.getElement().removeAllChildren();
		this.getElement().appendChild(btnImage.getElement());
		this.getElement().appendChild(btnLabel.getElement());

		NoDragImage dropDownImg =
				new NoDragImage(MaterialDesignResources.INSTANCE.arrow_drop_down(), 24);
		dropDownImg.setStyleName("dropDownImg");
		this.getElement().appendChild(dropDownImg.getElement());
		btnImage.setPresentation();

		getElement().removeAttribute("aria-pressed");
	}
}
