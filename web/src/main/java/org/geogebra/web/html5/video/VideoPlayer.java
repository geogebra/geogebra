package org.geogebra.web.html5.video;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.html5.gui.Persistable;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Frame;

/**
 * Represents a placeholder for videos.
 * 
 * @author Laszlo Gal
 *
 */
public class VideoPlayer extends Frame implements Persistable {
	private GeoVideo video;
	private String embedUrl = null;

	/**
	 * Constructor.
	 * 
	 * @param video
	 *            the video object.
	 */
	public VideoPlayer(GeoVideo video) {
		super(video.getEmbeddedUrl());
		this.video = video;
		addStyleName("mowVideo");
		getElement().setId(video.getYouTubeId());
		embedUrl = video.getEmbeddedUrl();
	}
	
	/**
	 * Updates the player based on video object.
	 */
	public void update() {
		Style style = getElement().getStyle();
		style.setLeft(video.getAbsoluteScreenLocX(), Unit.PX);
		style.setTop(video.getAbsoluteScreenLocY(), Unit.PX);
		setWidth(video.getWidth() + "px");
		setHeight(video.getHeight() + "px");
		if (video.isBackground()) {
			addStyleName("background");
			if (!video.getEmbeddedUrl().equals(embedUrl)) {
				embedUrl = video.getEmbeddedUrl();
			}
		} else {
			removeStyleName("background");
		}
	}
}

