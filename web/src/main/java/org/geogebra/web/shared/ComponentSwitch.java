package org.geogebra.web.shared;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author csilla
 * 
 *         material design switch component
 *
 */
public class ComponentSwitch extends FlowPanel {

	private SimplePanel track;
	private SimplePanel thumb;

	/**
	 * 
	 */
	public ComponentSwitch() {
		this.addStyleName("switch");
		track = new SimplePanel();
		track.addStyleName("track");
		thumb = new SimplePanel();
		thumb.addStyleName("thumb");
		this.add(track);
		this.add(thumb);
	}
}
