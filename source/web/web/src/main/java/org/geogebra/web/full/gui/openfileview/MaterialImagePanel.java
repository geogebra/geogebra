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

package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.images.AppResources;
import org.gwtproject.user.client.ui.FlowPanel;

class MaterialImagePanel extends FlowPanel {

	public MaterialImagePanel(Material material) {
		setStyleName("cardImgPanel");
		setBackground(material);
	}

	/**
	 * Update thumbnail from material
	 * @param m material
	 */
	public void setBackground(Material m) {
		final String thumb = m.getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			setBackgroundImageUrl(thumb);
		} else {
			setBackgroundImageUrl(AppResources.INSTANCE.geogebra64().getSafeUri().asString());
		}
	}

	private void setBackgroundImageUrl(String url) {
		getElement().getStyle().setBackgroundImage(
				"url(" + url + ")");
	}
}
