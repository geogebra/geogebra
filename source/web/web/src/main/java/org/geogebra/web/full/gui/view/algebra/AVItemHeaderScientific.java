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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

/**
 * Header for numbered rows
 */
public class AVItemHeaderScientific extends SimplePanel
		implements AlgebraItemHeader {

	private final Label number;
	private NoDragImage warningImage;
	private Localization loc;

	/**
	 * Create new number header
	 */
	public AVItemHeaderScientific(Localization loc) {
		this.loc = loc;
		setStyleName("avItemHeaderScientific");
		number = BaseWidgetFactory.INSTANCE.newSecondaryText("", "avItemNumber");
		setWidget(number);
	}

	@Override
	public void updateIcons(boolean warning) {
		setWidget(warning ? getWarningImage() : number);
		if (warning) {
			AriaHelper.setTitle(this, loc.getInvalidInputError());
		} else {
			AriaHelper.removeTitle(this);
		}
	}

	private NoDragImage getWarningImage() {
		if (warningImage == null) {
			warningImage = new NoDragImage(MaterialDesignResources.INSTANCE.wrong_input(), 24);
			warningImage.addStyleName("avWarningScientific");
		}
		return warningImage;
	}

	@Override
	public void setLabels() {
		// no localization
	}

	@Override
	public void update() {
		// nothing to do
	}

	@Override
	public boolean isHit(int x, int y) {
		return false;
	}

	@Override
	public void setIndex(int itemCount) {
		number.setText(itemCount + ")");
	}

	@Override
	public void setError(String errorMessage) {
		// not implemented
	}

}
