/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.dialog.options;

import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Interface for option panels
 * 
 * @author mathieu
 *
 */
public interface OptionPanelD {
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	public void updateGUI();

	/**
	 * JPanel method
	 */
	public void revalidate();

	/**
	 * JPanel method
	 * 
	 * @param border
	 *            border
	 */
	public void setBorder(Border border);

	/**
	 * @return the wrapped JPanel for Desktop
	 */
	public JPanel getWrappedPanel();

	/**
	 * apply modifications. should be called when the panel is hidden.
	 */
	public void applyModifications();

	/**
	 * update the font
	 */
	public void updateFont();

	/**
	 * set if this panel is selected or not (used for update)
	 * 
	 * @param flag
	 *            selected or not
	 */
	public void setSelected(boolean flag);

}
