/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.components.sideSheet;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

class SideSheetPanel extends FlowPanel implements SetLabels {
	private final AppW appW;
	private SideSheetData data;

	private FlowPanel contentPanel;
	private StandardButton positiveButton;
	private StandardButton negativeButton;
	private final Runnable onClose;
	private SheetTitlePanel titlePanel;
	private final boolean hasBackButton;

	/**
	 * Side sheet with all optional elements (e.g. back button, buttons)
	 * @param appW {@link AppW}
	 * @param data {@link SideSheetData}
	 * @param addBackButton whether back button should be added in the title panel
	 * @param onClose close button handler
	 */
	SideSheetPanel(AppW appW, SideSheetData data, boolean addBackButton,
			Runnable onClose) {
		this.appW = appW;
		this.hasBackButton = addBackButton;
		this.onClose = onClose;
		update(data);
	}

	/**
	 * Side sheet without back button in title panel.
	 * @param appW {@link AppW}
	 * @param data {@link SideSheetData}
	 * @param onClose close button handler
	 */
	SideSheetPanel(AppW appW, SideSheetData data, Runnable onClose) {
		this(appW, data, false, onClose);
	}

	void update(SideSheetData data) {
		clear();
		this.data = data;
		addStyleName("sideSheet");
		addStyleName("floating");
		buildSideSheet(hasBackButton);
		setAccessibilityProperties();
	}

	private void buildSideSheet(boolean addBackButton) {
		buildTitlePanel(addBackButton);
		buildContentPanel();
		buildButtonPanel();
	}

	private void buildTitlePanel(boolean addBackButton) {
		titlePanel = new SheetTitlePanel(appW, data.getTitleTransKey(),
				this::onClose, addBackButton ? this::onBack : null);
		add(titlePanel);
	}

	private void buildContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("contentPanel");
		if (hasButtonPanel()) {
			contentPanel.addStyleName("withButtonPanel");
		}
		add(contentPanel);
	}

	private void buildButtonPanel() {
		if (!hasButtonPanel()) {
			return;
		}

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");

		if (data.hasPositiveBtn()) {
			initPositiveButton();
			buttonPanel.add(positiveButton);
		}
		if (data.hasNegativeBtn()) {
			initNegativeButton();
			buttonPanel.add(negativeButton);
		}
		add(buttonPanel);
	}

	private void initPositiveButton() {
		positiveButton = BaseWidgetFactory.INSTANCE.newFilledButton(
				appW.getLocalization().getMenu(data.getPositiveBtnTransKey()));
	}

	private void initNegativeButton() {
		negativeButton = BaseWidgetFactory.INSTANCE.newOutlinedButton(
				appW.getLocalization().getMenu(data.getNegativeBtnTransKey()));
	}

	/**
	 * Adds elements to the content panel
	 * @param widget ui element
	 */
	void addToContent(Widget widget) {
		contentPanel.add(widget);
	}

	private boolean hasButtonPanel() {
		return data.hasPositiveBtn() || data.hasNegativeBtn();
	}

	void onClose() {
		onClose.run();
		appW.getAccessibilityManager().focusAnchor();
	}

	private void onBack() {
		// to fill later, when it's needed
	}

	void addPositiveButtonRunnable(Runnable positiveHandler) {
		if (positiveButton != null) {
			positiveButton.addFastClickHandler((source) -> positiveHandler.run());
		}
	}

	/**
	 * Attach negative action handler
	 * @param negativeHandler handler
	 */
	void addNegativeButtonRunnable(Runnable negativeHandler) {
		if (negativeButton != null) {
			negativeButton.addFastClickHandler(source -> negativeHandler.run());
		}
	}

	private void setAccessibilityProperties() {
		AriaHelper.setRole(this, "complementary");
		AriaHelper.setLabel(this, appW.getLocalization().getMenu(data.getTitleTransKey()));
	}

	/**
	 * Focus the close button.
	 */
	void focus() {
		titlePanel.focus();
	}

	@Override
	public void setLabels() {
		titlePanel.setLabels();
		setAccessibilityProperties();
		if (positiveButton != null) {
			positiveButton.setText(appW.getLocalization().getMenu(data.getPositiveBtnTransKey()));
		}
		if (negativeButton != null) {
			negativeButton.setText(appW.getLocalization().getMenu(data.getNegativeBtnTransKey()));
		}
	}
}
