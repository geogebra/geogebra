package org.geogebra.common.gui.menu;

import java.util.List;

public interface SubmenuItem extends MenuItem {

	List<ActionableItem> getItems();
}
