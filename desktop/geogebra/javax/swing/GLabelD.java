package geogebra.javax.swing;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;

/**
 * Wrapper for javax.swing.JLabel
 * 
 * @author Judit Elias
 */
public class GLabelD extends geogebra.common.javax.swing.GLabel {
	private javax.swing.JLabel impl;

	/**
	 * Creates label wrapper
	 * 
	 * @param string
	 *            text of the label
	 */
	public GLabelD(String string) {
		impl = new javax.swing.JLabel(string);
	}

	/**
	 * Creates new label wrapper
	 */
	public GLabelD() {
		impl = new javax.swing.JLabel();

	}

	/**
	 * @return unwrapped label
	 */
	public javax.swing.JLabel getImpl() {
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
		impl.setFont(geogebra.awt.GFontD.getAwtFont(font));

	}

	@Override
	public void setForeground(GColor color) {
		impl.setForeground(geogebra.awt.GColorD.getAwtColor(color));

	}

	@Override
	public void setBackground(GColor color) {
		impl.setBackground(geogebra.awt.GColorD.getAwtColor(color));

	}
}
