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

package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Represents a placeholder for videos.
 *
 * @author Laszlo Gal
 *
 */
public class VideoOffline extends AbstractVideoPlayer {

	private VideoErrorPanel errorPanel;

	/**
	 * Constructor. *
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player frame.
	 */
	VideoOffline(DrawVideo video, int id) {
		super(video);
		VendorSettings vendorSettings = ((AppW) app).getVendorSettings();
		String errorId = vendorSettings.getMenuLocalizationKey("VideoAccessError");
		errorPanel = new VideoErrorPanel(app.getLocalization(), errorId);
		stylePlayer(id);
		update();
	}

	@Override
	public void setBackground(boolean background) {
		// intentionally empty
	}

	@Override
	public boolean matches(GeoVideo video2) {
		return false;
	}

	@Override
	public Widget asWidget() {
		return errorPanel;
	}

	@Override
	boolean isOffline() {
		return true;
	}
}
