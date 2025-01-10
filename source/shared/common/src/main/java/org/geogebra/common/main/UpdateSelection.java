package org.geogebra.common.main;

// TODO rename to SelectionListener?
public interface UpdateSelection {

	// TODO rename to selectedGeosDidChange()? also, unclear what the argument means
	public void updateSelection(boolean updateProperties);
}
