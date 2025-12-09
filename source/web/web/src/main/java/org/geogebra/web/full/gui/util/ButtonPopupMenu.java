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

package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.FocusPanel;
import org.gwtproject.user.client.ui.Panel;

public class ButtonPopupMenu extends GPopupPanel implements HasKeyboardPopup {
	
	private final FocusPanel container;
	private final FlowPanel panel;

	/**
	 * @param root
	 *            root for popup
	 * @param app
	 *            application
	 */
	public ButtonPopupMenu(Panel root, App app) {
		super(root, app);
		container = new FocusPanel();
		panel = new FlowPanel();
		container.add(panel);
		container.addStyleName("ButtonPopupMenu");
		container.addKeyUpHandler(event -> {
			if (event.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
				hide();
			}
		});
		add(container);
	}
	
	public FlowPanel getPanel() {
		return panel;
	}

	public FocusPanel getFocusPanel() {
		return container;
	}

}
