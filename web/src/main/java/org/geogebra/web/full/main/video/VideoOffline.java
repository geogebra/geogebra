package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Widget;

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
