package org.geogebra.web.full.main.video;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a placeholder for videos.
 *
 * @author Laszlo Gal
 *
 */
public class VideoOffline extends AbstractVideoPlayer {

	private final static int DEFAULT_WIDTH = 420;
	private final static int DEFAULT_HEIGHT = 365;
	private VideoErrorPanel errorPanel;

	/**
	 * Constructor. *
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player frame.
	 */
	VideoOffline(GeoVideo video, int id) {
		super(video, id);
		VendorSettings vendorSettings = ((AppW) app).getVendorSettings();
		String errorId = vendorSettings.getMenuLocalizationKey("VideoAccessError");
		errorPanel = new VideoErrorPanel(app.getLocalization(), errorId);
		stylePlayer();
		update();
	}

	/**
	 * Updates the player based on video object.
	 */
	@Override
	public void update() {
		setDefaultSize();
		Style style = asWidget().getElement().getStyle();
		style.setLeft(video.getScreenLocX(app.getActiveEuclidianView()),
				Unit.PX);
		style.setTop(video.getScreenLocY(app.getActiveEuclidianView()),
				Unit.PX);
		asWidget().setWidth(video.getWidth() + "px");
		asWidget().setHeight(video.getHeight() + "px");

		if (video.isBackground()) {
			asWidget().addStyleName("background");
		} else {
			asWidget().removeStyleName("background");
		}
	}

	private void setDefaultSize() {
		if (video.hasSize()) {
			return;
		}

		video.setWidth(DEFAULT_WIDTH);
		video.setHeight(DEFAULT_HEIGHT);
	}

	@Override
	public boolean isValid() {
		return true;
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
