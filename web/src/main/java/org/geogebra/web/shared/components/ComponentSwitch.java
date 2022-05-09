package org.geogebra.web.shared.components;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;

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
	private boolean isSwitchOn;
	private AsyncOperation<Boolean> callback;

	/**
	 * @param switchOn
	 *            true if switch is on by default
	 * @param callback
	 *            function to update UI on switch update
	 * 
	 */
	public ComponentSwitch(boolean switchOn, AsyncOperation<Boolean> callback) {
		this.isSwitchOn = switchOn;
		this.callback = callback;
		this.addStyleName("switch");
		this.addStyleName(switchOn ? "on" : "off");
		track = new SimplePanel();
		track.addStyleName("track");
		thumb = new SimplePanel();
		thumb.addStyleName("thumb");
		this.add(track);
		this.add(thumb);
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setSwitchOn(!isSwitchOn());
				updateSwitchStyle();
			}
		});
	}

	/**
	 * @return true if switch is on
	 */
	public boolean isSwitchOn() {
		return isSwitchOn;
	}

	/**
	 * @param isSwitchOn
	 *            true if switch is on
	 */
	public void setSwitchOn(boolean isSwitchOn) {
		this.isSwitchOn = isSwitchOn;
	}

	/**
	 * update style of switch depending on its status (on/off)
	 */
	public void updateSwitchStyle() {
		Dom.toggleClass(this, "on", "off", isSwitchOn());
		runCallback();
	}

	/**
	 * run callback function
	 */
	public void runCallback() {
		if (callback != null) {
			callback.callback(isSwitchOn());
		}
	}
}
