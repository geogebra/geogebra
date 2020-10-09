package org.geogebra.web.html5.gui.view.button.header;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Label;

public class AppPickerButton extends StandardButton {

	private ResourcePrototype secondIcon;
	private App app;

	/**
	 * @param icon
	 *            - img of button
	 * @param label
	 *            - text of button
	 * @param app
	 *            - application
	 * @param secondIcon
	 * 	 *        - second image of button
	 */
	public AppPickerButton(final ResourcePrototype icon, final String label,
			App app, ResourcePrototype secondIcon) {
		super(app, label, icon);
		this.app = app;
		setIconLabelAndSecondIcon(icon, label, secondIcon, app);
	}

	private void setIconLabelAndSecondIcon(final ResourcePrototype image, final String label,
			final ResourcePrototype secondIcon, App app) {
		this.secondIcon = secondIcon;
		NoDragImage btnImage = new NoDragImage(image, 24, -1);
		btnImage.getElement().setTabIndex(-1);
		Label btnLabel = new Label(app.getLocalization().getMenu(label));
		AriaHelper.setAttribute(btnLabel, "data-trans-key", label);
		this.getElement().removeAllChildren();
		this.getElement().appendChild(btnImage.getElement());
		this.getElement().appendChild(btnLabel.getElement());
		NoDragImage secondImg = new NoDragImage(secondIcon, 24);
		secondImg.setStyleName("btnSecondIcon");
		this.getElement().appendChild(secondImg.getElement());
		btnImage.setPresentation();

		Roles.getButtonRole().removeAriaPressedState(getElement());
	}

	/**
	 * @param icon
	 *            - img of button
	 * @param label
	 *            - text of button
	 */
	public void setIconAndLabel(final ResourcePrototype icon, String label) {
		setIconLabelAndSecondIcon(icon, label, this.secondIcon, this.app);
	}
}
