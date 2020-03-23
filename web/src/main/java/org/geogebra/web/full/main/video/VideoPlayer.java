package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Represents a placeholder for videos.
 *
 * @author Laszlo Gal
 *
 */
public abstract class VideoPlayer extends AbstractVideoPlayer {

	/**
	 * Constructor. *
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player frame.
	 */
	VideoPlayer(DrawVideo video, int id) {
		super(video);
		createGUI();
		stylePlayer(id);
	}

	/**
	 * Build the GUI here
	 */
	protected abstract void createGUI();

	/**
	 * Updates the player based on video object.
	 */
	@Override
	public void update() {
		Style style = asWidget().getElement().getStyle();
		style.setLeft(getVideo().getScreenLocX(app.getActiveEuclidianView()),
				Unit.PX);
		style.setTop(getVideo().getScreenLocY(app.getActiveEuclidianView()),
				Unit.PX);
		if (getVideo().hasSize()) {
			asWidget().setWidth(getVideo().getWidth() + "px");
			asWidget().setHeight(getVideo().getHeight() + "px");
		}
		if (getVideo().isBackground()) {
			asWidget().addStyleName("background");
		} else {
			asWidget().removeStyleName("background");
		}
		video.getView().repaintView();
	}

	/**
	 *
	 * @return if iframe is valid.
	 */
	protected native boolean isFrameValid() /*-{
		return this.contentWindow != null;
	}-*/;

	@Override
	boolean isOffline() {
		return false;
	}
}
