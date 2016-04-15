package org.geogebra.desktop.javax.swing;

import javax.swing.JLabel;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.javax.swing.GLabel;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GFontD;

/**
 * Wrapper for javax.swing.JLabel
 * 
 * @author Judit Elias
 */
public class GLabelD extends GLabel {
	private JLabel impl;

	/**
	 * Creates label wrapper
	 * 
	 * @param string
	 *            text of the label
	 */
	public GLabelD(String string) {
		impl = new JLabel(string);
	}

	/**
	 * Creates new label wrapper
	 */
	public GLabelD() {
		impl = new JLabel();

	}

	/**
	 * @return unwrapped label
	 */
	public JLabel getImpl() {
		return impl;
	}

	@Override
	public void setVisible(boolean b) {
		impl.setVisible(b);

	}

	@Override
	public void setText(String string) {
		impl.setText(string);

	}

	@Override
	public void setOpaque(boolean b) {
		impl.setOpaque(b);

	}

	@Override
	public void setFont(GFont font) {
		impl.setFont(GFontD.getAwtFont(font));

	}

	@Override
	public void setForeground(GColor color) {
		impl.setForeground(GColorD.getAwtColor(color));

	}

	@Override
	public void setBackground(GColor color) {
		impl.setBackground(GColorD.getAwtColor(color));

	}
}
