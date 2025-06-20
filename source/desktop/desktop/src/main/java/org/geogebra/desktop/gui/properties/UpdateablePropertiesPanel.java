package org.geogebra.desktop.gui.properties;

import javax.swing.JPanel;

public interface UpdateablePropertiesPanel {
	/**
	 * @param geos selected geos
	 * @return self if OK, null otherwise
	 */
	JPanel updatePanel(Object[] geos);

	/**
	 * Show or hide the panel.
	 * @param flag whether to sho this
	 */
	void setVisible(boolean flag);
}