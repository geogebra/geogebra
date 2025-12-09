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

package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.shared.mow.header.NotesTopBar;

public class ToolboxDecorator {
	private final NotesToolbox toolboxMow;

	/**
	 * constructor
	 * @param toolbox - mow toolbox to decorate
	 * @param isTopBarAttached - whether it has {@link NotesTopBar} or not
	 */
	public ToolboxDecorator(NotesToolbox toolbox, boolean isTopBarAttached) {
		toolboxMow = toolbox;
		toolboxMow.addStyleName("toolboxMow");
		if (isTopBarAttached) {
			toolboxMow.addStyleName("withTopBar");
		}
	}

	/** position toolbox to the left side of screen */
	public void positionLeft() {
		toolboxMow.removeStyleName("bottomAligned");
		toolboxMow.addStyleName("leftAligned");
	}

	/** position toolbox to the bottom of screen */
	public void positionBottom() {
		toolboxMow.removeStyleName("leftAligned");
		toolboxMow.addStyleName("bottomAligned");
	}

	/**
	 * decorate toolbox according exam mode
	 * @param examActive whether the exam mode is active
	 */
	public void examMode(boolean examActive) {
		Dom.toggleClass(toolboxMow, "examMode", examActive);
	}
}
