package org.geogebra.web.full.gui.toolbar.mow.toolbox;

public class ToolboxDecorator {
	private final ToolboxMow toolboxMow;

	/**
	 * constructor
	 * @param toolbox - mow toolbox to decorate
	 */
	public ToolboxDecorator(ToolboxMow toolbox) {
		toolboxMow = toolbox;
		toolboxMow.addStyleName("toolboxMow");
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
