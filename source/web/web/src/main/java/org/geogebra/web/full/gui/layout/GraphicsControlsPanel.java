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

package org.geogebra.web.full.gui.layout;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.user.client.ui.FlowPanel;

public class GraphicsControlsPanel extends FlowPanel implements DockControlPanel {
	private final AppW app;
	private IconButton graphicsContextMenuBtn;

	/**
	 * Panel wrapping the settings icon and optional controls
	 * @param app application
	 * @param parent parent dock panel
	 */
	public GraphicsControlsPanel(AppW app, DockPanelW parent) {
		this.app = app;
		if (app.letShowPropertiesDialog() && !app.isWhiteboardActive()
				&& parent.getViewId() != App.VIEW_PROBABILITY_CALCULATOR) {
			addSettingsIcon(parent);
		}
		setStyleName("graphicsControlsPanel");
	}

	private void addSettingsIcon(final DockPanelW parent) {
		graphicsContextMenuBtn = new IconButton(app, null,
				new ImageIconSpec(MaterialDesignResources.INSTANCE.settings_border()), "Settings");
		final FocusableWidget focusableWidget = new FocusableWidget(
				AccessibilityGroup.getViewGroup(parent.getViewId()),
				AccessibilityGroup.ViewControlId.SETTINGS_BUTTON, graphicsContextMenuBtn);
		if (parent.getViewId() == App.VIEW_EUCLIDIAN) {
			focusableWidget.attachTo(app);
		}

		graphicsContextMenuBtn.addFastClickHandler(source -> {
			app.getAccessibilityManager().setAnchor(focusableWidget);
			onGraphicsSettingsPressed();
		});

		graphicsContextMenuBtn.getElement().setTabIndex(0);
		TestHarness.setAttr(graphicsContextMenuBtn, "graphicsViewContextMenu");
		graphicsContextMenuBtn.setTooltipPositionRight();
		add(graphicsContextMenuBtn);
	}

	/** Graphics Settings button handler */
	private void onGraphicsSettingsPressed() {
		app.closeMenuHideKeyboard();
		app.getDialogManager().showPropertiesDialog(OptionType.GLOBAL, null);
	}

	@Override
	public void setLabels() {
		if (graphicsContextMenuBtn != null) {
			graphicsContextMenuBtn.setLabels();
		}
	}

	@Override
	public void setLayout() {
		// no layout
	}
}
