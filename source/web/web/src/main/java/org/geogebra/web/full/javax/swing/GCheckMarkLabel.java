package org.geogebra.web.full.javax.swing;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;

public class GCheckMarkLabel extends GCheckMarkPanel {

	/**
	 * @param text initial text for item.
	 * @param checked initial state
	 * @param cmd command to run
	 */
	public GCheckMarkLabel(String text, boolean checked, ScheduledCommand cmd) {
		super(text, null, checked);
		ClickStartHandler.init(this, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setChecked(!isChecked());
				cmd.execute();
			}
		});
	}

}
