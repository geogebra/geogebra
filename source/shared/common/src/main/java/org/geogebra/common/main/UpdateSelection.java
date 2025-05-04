package org.geogebra.common.main;

/** TODO rename to SelectionListener? */
public interface UpdateSelection {

	/**
	 * TODO rename to selectedGeosDidChange()? also, unclear what the argument means
	 *
	 * @param updateProperties whether to update the properties view
	 */
	public void updateSelection(boolean updateProperties);
}
