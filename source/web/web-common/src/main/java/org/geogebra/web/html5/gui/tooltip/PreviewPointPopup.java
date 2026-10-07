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

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.gui.popup.specialpoint.SpecialPointPopupHelper;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
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
	 */
	public PreviewPointPopup(AppW appW) {
		super(appW.getAppletFrame(), appW);
		this.app = appW;
		content = new FlowPanel();
		add(content);
		this.addStyleName("previewPointsPopup");
		setAutoHideEnabled(true);
	}

	/**
	 * Position the popup for given special points.
	 *
	 * @param offsetWidth
	 *            width of popup
	 * @param offsetHeight
	 *            height of popup
	 * @param geos list of special points
	 */
	public void positionPopup(int offsetWidth, int offsetHeight, List<GeoElement> geos) {
		StylebarPositioner positioner = new StylebarPositioner(app);
		positioner.setCenter(true);
		int minY = 33;
		GRectangle rectangle = new Rectangle(
				offsetWidth / 2,
				minY,
				app.getActiveEuclidianView().getViewWidth() - offsetWidth,
				app.getActiveEuclidianView().getViewHeight() - offsetHeight - minY);
		GPoint pos = positioner.getPositionFor(geos.get(0), offsetHeight, 0, rectangle);
		if (pos != null) {
			this.setPopupPosition(
					pos.getX()
							+ app.getActiveEuclidianView().getAbsoluteLeft()
							- (int) ((AppW) app).getAbsLeft()
							- offsetWidth / 2,
					pos.getY());
		} else {
			hide(true);
		}
	}

	/**
	 * @param previewPoints points whose coordinates should be displayed
	 */
	public void createContent(List<GeoElement> previewPoints) {
		content.clear();
		List<String> contentRows = SpecialPointPopupHelper.getContentRows(app, previewPoints);
		for (String row : contentRows) {
			addToContent(row);
		}
	}

	private void addToContent(String lbl) {
		if (!lbl.isEmpty()) {
			content.add(new Label(lbl));
		}
	}
}
