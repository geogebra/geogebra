package org.geogebra.web.full.javax.swing;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * 
 * @author laszlo
 */
public class GCheckMarkLabel extends GCheckMarkPanel {

	/**
	 * 
	 * @param text
	 *            initial text for item.
	 * @param checkUrl
	 *            checkmark url.
	 * @param checked
	 *            initial state.
	 * @param cmd
	 *            Command to run.
	 */
	public GCheckMarkLabel(String text, SVGResource checkUrl, boolean checked,
			ScheduledCommand cmd) {
		super(text, checkUrl, checked, cmd);
		ClickStartHandler.init(getPanel(), new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toggle();
			}
		});
	}

	/**
	 * Checks/unchecks item.
	 */
	protected void toggle() {
		setChecked(!isChecked());
		if (getCmd() != null) {
			getCmd().execute();
		}
	}

	@Override
	protected void createContents() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void updateContents() {
		// TODO Auto-generated method stub

	}

}
