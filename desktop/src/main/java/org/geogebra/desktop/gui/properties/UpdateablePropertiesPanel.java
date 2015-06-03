package org.geogebra.desktop.gui.properties;

import javax.swing.JPanel;

import org.geogebra.common.kernel.geos.GeoElement;

public interface UpdateablePropertiesPanel {
	public JPanel updatePanel(Object[] geos);

	public void updateVisualStyle(GeoElement geo);

	public void setVisible(boolean flag);
}