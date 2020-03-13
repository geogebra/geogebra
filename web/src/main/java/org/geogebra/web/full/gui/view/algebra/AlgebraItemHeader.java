package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.SetLabels;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Header for each algebra view item
 */
public interface AlgebraItemHeader extends IsWidget, SetLabels {

	/**
	 * @return width in pixels
	 */
	int getOffsetWidth();

	/**
	 * Show the right content (+, warning, number or marble)
	 * 
	 * @param warning
	 *            whether to show warning
	 */
	void updateIcons(boolean warning);

	/**
	 * Update marble to match visibility
	 */
	void update();

	/**
	 * @param x
	 *            pointer event x-coord
	 * @param y
	 *            pointer event y-coord
	 * @return whether header was hit
	 */
	boolean isHit(int x, int y);

	/**
	 * Set number (starting from 1)
	 * 
	 * @param index
	 *            index in AV
	 */
	void setIndex(int index);

	void setError(String s);

	int getOffsetHeight();

	int getAbsoluteTop();

	int getAbsoluteLeft();
}
