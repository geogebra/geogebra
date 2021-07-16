package org.geogebra.common.gui;

import org.geogebra.common.kernel.View;

public interface Editing extends View {
	public void cancelEditItem();

	/**
	 * Finish editing and update currently edited item
	 * 
	 * @param unselectAll
	 *            whether to unselect other items
	 */
	public void resetItems(boolean unselectAll);

	/**
	 * @return whether this view is visible
	 */
	public boolean isShowing();

}
