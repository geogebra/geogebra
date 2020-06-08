package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;

import com.google.gwt.user.client.ui.IsWidget;

public abstract class AbstractVideoPlayer implements IsWidget {

	/** The application */
	protected App app;

	/** Video geo to play */
	protected DrawVideo video;

	AbstractVideoPlayer(DrawVideo video) {
		this.video = video;
		app = video.getView().getApplication();
	}

	/**
	 * @return the associated GeoVideo object.
	 */
	public GeoVideo getVideo() {
		return video.getVideo();
	}

	protected void stylePlayer(int id) {
		asWidget().addStyleName("mowVideo");
		asWidget().addStyleName("mowWidget");
		asWidget().getElement().setId("video_player" + id);
	}

	abstract void update();

	/**
	 * @return if the player is valid.
	 */
	abstract boolean isValid();

	/**
	 * @param video2 other video
	 * @return whether the player is compatible with the oter video
	 */
	abstract boolean matches(GeoVideo video2);

	/**
	 * Sends the player background.
	 */
	public void setBackground(boolean background) {
		video.setBackground(background);
		update();
		if (!background) {
			asWidget().getElement().getStyle().clearZIndex();
		}
	}

	/**
	 * @return if player is offline.
	 */
	abstract boolean isOffline();
}
