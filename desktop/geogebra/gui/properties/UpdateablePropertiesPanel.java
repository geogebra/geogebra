package geogebra.gui.properties;

import geogebra.common.kernel.geos.GeoElement;

import javax.swing.JPanel;

public interface UpdateablePropertiesPanel {
	public JPanel update(Object[] geos);
	public void updateVisualStyle(GeoElement geo);
	public void setVisible(boolean flag);
}