package org.geogebra.web.html5.video;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.html5.video.VideoWidget.VideoListener;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Frame based HTML5 player with video tag.
 *
 * @author laszlo
 *
 */
public class HTML5Player extends VideoPlayer implements VideoListener {
	private VideoWidget v;
	private FlowPanel main;

	/**
	 * Constructor
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player.
	 */
	public HTML5Player(GeoVideo video, int id) {
		super(video, id);

	}

	@Override
	protected void createGUI() {
		main = new FlowPanel();
		v = new VideoWidget(video.getSrc(), this);
		main.add(v);
	}

	@Override
	public void update() {
		super.update();
		v.setControls(video.isBackground() || video.isReady());
	}

	@Override
	public boolean isValid() {
		return isFrameValid();
	}

	@Override
	public void play() {
		video.play();
		if (video.isPlaying()) {
			v.play();
		}
		update();
	}

	@Override
	public void pause() {
		video.pause();
		v.pause();
		update();
	}

	@Override
	public Widget asWidget() {
		return main;
	}

	@Override
	public void onLoad(int width, int height) {
		getVideo().setWidth(width);
		getVideo().setHeight(height);
		getVideo().update();
		update();
	}

	@Override
	public void onError() {
		main.clear();
		main.add(getErrorWidget());
		update();
	}

	/**
	 * @return the error widget needs to be displayed.
	 */
	protected Widget getErrorWidget() {
		return new Label(app.getLocalization().getMenuDefault("HTML5VideoAccessError",
				"Something went wrong. Please, check"
						+ "if you are online, the link exists or have permission to the video"));
	}
}
