package org.geogebra.web.full.gui.menubar;

public abstract class DefaultMenuAction<T> implements MenuAction<T> {

	@Override
	public boolean isAvailable(T item) {
		return true;
	}
}
