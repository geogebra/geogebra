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

package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.SliderEventHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.resources.SVGResource;

/**
 * Stylebar popup for 3D rotation
 */
class RotateViewPopup extends PopupMenuButtonW {

	private final EuclidianStyleBar3DW euclidianStyleBar3DW;
	private final ImageOrText pauseIcon;
	private final ImageOrText playIcon;

	/**
	 * @param euclidianStyleBar3DW
	 *            stylebar
	 * @param playIcon
	 *            play icon
	 * @param pauseIcon
	 *            pause icon
	 */
	public RotateViewPopup(EuclidianStyleBar3DW euclidianStyleBar3DW,
			SVGResource playIcon, SVGResource pauseIcon) {
		super(euclidianStyleBar3DW.app, null, -1, -1, SelectionTable.MODE_ICON,
				false, true);
		setChangeEventHandler(new SliderEventHandler() {
			@Override
			public void onValueChange() {
				setRotation();
				app.storeUndoInfo();
			}

			@Override
			public void onSliderInput() {
				setRotation();
			}
		});
		this.euclidianStyleBar3DW = euclidianStyleBar3DW;

		this.playIcon = new ImageOrText(playIcon, 24);
		this.pauseIcon = new ImageOrText(pauseIcon, 24);

		setIcon(this.playIcon);

		getSlider().setMinimum(-10);
		getSlider().setMaximum(10);
		getSlider().setTickSpacing(1);
		setSliderValue(5);
	}

	protected void setRotation() {
		this.euclidianStyleBar3DW.getView().setRotContinueAnimation(0, getSliderValue() * 0.01);
		if (getSliderValue() == 0) {
			setIcon(playIcon);
		} else {
			setIcon(pauseIcon);
		}
	}

	@Override
	protected void onClickAction() {
		if (this.euclidianStyleBar3DW.getView().isRotAnimatedContinue()) {
			this.euclidianStyleBar3DW.getView().stopAnimation();
			setIcon(playIcon);
		} else {
			this.euclidianStyleBar3DW.getView().setRotContinueAnimation(0, getSliderValue() * 0.01);
			setIcon(pauseIcon);
		}
	}

	@Override
	public void update(List<GeoElement> geos) {
		this.setVisible(geos.isEmpty() && !EuclidianView.isPenMode(app.getMode())
						&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}
}
