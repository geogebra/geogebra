package org.geogebra.common.gui.menu;

import javax.annotation.Nullable;
import java.util.List;

public interface MenuItemGroup {

	@Nullable String getTitle();

	List<MenuItem> getMenuItems();
}
