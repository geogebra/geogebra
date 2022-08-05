package org.geogebra.desktop.gui.menubar;

import org.geogebra.common.gui.menubar.MenuInterface;

public interface MenuFactory {

	RadioButtonMenuBarD newSubmenu();

	public void addMenuItem(MenuInterface parentMenu, String key,
			boolean asHtml, MenuInterface subMenu);

}
