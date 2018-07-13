package org.geogebra.web.html5.video;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.html5.video.VideoWidget.VideoListener;

import com.google.gwt.user.client.ui.FlowPanel;
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
	private boolean updateContent = true;

	/**
	 * Constructor
	 *
	 * @param video
	 *            s * the video object.
	 * @param id
	 *            The id of the player frame.
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

	/**
	 *
	 * @return if frame content needs to be updated.
	 */
	public boolean isUpdateContent() {
		return updateContent;
	}

	/**
	 * Set it to true if frame content needs to be updated.
	 *
	 * @param updateContent
	 *            to set.
	 */
	public void setUpdateContent(boolean updateContent) {
		this.updateContent = updateContent;
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

}
