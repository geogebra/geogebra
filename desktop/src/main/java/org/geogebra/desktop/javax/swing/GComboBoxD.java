package org.geogebra.desktop.javax.swing;

import java.awt.Component;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.event.ActionListener;
import org.geogebra.common.javax.swing.AbstractJComboBox;

/**
 * Wrapper for javax.swing.Box
 * 
 * @author Judit Elias, Michael
 */
public class GComboBoxD extends org.geogebra.common.javax.swing.AbstractJComboBox {

	private javax.swing.JComboBox impl = null;

	int selectedIndex = -1;

	/**
	 * Creates new wrapper Box
	 */
	public GComboBoxD() {
		this.impl = new javax.swing.JComboBox();
	}

	@Override
	public void setVisible(boolean b) {
		impl.setVisible(b);

	}

	@Override
	public Object getItemAt(int i) {
		return impl.getItemAt(i);
	}

	@Override
	public void setFont(GFont font) {
		impl.setFont(org.geogebra.desktop.awt.GFontD.getAwtFont(font));

	}

	@Override
	public void setForeground(GColor color) {
		impl.setForeground(org.geogebra.desktop.awt.GColorD.getAwtColor(color));

	}

	@Override
	public void setBackground(GColor color) {
		impl.setBackground(org.geogebra.desktop.awt.GColorD.getAwtColor(color));
	}

	@Override
	public void setFocusable(boolean b) {
		impl.setFocusable(b);
	}

	@Override
	public void setEditable(boolean b) {
		impl.setEditable(b);

	}

	@Override
	public void addItem(String string) {
		impl.addItem(string);

	}

	@Override
	public void setSelectedIndex(int selectedIndex) {
		// if (selectedIndex < impl.getItemCount())

		impl.setSelectedIndex(selectedIndex);

	}

	@Override
	public int getSelectedIndex() {
		return impl.getSelectedIndex();
	}

	public static Component getJComboBox(AbstractJComboBox comboBox) {
		if (!(comboBox instanceof GComboBoxD))
			return null;
		return ((GComboBoxD) comboBox).impl;
	}

	@Override
	public void addActionListener(ActionListener newActionListener) {
		impl.addActionListener((org.geogebra.desktop.euclidian.event.ActionListenerD) newActionListener);
	}

	@Override
	public void removeAllItems() {
		impl.removeAllItems();
	}

	@Override
	public int getItemCount() {
		return impl.getItemCount();
	}

}
