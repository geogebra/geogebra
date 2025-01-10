package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;

public abstract class DefaultMenuAction<T> implements MenuAction<T> {

	@Override
	public boolean isAvailable(T item) {
		return true;
	}
}
