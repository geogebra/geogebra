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

package org.geogebra.web.full.gui.toolbar.mow.toolbox.ruler;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.ToolIconButton;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;

public class RulerIconButton extends ToolIconButton {
	private final EuclidianController ec;
	private RulerPopup rulerPopup;
	private final AppW appW;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - title
	 * @param dataTest - ui test id
	 */
	public RulerIconButton(AppW appW, IconSpec icon, String ariaLabel, String dataTitle,
			String dataTest) {
		super(appW, icon, ariaLabel, dataTitle, dataTest, null);
		this.appW = appW;
		ec = appW.getActiveEuclidianView().getEuclidianController();
		addFastClickHandler((event) -> {
			appW.closePopups();
			setActive(!isActive());
			initRulerTypePopup();
			if (!isActive()) {
				rulerPopup.hide();
			} else {
				showRulerTypePopup();
			}
			handleRuler();
		});
	}

	private void initRulerTypePopup() {
		if (rulerPopup == null) {
			rulerPopup = new RulerPopup(appW, this);
		}
	}

	private void showRulerTypePopup() {
		rulerPopup.updatePopupSelection();
		ToolboxPopupPositioner.showRelativeToToolbox(rulerPopup.getPopupPanel(), this, appW);
	}

	/**
	 * set active ruler, or remove it in switch ruler off
	 */
	public void handleRuler() {
		if (isActive()) {
			appW.setMode(rulerPopup.getActiveRulerType());
		} else {
			removeTool();
			appW.setMoveMode();
		}
	}

	/**
	 * remove measurement tool from construction
	 */
	public void removeTool() {
		ec.removeMeasurementTool(rulerPopup.getActiveRulerType());
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (rulerPopup != null) {
			rulerPopup.setLabels();
		}
	}
}
