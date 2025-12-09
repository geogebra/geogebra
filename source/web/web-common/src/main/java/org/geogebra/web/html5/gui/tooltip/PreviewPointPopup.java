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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.popup.specialpoint.SpecialPointPopupHelper;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * @author csilla
 *
 */
public class PreviewPointPopup extends GPopupPanel {

	private final FlowPanel content;

	/**
	 * @param appW
	 *            application
	 * @param previewPoints
	 *            list of preview points
	 */
	public PreviewPointPopup(AppW appW, ArrayList<GeoElement> previewPoints) {
		super(appW.getAppletFrame(), appW);
		this.app = appW;
		content = new FlowPanel();
		this.addStyleName("previewPointsPopup");
		createContent(previewPoints);
		add(content);
		setAutoHideEnabled(true);
	}

	/**
	 * position popup
	 * 
	 * @param offsetWidth
	 *            width of popup
	 * @param offsetHeight
	 *            height of popup
	 */
	public void positionPopup(int offsetWidth, int offsetHeight,
			ArrayList<GeoElement> geos) {
		StylebarPositioner positioner = new StylebarPositioner(app);
		positioner.setCenter(true);
		GPoint pos = positioner.getPositionFor(geos, offsetHeight, 33,
				app.getActiveEuclidianView().getViewHeight() - offsetHeight,
				offsetWidth / 2,
				app.getActiveEuclidianView().getViewWidth() - offsetWidth / 2);
		if (pos != null) {
			this.setPopupPosition(
					pos.getX() + app.getActiveEuclidianView().getAbsoluteLeft()
							- (int) ((AppW) app).getAbsLeft()
							- offsetWidth / 2,
					pos.getY());
		} else {
			hide(true);
		}
	}

	private void createContent(ArrayList<GeoElement> previewPoints) {
		List<String> contentRows = SpecialPointPopupHelper.getContentRows(app, previewPoints);
		for (String row : contentRows) {
			Label lbl = new Label(row);
			addToContent(lbl);
		}
	}

	private void addToContent(Label lbl) {
		if (!"".equals(lbl.getText())) {
			content.add(lbl);
		}
	}
}
