package org.geogebra.web.shared;

import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.AppSwitcherPopup;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class SuiteHeaderAppPicker extends StandardButton {

	private AppSwitcherPopup suitePopup;

	/**
	 * @param app
	 *            - application
	 */
	public SuiteHeaderAppPicker(AppW app) {
		super(app);
		createAppPickerButton(app);
	}

	private void createAppPickerButton(AppW app) {
		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		setIconAndLabel(res.menu_icon_algebra_transparent(), "GraphingCalculator.short",
				app);
		addStyleName("suiteAppPickerButton");
		suitePopup = new AppSwitcherPopup((AppWFull) app, this);
		addFastClickHandler(event -> suitePopup.showPopup(app));
	}

	/**
	 * @param image
	 *            - image of button
	 * @param label
	 *            - text of button
	 * @param app
	 *            - application
	 */
	public void setIconAndLabel(final ResourcePrototype image, final String label,
			App app) {
		NoDragImage btnImage = new NoDragImage(image, 24, -1);
		btnImage.getElement().setTabIndex(-1);

		Label btnLabel = new Label(app.getLocalization().getMenu(label));
		AriaHelper.setAttribute(btnLabel, "data-trans-key", label);

		this.getElement().removeAllChildren();
		this.getElement().appendChild(btnImage.getElement());
		this.getElement().appendChild(btnLabel.getElement());

		NoDragImage secondImg =
				new NoDragImage(MaterialDesignResources.INSTANCE.arrow_drop_down(), 24);
		secondImg.setStyleName("btnSecondIcon");
		this.getElement().appendChild(secondImg.getElement());
		btnImage.setPresentation();

		Roles.getButtonRole().removeAriaPressedState(getElement());
	}

	/**
	 * sets the button visibility depending on overlapping of divs
	 */
	public void checkButtonVisibility() {
		final RootPanel appPickerPanel = RootPanel.get("suiteAppPicker");
		int buttonRight = appPickerPanel.getAbsoluteLeft() + appPickerPanel.getOffsetWidth();
		int buttonsLeft = RootPanel.get("buttonsID").getAbsoluteLeft();
		final Style style = appPickerPanel.getElement().getStyle();
		if (buttonsLeft < buttonRight) {
			style.setProperty("visibility", "hidden");
		} else {
			style.setProperty("visibility", "visible");
		}
	}
}
