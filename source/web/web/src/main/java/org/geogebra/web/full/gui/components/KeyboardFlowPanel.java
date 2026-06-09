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

package org.geogebra.web.full.gui.components;

import javax.annotation.CheckForNull;

import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.accessibility.HasFocus;
import org.gwtproject.user.client.ui.FlowPanel;

public class KeyboardFlowPanel extends FlowPanel
		implements HasKeyboardPopup, HasFocus {
	private @CheckForNull Runnable focusDelegate;

	@Override
	public void focus() {
		if (focusDelegate != null) {
			focusDelegate.run();
		}
	}

	/**
	 * @param focusDelegate to be called when panel is focused
	 */
	public void setFocusDelegate(@CheckForNull Runnable focusDelegate) {
		this.focusDelegate = focusDelegate;
	}
}
