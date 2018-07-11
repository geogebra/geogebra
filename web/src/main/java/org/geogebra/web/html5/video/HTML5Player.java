package org.geogebra.web.html5.video;

import org.geogebra.common.kernel.geos.GeoVideo;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Frame based HTML5 player with video tag.
 *
 * @author laszlo
 *
 */
public class HTML5Player extends VideoPlayer {
	private VideoWidget v;
	private FlowPanel main;
	private boolean updateContent = true;

	/**
	 * Constructor
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player frame.
	 */
	public HTML5Player(GeoVideo video, int id) {
		super(video, "about:blank", id);
		createGUI();
	}

	private void createGUI() {
		main = new FlowPanel();
		v = new VideoWidget(video.getSrc());
		main.add(v);
	}

	@Override
	public void update() {
		super.update();
		if (isUpdateContent()) {
			setContentDeferred(main);
		}
		setWidth(getVideo().getWidth());
		setHeight(getVideo().getHeight());
		v.setControls(video.isBackground() ? true : video.isReady());

	}

	private void setHeight(int h) {
		setHeight(h + "px");
		v.setHeight(h);
	}

	private void setWidth(int w) {
		setWidth(w + "px");
		v.setWidth(w);
	}

	/**
	 * Sets the content HTML of the frame.
	 *
	 * @param content
	 *            the HTML to set.
	 */
	native boolean setContent(String content) /*-{
		var frame = $doc.querySelector('iframe');
		if (frame != null && frame.contentDocument == null) {
			return false;
		}
		frame.contentDocument.write(content);
		return true;
	}-*/;

	private void setContentDeferred(final Widget w) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (setContent(w.getElement().getInnerHTML())) {
					setUpdateContent(false);
				}
			}
		});
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

}
