package org.geogebra.web.full.gui.toolbar.mow.toolbox;

public class ToolboxDecorator {
	private final ToolboxMow toolboxMow;

	public ToolboxDecorator(ToolboxMow toolbox) {
		this.toolboxMow = toolbox;
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
