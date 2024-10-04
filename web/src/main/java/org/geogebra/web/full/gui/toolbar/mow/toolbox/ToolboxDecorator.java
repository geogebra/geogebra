package org.geogebra.web.full.gui.toolbar.mow.toolbox;

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

	/**
	position toolbox to the left side of screen
	 */
	public void positionLeft() {
		toolboxMow.removeStyleName("bottomAligned");
		toolboxMow.addStyleName("leftAligned");
	}

	/**
	 position toolbox to the bottom of screen
	 */
	public void positionBottom() {
		toolboxMow.removeStyleName("leftAligned");
		toolboxMow.addStyleName("bottomAligned");
	}
}
