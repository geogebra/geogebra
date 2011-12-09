package geogebra.gui.properties;

import javax.swing.JPanel;

public interface UpdateablePropertiesPanel {
	public JPanel update(Object[] geos);
	public void setVisible(boolean flag);
}