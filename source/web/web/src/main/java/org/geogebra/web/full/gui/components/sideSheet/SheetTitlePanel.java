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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.FocusUtil;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Panel holding the title, close button and possible back button for a side sheet or
 * a fixed sheet.
 */
public class SheetTitlePanel extends FlowPanel implements SetLabels {
	private final Label titleLabel;
	private final IconButton closeButton;
	private final AppW appW;
	private final String titleTransKey;

	/**
	 *
	 * @param appW application
	 * @param titleTransKey title translation key
	 * @param onClose close button callback
	 * @param onBack back button callback (null = no back button)
	 */
	public SheetTitlePanel(AppW appW, String titleTransKey,
			@Nonnull Runnable onClose, @CheckForNull Runnable onBack) {
		this.appW = appW;
		this.titleTransKey = titleTransKey;
		if (onBack != null) {
			addStyleName("withBackBtn");
			IconButton backButton = new IconButton(appW, onBack, new ImageIconSpec(
					GuiResourcesSimple.INSTANCE.arrow_back()), "Back");
			backButton.addStyleName("backBtn");
			add(backButton);
		}

		titleLabel = new Label(appW.getLocalization().getMenu(titleTransKey));
		titleLabel.addStyleName("title");
		add(titleLabel);

		closeButton = new IconButton(appW, onClose,
				new ImageIconSpec(GuiResourcesSimple.INSTANCE.close()), "Close");
		closeButton.addStyleName("closeBtn");
		closeButton.getElement().setAttribute("tooltip-position", "right");
		closeButton.setTabIndex(0);
		new FocusableWidget(AccessibilityGroup.SETTINGS_CLOSE_BUTTON,
				null, closeButton) {
			@Override
			protected void focus(Widget widget) {
				closeButton.addStyleName("keyboardFocus");
				closeButton.getElement().focus();
			}
		}.attachTo(appW);
		add(closeButton);
		addStyleName("titlePanel");
	}

	/**
	 * Focus the close button.
	 */
	public void focus() {
		FocusUtil.focusNoScroll(closeButton.getElement());
	}

	@Override
	public void setLabels() {
		titleLabel.setText(appW.getLocalization().getMenu(titleTransKey));
		closeButton.setLabels();
	}
}
