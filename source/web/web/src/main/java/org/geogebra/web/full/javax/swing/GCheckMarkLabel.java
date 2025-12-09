/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
