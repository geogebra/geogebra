package geogebra.gui.util;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Custom toggle button for use in stylebars
 * @author G. Sturr
 *
 */
public class MyToggleButton extends JButton {

	private static final long serialVersionUID = 1L;
	private int myHeight;

	public MyToggleButton(ImageIcon icon, int height) {
		super(icon);
		initButton(height);

		Dimension d = new Dimension(icon.getIconWidth(), height);
		setIcon(GeoGebraIcon.ensureIconSize(icon, d));
	}

	public MyToggleButton(int height) {
		super();
		initButton(height);
	}

	private void initButton(int height) {

		this.myHeight = height;
		this.setRolloverEnabled(true);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				toggle();
			}
		});
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		Dimension d = this.getPreferredSize();
		d.height = myHeight;
		this.setPreferredSize(d);
	}

	public void update(Object[] geos) {
	}

	private void toggle() {
		this.setSelected(!this.isSelected());
	}
}