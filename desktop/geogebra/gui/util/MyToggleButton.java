package geogebra.gui.util;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MyToggleButton extends JButton {

	private static final long serialVersionUID = 1L;

	public MyToggleButton(ImageIcon icon, int iconHeight) {
		super(icon);
		Dimension d = new Dimension(icon.getIconWidth(), iconHeight);
		setIcon(GeoGebraIcon.ensureIconSize(icon, d));
		this.setRolloverEnabled(true);
		
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				toggle();
			}
		});		
	}

	public void update(Object[] geos) {
	}

	private void toggle() {
		this.setSelected(!this.isSelected());
	}
}