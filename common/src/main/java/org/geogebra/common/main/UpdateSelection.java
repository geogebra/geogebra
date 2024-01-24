package org.geogebra.common.main;

// TODO rename to SelectionListener?
public interface UpdateSelection {

	// TODO rename to selectionDidChange? also, find a better name for the argument
	public void updateSelection(boolean updateProperties);
}
