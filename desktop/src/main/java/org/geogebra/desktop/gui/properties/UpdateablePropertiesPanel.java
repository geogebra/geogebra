package org.geogebra.desktop.gui.properties;

import javax.swing.JPanel;

public interface UpdateablePropertiesPanel {
	public JPanel updatePanel(Object[] geos);

	public void setVisible(boolean flag);
}