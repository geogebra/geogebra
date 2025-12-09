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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class AnimatingModel extends BooleanOptionModel {
	private Kernel kernel;

	public AnimatingModel(App app, IBooleanOptionListener listener) {
		super(listener, app);
		kernel = app.getKernel();
	}

	@Override
	public void applyChanges(boolean value) {
		super.applyChanges(value);
		if (value) {
			kernel.getAnimationManager().startAnimation();
		}
		storeUndoInfo();
	}

	@Override
	public String getTitle() {
		return "Animating";
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isAnimatable();
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isAnimating();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setAnimating(value);
		geo.updateRepaint();
	}

}
