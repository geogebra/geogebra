package org.geogebra.web.html5.video;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Represents a placeholder for videos.
 *
 * @author Laszlo Gal
 *
 */
public abstract class VideoPlayer implements IsWidget {
	/** The application */
	protected App app;

	/** Video geo to play */
	protected GeoVideo video;
	private String playerId;

	/**
	 * Constructor. *
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player frame.
	 */
	public VideoPlayer(GeoVideo video, int id) {
		this.video = video;
		app = video.getKernel().getApplication();
		playerId = "video_player" + id;
		createGUI();
		stylePlayer();
	}

	/**
	 * Put your styling here.
	 */
	protected void stylePlayer() {
		asWidget().addStyleName("mowVideo");
		asWidget().addStyleName("mowWidget");
		asWidget().getElement().setId(playerId);
	}

	/**
	 * Build the GUI here
	 */
	protected abstract void createGUI();

	/**
	 * Updates the player based on video object.
	 */
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
		video.getKernel().getApplication().getActiveEuclidianView().repaintView();
	}

	/**
	 *
	 * @return the associated GeoVideo object.
	 */
	public GeoVideo getVideo() {
		return video;
	}

	/**
	 * Called after video specified by its id is loaded.
	 *
	 */
	public void onReady() {
		video.setBackground(true);
		EuclidianView view = app.getActiveEuclidianView();
		Drawable d = ((Drawable) view.getDrawableFor(video));
		d.update();
		if (d.getBoundingBox().getRectangle() != null) {
			view.setBoundingBox(d.getBoundingBox());
			view.repaintView();
			app.getSelectionManager().addSelectedGeo(video);
		}
	}

	private static void onPlayerStateChange() {
		// implement later;
	}

	/**
	 *
	 * @return if iframe is valid.
	 */
	protected native boolean isFrameValid() /*-{
		return this.contentWindow != null;
	}-*/;

	/**
	 * @return if the player is valid.
	 */
	public abstract boolean isValid();

	/**
	 * Play the video.
	 */
	public abstract void play();

	/**
	 * Pause the video.
	 */
	public abstract void pause();

	/**
	 * Sends the player background.
	 */
	public void sendBackground() {
		video.setBackground(true);
		update();
	}

	/**
	 * @param video2
	 *            other video
	 * @return whether the player is compatible with the oter video
	 */
	public abstract boolean matches(GeoVideo video2);

}

