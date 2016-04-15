package org.geogebra.desktop.javax.swing;

import java.awt.Component;

import javax.swing.JComboBox;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.event.ActionListener;
import org.geogebra.common.javax.swing.AbstractJComboBox;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.euclidian.event.ActionListenerD;

/**
 * Wrapper for javax.swing.Box
 * 
 * @author Judit Elias, Michael
 */
public class GComboBoxD extends AbstractJComboBox {

	private JComboBox impl = null;

	int selectedIndex = -1;

	/**
	 * Creates new wrapper Box
	 */
	public GComboBoxD() {
		this.impl = new JComboBox();
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
		impl.addActionListener((ActionListenerD) newActionListener);
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
