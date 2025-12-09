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

import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;

public class CenterImageModel extends BooleanOptionModel {

	public CenterImageModel(App app) {
		super(null, app);
	}

	private GeoImage getImageAt(int index) {
		return (GeoImage) getGeoAt(index);
	}

	@Override
	public boolean getValueAt(int index) {
		return getImageAt(index).isCentered();
	}

	@Override
	public void apply(int index, boolean value) {
		getImageAt(index).setCentered(value);
	}

	@Override
	public String getTitle() {
		return "CenterImage";
	}

	@Override
	protected boolean isValidAt(int index) {
		return getGeoAt(index) instanceof GeoImage
				&& getGeoAt(index).isIndependent()
				&& !getImageAt(index).isAbsoluteScreenLocActive();
	}

}
