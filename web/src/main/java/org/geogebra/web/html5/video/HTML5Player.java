package org.geogebra.web.html5.video;

import org.geogebra.common.kernel.geos.GeoVideo;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Frame based HTML5 player with video tag.
 *
 * @author laszlo
 *
 */
public class HTML5Player extends VideoPlayer {
	private Element videoElem;
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
		createVideoElement();
	}

	@Override
	public void update() {
		if (isUpdateContent()) {
			setContentDeferred(main);
		}
		super.update();
	}

	private void createVideoElement() {
		videoElem = DOM.createElement("video");
		videoElem.setAttribute("src", video.getSrc());
		videoElem.setAttribute("controls", "");
		videoElem.setAttribute("autoplay", "");
		main = new FlowPanel();
		main.getElement().appendChild(videoElem);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	public boolean isUpdateContent() {
		return updateContent;
	}

	public void setUpdateContent(boolean updateContent) {
		this.updateContent = updateContent;
	}

}
