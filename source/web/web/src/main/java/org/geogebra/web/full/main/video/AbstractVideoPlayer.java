package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.IsWidget;

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

	/**
	 * Updates the player based on video object.
	 */
	public void update() {
		Style style = asWidget().getElement().getStyle();
		style.setLeft(video.getLeft(), Unit.PX);
		style.setTop(video.getTop(), Unit.PX);

		if (getVideo().hasSize()) {
			asWidget().setWidth(getVideo().getWidth() + "px");
			asWidget().setHeight(getVideo().getHeight() + "px");
			style.setProperty("transformOrigin", "0 0");
			style.setProperty("transform", "rotate(" + getVideo().getAngle() + "rad)");
		}
		if (getVideo().isBackground()) {
			asWidget().addStyleName("background");
		} else {
			asWidget().removeStyleName("background");
		}
		video.getView().repaintView();
	}

	/**
	 * @param video2 other video
	 * @return whether the player is compatible with the other video
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
