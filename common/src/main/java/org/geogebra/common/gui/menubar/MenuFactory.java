package org.geogebra.common.gui.menubar;

public interface MenuFactory {

	RadioButtonMenuBar newSubmenu();

	public void addMenuItem(MenuInterface parentMenu, String key,
			boolean asHtml, MenuInterface subMenu);

}
