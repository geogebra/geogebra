package org.geogebra.desktop.gui.util;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Custom toggle button for use in stylebars
 * 
 * @author G. Sturr
 *
 */
public class ToggleButtonD extends JButton {

	private static final long serialVersionUID = 1L;
	private int myHeight;

	/**
	 * @param icon icon
	 * @param height height
	 */
	public ToggleButtonD(ImageIcon icon, int height) {
		super(icon);
		initButton(height);

		Dimension d = new Dimension(icon.getIconWidth(), height);
		setIcon(GeoGebraIconD.ensureIconSize(icon, d));
	}

	/**
	 * @param height height in pixels
	 */
	public ToggleButtonD(int height) {
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

	public void update(List<GeoElement> geos) {
		// override
	}

	protected void toggle() {
		this.setSelected(!this.isSelected());
	}
}
