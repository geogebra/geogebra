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

package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.main.App;

public class ButtonSizeModel extends OptionsModel {
	private IButtonSizeListener listener;

	public interface IButtonSizeListener extends PropertyListener {
		@MissingDoc
		void updateSizes(int width, int height, boolean isFixed);
	}

	public ButtonSizeModel(App app) {
		super(app);
	}

	public void setListener(IButtonSizeListener listener) {
		this.listener = listener;
	}

	private GeoButton getButtonAt(int index) {
		Object geo = getObjectAt(index);
		if (geo instanceof GeoButton) {
			return (GeoButton) geo;
		}

		return null;
	}

	@Override
	public void updateProperties() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);

			if (geo != null) {
				listener.updateSizes(geo.getWidth(), geo.getHeight(),
						geo.isFixedSize());
			}
		}

	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isGeoButton()
				&& !getGeoAt(index).isGeoInputBox();
	}

	public void setSizesFromString(String strWidth, String strHeight,
			boolean isFixed) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);

			if (geo != null) {
				geo.setFixedSize(isFixed);
				if (isFixed) {
					geo.setHeight(Integer.parseInt(strHeight));
					geo.setWidth(Integer.parseInt(strWidth));
				} else {
					geo.setFixedSize(false);
				}
			}
		}
	}

	public void applyChanges(boolean isFixed) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);

			if (geo != null) {
				geo.setFixedSize(isFixed);
				listener.updateSizes(geo.getWidth(), geo.getHeight(), isFixed);
			}
		}
		storeUndoInfo();
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
