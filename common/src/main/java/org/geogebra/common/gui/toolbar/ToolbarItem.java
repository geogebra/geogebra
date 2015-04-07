package org.geogebra.common.gui.toolbar;

import java.util.Vector;

public class ToolbarItem {
	private Vector<Integer> menu;
	private Integer mode;

	public ToolbarItem(Vector<Integer> menu) {
		this.menu = menu;
		this.mode = null;
	}

	public ToolbarItem(Integer mode) {
		this.mode = mode;
	}

	public Vector<Integer> getMenu() {
		return menu;
	}

	public Integer getMode() {
		return mode;
	}

}
