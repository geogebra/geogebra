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
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Player for videos hosted by Mebis.
 * 
 * @author laszlo
 *
 */
public class MebisPlayer extends HTML5Player {

	/**
	 * Constructor
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player.
	 */
	public MebisPlayer(DrawVideo video, int id) {
		super(video, id);
	}

	@Override
	protected Widget getErrorWidget() {
		return new Label(app.getLocalization().getMenuDefault("MebisAccessError",
						"Something went wrong. Please, check "
						+ "if you are online and logged in to Mebis"));
	}
}
