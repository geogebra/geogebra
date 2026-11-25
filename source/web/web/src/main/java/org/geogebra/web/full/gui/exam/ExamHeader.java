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

package org.geogebra.web.full.gui.exam;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menu.icons.DefaultMenuIconResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.gui.toolbarpanel.MenuToggleButton;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.util.Persistable;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.dom.client.Element;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.jspecify.annotations.NonNull;

/**
 * Header shown on top of the app during exam (enabled by data-param-showExamHeader).
 * Contains the main menu button, exam name, elapsed time, optional exam hash
 * and a button to show exam details.
 */
public final class ExamHeader extends FlowPanel implements SetLabels, Persistable {

	/** Height of the header in pixels (including border) */
	public static final int HEIGHT = 64;

	private static final int TIMER_UPDATE_MS = 500;

	private final AppWFull app;
	private final Label examName;
	private final Label elapsedTime;
	private final Label examHash;
	private final IconButton examInfoButton;
	private final Timer timer = new Timer() {
		@Override
		public void run() {
			updateSessionInfo();
		}
	};

	/**
	 * @param app application
	 */
	public ExamHeader(@NonNull AppWFull app) {
		this.app = app;
		addStyleName("examHeader");

		examName = BaseWidgetFactory.INSTANCE.newPrimaryText("", "examName");
		add(examName);

		FlowPanel sessionControls = new FlowPanel();
		sessionControls.addStyleName("sessionControls");

		FlowPanel timerPanel = new FlowPanel();
		timerPanel.addStyleName("examTimerPanel");
		Image timerImg =
				new Image(MaterialDesignResources.INSTANCE.timer().getSafeUri().asString());
		timerImg.addStyleName("timerImg");
		AriaHelper.hide(timerImg);
		timerImg.getElement().setDraggable(Element.DRAGGABLE_FALSE);
		elapsedTime = new Label();
		elapsedTime.addStyleName("elapsedTime");
		timerPanel.add(timerImg);
		timerPanel.add(elapsedTime);
		sessionControls.add(timerPanel);

		examHash = new Label();
		examHash.addStyleName("examHash");
		examHash.setVisible(false);
		sessionControls.add(examHash);

		examInfoButton = new IconButton(
				app,
				() -> {},
				new ImageIconSpec(DefaultMenuIconResources.INSTANCE.assignment()),
				"exam_log_header");
		examInfoButton.addFastClickHandler(
				source -> app.getGuiManager().showExamInfoDialog(examInfoButton));
		sessionControls.add(examInfoButton);
		add(sessionControls);

		setLabels();
	}

	/**
	 * Move the main menu button to the header.
	 * @param menuButton main menu button
	 */
	public void setMenuButton(@NonNull MenuToggleButton menuButton) {
		menuButton.setExternal(true);
		insert(menuButton, 0);
	}

	/**
	 * Start updating the timer.
	 */
	public void start() {
		updateSessionInfo();
		timer.scheduleRepeating(TIMER_UPDATE_MS);
	}

	/**
	 * Stop updating the timer.
	 */
	public void stop() {
		timer.cancel();
	}

	private void updateSessionInfo() {
		ExamController examController = GlobalScope.getExamController(app);
		if (examController == null || !examController.isExamActive()) {
			return;
		}
		elapsedTime.setText(examController.getDurationFormatted(app.getLocalization()));
		String hash = GlobalHeader.getExamHash();
		examHash.setVisible(!StringUtil.empty(hash));
		examHash.setText(hash == null ? "" : hash);
	}

	@Override
	public void setLabels() {
		examName.setText(getExamName());
		examInfoButton.setLabels();
		updateSessionInfo();
	}

	private @NonNull String getExamName() {
		String featureSet = app.getAppletParameters().getParamFeatureSet();
		ExamController examController = GlobalScope.getExamController(app);
		ExamType examType = examController == null ? null : examController.getExamType();
		if (examType == null && !StringUtil.empty(featureSet)) {
			examType = ExamType.byName(featureSet);
		}
		return examType == null ? "" : examType.getDisplayName(app.getLocalization(), app.getConfig());
	}
}
