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

package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentSnackbar extends FlowPanel {

	public static final int TOOL_TOOLTIP_DURATION = 8000;
	public static final int DEFAULT_TOOLTIP_DURATION = 4000;
	private StandardButton actionBtn;
	private Runnable btnAction;
	private int showDuration;
	private Timer fadeIn = new Timer() {
		@Override
		public void run() {
			addStyleName("fadeIn");
			fadeOut.schedule(showDuration);
		}
	};
	private Timer fadeOut = new Timer() {
		@Override
		public void run() {
			removeStyleName("fadeIn");
			remove.schedule(2000);
		}
	};
	private Timer remove = new Timer() {
		@Override
		public void run() {
			removeFromParent();
		}
	};

	/**
	 * constructor
	 * @param app see {@link AppW}
	 * @param toolTip tooltip data
	 */
	public ComponentSnackbar(AppW app, ToolTip toolTip) {
		addStyleName("snackbarComponent");
		if (toolTip.isAlert()) {
			AriaHelper.setRole(this, "alert");
		}
		getElement().setId("snackbarID");
		buildGui(toolTip, app);
		app.getAppletFrame().add(this);
		fadeIn.schedule(100);
	}

	private void buildGui(ToolTip toolTip, AppW app) {
		FlowPanel textContainer = new FlowPanel();
		textContainer.addStyleName("txtContainer");

		String[] textLines = toolTip.title.split("\\n");
		for (String line : textLines) {
			Label textLbl = new Label(line);
			textLbl.addStyleName("title");
			textContainer.add(textLbl);
		}

		if (toolTip.helpText != null) {
			Label textLbl = new Label(toolTip.helpText);
			textLbl.addStyleName("text");
			textContainer.add(textLbl);
		}
		add(textContainer);

		if (toolTip.buttonTransKey != null) {
			actionBtn = new StandardButton(app.getLocalization()
					.getMenu(toolTip.buttonTransKey));
			actionBtn.addStyleName("materialTextButton");
			if (shouldAddButton(toolTip)) {
				add(actionBtn);
			}
			actionBtn.addFastClickHandler(source -> {
				if (btnAction != null) {
					btnAction.run();
					fadeOut.run();
				}
			});
		}
	}

	/**
	 * @param toolTip - tooltip data
	 * @return whether should add button, dont allow redirects in exam mode
	 */
	private boolean shouldAddButton(ToolTip toolTip) {
		return GlobalScope.examController.isIdle()
				|| "Share".equals(toolTip.buttonTransKey);
	}

	/**
	 * set button action
	 * @param action - what should happen on positive button hit
	 */
	public void setButtonAction(Runnable action) {
		btnAction = action;
	}

	/**
	 * fade out snackbar without delay
	 */
	public void hide() {
		fadeOut.run();
		btnAction = null;
	}

	public void setShowDuration(int showDuration) {
		this.showDuration = showDuration;
	}
}
