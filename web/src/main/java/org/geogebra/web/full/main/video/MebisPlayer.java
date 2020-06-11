package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
