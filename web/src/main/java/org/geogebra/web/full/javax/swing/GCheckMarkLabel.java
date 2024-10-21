package org.geogebra.web.full.javax.swing;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;

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
		super(text, null, checkUrl, checked);
		ClickStartHandler.init(this, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setChecked(!isChecked());
				cmd.execute();
			}
		});
	}

}
