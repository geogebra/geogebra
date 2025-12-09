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

package org.geogebra.web.shared.components.infoError;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentInfoErrorPanel extends FlowPanel {
	private final Localization loc;
	private StandardButton actionButton;

	/**
	 * info/error panel constructor
	 * @param loc - localization
	 * @param data - data of the panel including title, subtext, icon and button text
	 * @param buttonAction - handler for the button
	 */
	public ComponentInfoErrorPanel(Localization loc, InfoErrorData data,
			Runnable buttonAction) {
		this.loc = loc;
		addStyleName("infoErrorPanel");
		buildGUI(data, buttonAction);
	}

	/**
	 * default info/error panel constructor without button with light bulb img
	 * @param loc - localization
	 * @param data - data of the panel including title, subtext and button text
	 */
	public ComponentInfoErrorPanel(Localization loc, InfoErrorData data) {
		this(loc, data, null);
	}

	private void buildGUI(InfoErrorData data, Runnable buttonAction) {
		NoDragImage infoImage = new NoDragImage(data.getImage(), 56, 56);
		add(infoImage);

		if (data.getTitle() != null) {
			Label titleLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
					loc.getMenu(data.getTitle()), "title");
			add(titleLabel);
		}

		if (data.getSubtext() != null) {
			Label subtextLabel = BaseWidgetFactory.INSTANCE.newSecondaryText(
					loc.getMenu(data.getSubtext()), "subtext");
			add(subtextLabel);
		}

		if (data.getActionButtonText() != null) {
			actionButton =
					new StandardButton(loc.getMenu(data.getActionButtonText()));
			actionButton.addStyleName("dialogContainedButton");
			actionButton.addFastClickHandler(source -> {
					if (!actionButton.getStyleName().contains("disabled")) {
						buttonAction.run();
					}
			});
			add(actionButton);
		}
	}

	/**
	 * Disable or enable the action button (TODO why style only?)
	 * @param disabled whether to disable it.
	 */
	public void disableActionButton(boolean disabled) {
		Dom.toggleClass(actionButton, "disabled", disabled);
	}
}
