package org.geogebra.web.html5.video;

import org.geogebra.common.kernel.geos.GeoVideo;

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
		errorPanel = new VideoErrorPanel(app.getLocalization(), video.getErrorId());
		stylePlayer();
	}

	/**
	 * Updates the player based on video object.
	 */
	public void update() {
		Style style = asWidget().getElement().getStyle();
		style.setLeft(video.getScreenLocX(app.getActiveEuclidianView()),
				Unit.PX);
		style.setTop(video.getScreenLocY(app.getActiveEuclidianView()),
				Unit.PX);
		if (video.hasSize()) {
			asWidget().setWidth(video.getWidth() + "px");
			asWidget().setHeight(video.getHeight() + "px");
		}
		if (video.isBackground()) {
			asWidget().addStyleName("background");
		} else {
			asWidget().removeStyleName("background");
		}
	}

	@Override
	public void onReady() {
		// intentionally empty
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void play() {
		// intentionally empty
	}

	@Override
	public void pause() {
		// intentionally empty
	}

	@Override
	public void sendBackground() {
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
}
