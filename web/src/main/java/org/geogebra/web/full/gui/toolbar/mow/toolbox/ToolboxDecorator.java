package org.geogebra.web.full.gui.toolbar.mow.toolbox;

public class ToolboxDecorator {
	private final ToolboxMow toolboxMow;

	public ToolboxDecorator(ToolboxMow toolbox) {
		this.toolboxMow = toolbox;
	}

	public void positionLeft() {
		toolboxMow.removeStyleName("bottomAligned");
		toolboxMow.addStyleName("leftAligned");
	}

	public void positionBottom() {
		toolboxMow.removeStyleName("leftAligned");
		toolboxMow.addStyleName("bottomAligned");
	}
}
