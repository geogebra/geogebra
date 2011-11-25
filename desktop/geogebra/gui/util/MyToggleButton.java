package geogebra.gui.util;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MyToggleButton extends JButton {

	public MyToggleButton(ImageIcon icon, int iconHeight) {
		super(icon);
		Dimension d = new Dimension(icon.getIconWidth(), iconHeight);
		setIcon(GeoGebraIcon.ensureIconSize(icon, d));
		this.setRolloverEnabled(true);
	}

	public void update(Object[] geos) {
	}

	public void toggle() {
		this.setSelected(!this.isSelected());
	}
}